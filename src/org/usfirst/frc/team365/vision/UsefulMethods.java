package org.usfirst.frc.team365.vision;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class UsefulMethods {
	static OpenCVFrameConverter.ToMat TheFrameToMatConverter = new OpenCVFrameConverter.ToMat();
	static OpenCVFrameConverter.ToIplImage TheFrameToIplImageConverter = new OpenCVFrameConverter.ToIplImage();
	public static Mat FrameToMat(Frame frame){

		return TheFrameToMatConverter.convert(frame);
		
	}
	
	public static Frame MatToFrame(Mat mat){
		return TheFrameToMatConverter.convert(mat);
	}
	
	public static IplImage FrameToIplImage(Frame frame){
		return TheFrameToIplImageConverter.convert(frame);
	}
	
	public static Frame IplImageToFrame(IplImage iplImage){
		return TheFrameToIplImageConverter.convert(iplImage);
	}
	
	
	public static byte[] matToByteArray(Mat mat){
		return null;
		
	}
	
	
	
}
