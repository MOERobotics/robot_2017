package org.usfirst.frc.team365.vision;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;



public class MJPEGserver {
	private int listeningport;
	private OpenCVFrameConverter.ToMat FrameToMatConverter = new OpenCVFrameConverter.ToMat();
	private Set<Socket> socketSet = new HashSet();
	private ServerSocket serverSocket;
	private Thread serverThread;
	
	
	
	
	/**
	 * Creates an MJPEG server 
	 *
	 * @param  port the local port on which the server should listen
	 */
	public MJPEGserver(int port){
		listeningport = port;
	}

	 

	/**
	 * Starts the MJPEG server 
	 */
	public void start(){
		serverThread = new Thread() {
			public void run() {
				try {
					serverSocket = new ServerSocket(listeningport);
					while (!serverSocket.isClosed()) {
						Socket socket = serverSocket.accept();
						socket.setSoTimeout(2);
						System.out.println("Client connected");

						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(
								("HTTP/1.0 200 OK\n" + "Server: YourServerName\r\n" + "Connection: close\r\n" + "Max-Age: 0\r\n"
										+ "Expires: 0\r\n" + "Cache-Control: no-cache, private\r\n" + "Pragma: no-cache\r\n"
										+ "Content-Type: multipart/x-mixed-replace; " + "boundary=--BoundaryString\r\n\r\n")
												.getBytes());
						outputStream.flush();
						socketSet.add(socket);
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		};
		serverThread.start();
	}
	
	
	
	/**
	 * Stops the MJPEG server 
	 */
	public void stop() throws IOException, InterruptedException{
		serverSocket.close();
		serverThread.join();
	}
	
	
	
	
	/**
	 * streams a frame
	 *
	 * @param  frame a frame that is to be streamed
	 */
	public void streamFrame(Frame frame){
		BytePointer ptr = new BytePointer();
		opencv_imgcodecs.imencode(".jpeg", FrameToMatConverter.convert(frame), ptr);
		streamData(ptr.getStringBytes());
	}
	
	
	/**
	 * streams a mat
	 *
	 * @param  mat a mat that is to be streamed
	 */
	public void streamMat(Mat mat){
		BytePointer ptr = new BytePointer();
		opencv_imgcodecs.imencode(".jpeg", mat, ptr);
		streamData(ptr.getStringBytes());
	}
	
	
	/**
	 * streams data
	 *
	 * @param  data the data (byte[]) to be streamed
	 */
	public void streamData(byte[] data){
		try{
			for (Socket socket : socketSet) {
				try {
					OutputStream outputStream = socket.getOutputStream();
					outputStream.write((
					        "--BoundaryString\r\n" +
					        "Content-type: image/jpg\r\n" +
					        "Content-Length: " +
					        data.length +
					        "\r\n\r\n").getBytes());
					    outputStream.write(data);
					    outputStream.write("\r\n\r\n".getBytes());
					    outputStream.flush();
				} catch (Exception e) {
					//e.printStackTrace();
					socketSet.remove(socket);
				}
			}	
		} catch(Exception e){/*e.printStackTrace();*/}	
	}
} //end file
