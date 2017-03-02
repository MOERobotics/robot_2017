import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.system.SystemInfo;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;

import java.io.File;
import java.util.*;

import static org.bytedeco.javacpp.avcodec.avpicture_get_size;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_RGB24;

public class SingleCameraSingleStream {
    static OpenCVFrameConverter.ToMat FrameToMatConverter = new OpenCVFrameConverter.ToMat();
	public static void main(String[] args) {
		try {a();}
		catch (java.lang.Exception e) {
			e.printStackTrace();
			System.exit(42);
		}
	}
	static FFmpegFrameGrabber grabber;
	public static void a() throws java.lang.Exception{
	    System.err.print("\nStarting execution");

        System.err.print("\nOpening Camera...");
        String videoLocation = findVideoDevices().get(0).getAbsolutePath();

        grabber = new FFmpegFrameGrabber("/dev/video0");
        grabber.setImageHeight(320);
        grabber.setImageWidth(480);
        grabber.setFrameRate(30);

        System.err.print("Init...");
        grabber.start();
        System.out.println(grabber.getFormat());
        System.out.println(grabber.getVideoCodec());

        System.err.print("Done!");

        /*
        System.err.print("\nCreating Canvases...");
		CanvasFrame c1 = new CanvasFrame("debugOutput1");
		c1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		*/

        System.err.print("\nStarting servers...");
		MJPEGserver server1 = new MJPEGserver(8000);
		server1.start();
        System.err.print("1,");
        /*
        MJPEGserver server2 = new MJPEGserver(8001);
        server2.start();
        System.err.print("2,");
        MJPEGserver server3 = new MJPEGserver(8002);
        server3.start();
        System.err.print("and 3...");
        */


        System.err.print("\nStarting encoders...");
        EncoderThread encoder1 = new EncoderThread();
        encoder1.start();

        System.err.print("\nStarting the main loop...");
        long iter = 0;
        long start = System.currentTimeMillis();
        int frameProfileLength = 600;
		do {
		    if ((iter = ((iter + 1) % 600)) == 0) {
		        long end = System.currentTimeMillis();
		        double dTime = (end - start)/1000.0;
		        System.out.printf(
                    "\n%d frames at %f frames per second",
                    frameProfileLength,
                    frameProfileLength/dTime
                );
		        start = end;
            }
            //System.err.print("\nGrabbin...");
			Frame frame = grabber.grab();
            //System.err.print("Printin...");

            opencv_core.Mat m = new opencv_core.Mat();
            FrameToMatConverter.convert(frame).copyTo(m);
            encoder1.setWorkItem(m);
            //c1.showImage(frame);
            BytePointer image = encoder1.outputImage;

            //System.err.print("Streamin...");
            if (image != null)
                server1.streamData(image.getStringBytes());



		} while ( server1 != null
            //c1.isDisplayable() &&
            //c2.isDisplayable() &&
            //c3.isDisplayable()
        );
        System.err.print("\nBRB dying\n");
		
		//c1.dispose();
		server1.stop();
		grabber.stop();
	}

	public static List<File> findVideoDevices() {
	    File[] filelist = new File("/dev/").listFiles((dir, name) -> name.matches("^video\\d+"));
	    return Arrays.asList(filelist);
    }
	
	
	
}
