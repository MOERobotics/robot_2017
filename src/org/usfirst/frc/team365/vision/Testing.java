package org.usfirst.frc.team365.vision;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.IPCameraFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Testing {
	static Scanner input = new Scanner(System.in);
	static String userChoice;
	public static void main(String[] args) throws IOException, Exception {
		
		
		System.out.println("What what you like to test? (choose a letter)");
		System.out.println("a: GearVision");
		userChoice = input.next();
		System.out.println("----------------------------------------------------------------");
		
		switch (userChoice){
		case "a":
			a();
			break;
		case "b":
	//		b();
			break;
		default:
			System.out.println("Invalid Choice");
			break;
		}
	}
	static FrameGrabber grabber;
	public static void a() throws MalformedURLException, Exception{
		
		CanvasFrame c1;
		System.out.println("InputStream?");
		System.out.println("a: Webcam");
		System.out.println("b: IP Cam/Stream");
		userChoice = input.next();
		switch (userChoice){
		case "a":
			grabber = new OpenCVFrameGrabber(0);
			break;
		case "b":
			System.out.println("URL? (with \"http://\" of course)");
			userChoice  = input.next();
			grabber = new IPCameraFrameGrabber("\"" + userChoice + "\"");
			break;
		default:
			System.out.println("Invalid Choice");
			break;
		}
		grabber.start();
		Frame frame;
		Gear.process(UsefulMethods.FrameToMat(grabber.grab()));
	//	FrameToMat(frame);
		
	}
	
	
	
}
