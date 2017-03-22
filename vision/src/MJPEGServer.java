
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MJPEGServer extends Thread {
	private int port;
	private Set<MJPEGClient> clientThreads = new HashSet<>();
	private ServerSocket serverSocket;
	private Thread serverThread;

	private MJPEGServer(){}
	public MJPEGServer(int port){
		this.port = port;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			while (!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(2);
				byte[] ipOctals = socket.getInetAddress().getAddress();
				System.out.printf("\nClient connected: %d.%d.%d.%d", ipOctals);
				MJPEGClient client = new MJPEGClient(socket);
				client.start();
				clientThreads.add(client);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public void shutdown() throws Exception {
		for (MJPEGClient t : clientThreads) t.shutdown();
		serverThread.join();
		serverSocket.close();
	}

	public void streamData(byte[] data) throws InterruptedException {
		for (
				Iterator<MJPEGClient> iter = clientThreads.iterator();
				iter.hasNext();
		) {
			MJPEGClient t = iter.next();
			try { t.setStreamImage(data); }
			catch (Exception e) {
				t.join();
				iter.remove();
			}
		}
	}
}
