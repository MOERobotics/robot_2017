import org.bytedeco.javacv.*;

/**
 * Created by kevok on 2/9/17.
 */
public class TwoCameraTest {
    public static void main(String[] args) throws java.lang.Exception {
        try {a();}
        catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            System.exit(42);
        }
    }
    public static void a() throws java.lang.Exception{
        System.err.print("\nStarting execution");
        FrameGrabber grabber1, grabber2;

        System.err.print("\nOpening Camera 1...");
        grabber1 = new FFmpegFrameGrabber("/dev/video0");
        System.err.print("Init...");
        grabber1.start();
        System.err.print("Done!");

        System.err.print("\nOpening Camera 2...");
        grabber2 = new FFmpegFrameGrabber("/dev/video1");
        System.err.print("Init...");
        grabber2.start();
        System.err.print("Done!");

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


        System.err.print("\nStarting the main loop...");
        int frameCycleIndex = 0;
        do {
            System.err.print("\nGrabbin...");
            Frame frame1 = grabber1.grab();

            System.err.print("Printin...");
            //c1.showImage(frame1);

            System.err.print("Streamin...");
            server1.streamFrame(frame1);

            System.err.print("Doin it again!");

            System.err.print("\nGrabbin...");
            Frame frame2 = grabber2.grab();

            System.err.print("Printin...");
            //c2.showImage(frame2);

            System.err.print("Streamin...");
            server1.streamFrame(frame2);

            Thread.sleep(10);


        } while ( frameCycleIndex != 99 );
        System.err.print("\nBRB dying");

        //c1.dispose();
        //c2.dispose();
        //c3.dispose();
        server1.stop();
        server2.stop();
        grabber1.stop();
        grabber2.stop();
    }
}
