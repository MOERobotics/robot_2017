package testpackage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class Capture_Rebroadcast_Stream {
	
	static FrameGrabber grabber = new OpenCVFrameGrabber(0);
	static FrameGrabber grabber2 = new OpenCVFrameGrabber(1);
	static CanvasFrame c1 = new CanvasFrame("cam1");
	static CanvasFrame c2 = new CanvasFrame("cam2");
	static Frame frame1;
	static Frame frame2;
	public static void main(String[] args) throws Exception, IOException, InterruptedException{
		grabber.start();
		grabber2.start();
		MJPEGserver server1 = new MJPEGserver(8001);
		MJPEGserver server2 = new MJPEGserver(8002);
		server1.start();
		server2.start();
		
		while(true){
			frame1 = grabber.grab();
			frame2 = grabber2.grab();
			
			c1.showImage(frame1);
			c2.showImage(frame2);
			server1.streamFrame(frame1);
			server2.streamFrame(frame2);
			
		}
	}
}

