
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;


public class MJPEGserver {
	private int listeningport;
	private OpenCVFrameConverter.ToMat FrameToMatConverter = new OpenCVFrameConverter.ToMat();
	private Set<clientThread> socketSet = new HashSet<>();
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
						System.out.print("\nClient connected");
						clientThread t = new clientThread(socket);
						t.start();
						socketSet.add(t);
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
	public void stop() {
		try {
			serverThread.join();
			serverSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
			for (
				Iterator<clientThread> i = socketSet.iterator();
				i.hasNext();
			) {
				clientThread t = i.next();
				try {
					t.queueWrite(data);
				} catch (Exception e) {
					t.join();
					i.remove();
				}
			}
		} catch(Exception e){e.printStackTrace();}
	}

	class clientThread extends Thread {
		public Socket socket;
		public OutputStream outputStream;
		byte[] newFrame = null;
		byte[] oldFrame = null; //just a pointer
		boolean online;

		public clientThread (Socket socket) throws java.lang.Exception {
			online=true;
			this.socket       = socket;
			this.outputStream = socket.getOutputStream();
			outputStream.write((
				"HTTP/1.0 200 OK\r\n" +
				"Server: YourServerName\r\n" +
				"Connection: close\r\n" +
				"Max-Age: 0\r\n" +
				"Expires: 0\r\n" +
				"Cache-Control: no-cache, private\r\n" +
				"Pragma: no-cache\r\n" +
				"Content-Type: multipart/x-mixed-replace; " +
				"boundary=--BoundaryString\r\n\r\n"
			).getBytes());
			outputStream.flush();
		}

		public void queueWrite(byte[] frame) throws IOException {
			if (!online) throw new IOException("Socket failed, waiting for close");
			newFrame = frame;
		}

		public void run() {
			setName(String.format(
				"%s.%d",
				this.getClass().getCanonicalName(),
				this.getId()
			));
			while (true) try {
				byte[] frame = newFrame; //get ptr now, use it for rest of loop
				if (frame == oldFrame) {
					Thread.sleep(50);
					continue;
				}
				outputStream.write((
					"--BoundaryString\r\n" +
					"Content-type: image/jpg\r\n" +
					"Content-Length: " +
					frame.length +
					"\r\n\r\n"
				).getBytes());
				outputStream.write(frame);
				outputStream.write("\r\n\r\n".getBytes());
				outputStream.flush();

			} catch (java.lang.Exception e) {
				break;
				//e.printStackTrace();
			}
			online = false;
		}


	}
} //end file
