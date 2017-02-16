
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import javax.swing.WindowConstants;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.impl.GpioPinImpl;
import com.pi4j.system.SystemInfo;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;
import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avutil.*;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2RGB;
import static org.bytedeco.javacpp.swscale.*;

public class Testing {
	static Scanner input = new Scanner(System.in);
	static String userChoice;
    static OpenCVFrameConverter.ToMat FrameToMatConverter = new OpenCVFrameConverter.ToMat();
	public static void main(String[] args) throws java.lang.Exception {
        if (System.getProperty("debug") != null) {
            System.err.print("\nWaiting for first connection...\n");
            for (int i = 10; i > 0; i--) {
                System.err.printf(" %d...", i);
                Thread.sleep(1000);

            }
        }
		try {a();}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(42);
		}
	}
	static FrameGrabber grabber;
	public static void a() throws java.lang.Exception{
	    System.err.print("\nStarting execution");

        System.err.print("\nOpening Camera...");
        avcodec.avcodec_register_all();
        avcodec.AVCodec avc = avcodec.avcodec_find_encoder(avcodec.AV_CODEC_ID_JPEG2000);

        grabber = new OpenCVFrameGrabber(0);
        //grabber.setFormat("video4linux2");
        //grabber.setFrameRate(1);
        //grabber.setSampleRate(1);
        //grabber.setNumBuffers(1);
        //grabber = new IPCameraFrameGrabber("http://10.3.65.11/live/0/mjpeg.jpg");
        System.err.print("Init...");
        grabber.start();
        System.err.print("Done!");

        System.err.print("\nChecking arch..");
        String arch = SystemInfo.getOsArch();
        GpioController gpio = null;
        GpioPinDigitalOutput outpin = null;

        if (System.getProperty("nogpio") != null) {
            System.err.print("I don't think we are pi");
        } else {
            System.err.print("I Think we are pi");
            gpio = GpioFactory.getInstance();
            outpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        }

        /*
        System.err.print("\nCreating Canvases...");
		CanvasFrame c1 = new CanvasFrame("debugOutput1");
		c1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.err.print("1,");
        CanvasFrame c2 = new CanvasFrame("debugOutput2");
        c1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.err.print("2,");
        CanvasFrame c3 = new CanvasFrame("debugOutput3");
        c1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.err.print("and 3...");
        */


        System.err.print("\nStarting servers...");
		MJPEGserver server1 = new MJPEGserver(8000);
		server1.start();
        System.err.print("1,");
        MJPEGserver server2 = new MJPEGserver(8001);
        server2.start();
        System.err.print("2,");
        MJPEGserver server3 = new MJPEGserver(8002);
        server3.start();
        System.err.print("and 3...");


        System.err.print("\nStarting the main loop...");
        int frameCycleIndex = 0;
		do {
            System.err.print("\nGrabbin...");
			Frame frame = grabber.grab();
            System.err.print("Printin...");
            opencv_core.Mat mat = FrameToMatConverter.convert(frame);
            opencv_core.Mat rgb_mat = new opencv_core.Mat();

            opencv_imgproc.cvtColor(mat,rgb_mat,CV_BGR2RGB);

            AVPicture avp = new AVPicture();
            avpicture_fill(avp,rgb_mat.data(),AV_PIX_FMT_RGB24,480,320);
            SwsContext sws_cx = sws_getContext(
                    480,320,AV_PIX_FMT_RGB24,
                    480,320,AV_PIX_FMT_YUV420P,
                    SWS_FAST_BILINEAR,null,null,(DoublePointer)null
            );

            AVFrame avf = av_frame_alloc()
                    .format(AV_PIX_FMT_YUV420P)
                    .width(480)
                    .height(320);



            sws_scale(
                    sws_cx,
                    avp.data(),avp.linesize(),
                    0,420,
                    avf.data(),avf.linesize()
            );

            avcodec.AVCodecContext avc_cx =
                avcodec.avcodec_alloc_context3(avc)
                    .bit_rate(400000)
                    .width(480)
                    .height(320)
                    .time_base(avutil.av_make_q(1,25))
                    .pix_fmt(AV_PIX_FMT_YUV420P);
            avcodec.avcodec_open2(avc_cx,avc,(PointerPointer)null);

            AVPacket avpk = new AVPacket()
                .size(0)
                .data(null);
            av_init_packet(avpk);

            avcodec_encode_video2(avc_cx,avpk,avf,new int[0]);

            byte[] img = avp.asByteBuffer().array();

			//c1.showImage(frame);

            System.err.print("Streamin...");
            server1.streamData(img);


            switch (frameCycleIndex++) {
                case 0:
                    if (outpin != null) outpin.low();
                    break;
                case 1:
                    System.err.print("Doin it again!");
                    //c2.showImage(frame);
                    server2.streamData(img);
                    break;
                case 2:
                    if (outpin != null) outpin.high();
                    break;
                case 3:
                    System.err.print("Doin it again!");
                    //c3.showImage(frame);
                    server3.streamData(img);
                    break;
            }
            frameCycleIndex %=4;

		} while ( frameCycleIndex != 99
            //c1.isDisplayable() &&
            //c2.isDisplayable() &&
            //c3.isDisplayable()
        );
        System.err.print("\nBRB dying\n");
		
		//c1.dispose();
        //c2.dispose();
        //c3.dispose();
		server1.stop();
        server2.stop();
        server3.stop();
		grabber.stop();
	}
	
	
	
}
