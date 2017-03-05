
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Testing {
    static OpenCVFrameConverter.ToMat FrameToMatConverter = new OpenCVFrameConverter.ToMat();

    public static void main(String[] args) throws java.lang.Exception {
        if (System.getProperty("debug") != null) {
            System.err.print("\nWaiting for first connection...\n");
            for (int i = 10; i > 0; i--) {
                System.err.printf(" %d...", i);
                Thread.sleep(1000);

            }
        }
        try {a();} catch (Exception e) {
            e.printStackTrace();
            System.exit(42);
        }
    }

    static FrameGrabber grabber;

    public static void a() throws java.lang.Exception {
        System.err.print("\nStarting execution");
        String videoLocation = findVideoDevices().get(0).getAbsolutePath();
        Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c brightness=30");
        Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c contrast=6");
        Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c saturation=100");
        Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c white_balance_temperature_auto=0");
        Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c white_balance_temperature=6500");
        Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c exposure_auto=1");
        //Runtime.getRuntime().exec("v4l2-ctl --device "+videoLocation+" -c exposure=156");

        System.err.print("\nOpening Camera...");
        grabber = new FFmpegFrameGrabber(videoLocation);
        grabber.setImageWidth(424);
        grabber.setImageHeight(240);
        //grabber.setFormat("video4linux2");
        //grabber.setFrameRate(1);
        //grabber.setSampleRate(1);
        //grabber.setNumBuffers(1);
        //grabber = new IPCameraFrameGrabber("http://10.3.65.11/live/0/mjpeg.jpg");
        System.err.print("Init...");
        grabber.start();
        System.err.print("Done!");

        System.err.print("\nChecking arch..");
        GpioController gpio = null;
        GpioPinDigitalOutput outpin = null;

        if (System.getProperty("nogpio") != null) {
            System.err.print("I don't think we are pi");
        } else {
            System.err.print("I Think we are pi");
            gpio = GpioFactory.getInstance();
            outpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
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
        MJPEGServer server1 = new MJPEGServer(8000);
        server1.start();
        System.err.print("1,");
        MJPEGServer server2 = new MJPEGServer(8001);
        server2.start();
        System.err.print("2,");
        MJPEGServer server3 = new MJPEGServer(8002);
        server3.start();
        System.err.print("and 3...");

        System.err.print("\nStarting encoders...");
        EncoderThread encoder1 = new EncoderThread();
        encoder1.start();

        System.err.print("\nStarting the main loop...");
        int frameCycleIndex = 0;
        long frameProfileCounter = 0;
        long start = System.currentTimeMillis();
        int frameProfileLength = 600;
        do {
            frameProfileCounter = (frameProfileCounter + 1) % 600;
            if (frameProfileCounter == 0) {
                long end = System.currentTimeMillis();
                double dTime = (end - start) / 1000.0;
                System.out.printf(
                        "\n%d frames at %f frames per second",
                        frameProfileLength,
                        frameProfileLength / dTime
                );
                start = end;
            }
            //System.err.print("\nGrabbin...");
            Frame frame = ((FFmpegFrameGrabber)grabber).grabImage();
            //System.err.print("Printin...");

            opencv_core.Mat m = new opencv_core.Mat();
            FrameToMatConverter.convert(frame).copyTo(m);
            encoder1.setWorkItem(m);
            //c1.showImage(frame);
            if (encoder1.outputImage == null) continue;
            byte[] image = encoder1.outputImage.getStringBytes();

            //c1.showImage(frame);

            //System.err.print("Streamin...");
            server1.streamData(image);


            switch (frameCycleIndex++) {
                case 0:
                    if (outpin != null) outpin.low();
                    break;

                    case 1:
                    //System.err.print("Doin it again!");
                    //c2.showImage(frame);
                    server2.streamData(image);
                    break;
                case 2:
                    if (outpin != null) outpin.high();
                    break;
                case 3:
                    //System.err.print("Doin it again!");
                    //c3.showImage(frame);
                    server3.streamData(image);
                    break;
            }
            frameCycleIndex %= 4;

        } while (frameCycleIndex != 99
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

    public static List<File> findVideoDevices() {
        File[] filelist = new File("/dev/").listFiles((dir, name) -> name.matches("^video\\d+"));
        return Arrays.asList(filelist);
    }


}