import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MJPEGClient extends Thread {

    public Socket socket;
    public OutputStream outputStream;
    public byte[] newFrame = null;
    public byte[] oldFrame = null;
    public boolean online;

    public static final String header =
            "HTTP/1.0 200 OK\r\n" +
                    "Server: YourServerName\r\n" +
                    "Connection: close\r\n" +
                    "Max-Age: 0\r\n" +
                    "Expires: 0\r\n" +
                    "Cache-Control: no-cache, private\r\n" +
                    "Pragma: no-cache\r\n" +
                    "Content-Type: multipart/x-mixed-replace; " +
                    "boundary=--BoundaryString\r\n\r\n";

    public static final String packetFormat =
            "--BoundaryString\r\n" +
            "Content-type: image/jpg\r\n" +
            "Content-Length: %d\r\n\r\n";

    public MJPEGClient(Socket socket) throws java.lang.Exception {
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
        outputStream.write(header.getBytes());
        outputStream.flush();
        this.online = true;
    }

    public void setStreamImage(byte[] frame) throws IOException {
        if (!online) throw new IOException("Socket failed, waiting for close");
        newFrame = frame;
    }

    private void flush (        ) throws IOException { outputStream.flush()            ; }
    private void write (byte[] b) throws IOException { outputStream.write(b)           ; }
    private void write (String s) throws IOException { outputStream.write(s.getBytes()); }
    private void format(String format, Object... args) throws IOException {
        outputStream.write(String.format(format,args).getBytes());
    }

    @Override
    public void run() {
        setName(String.format(
            "%s.%d",
            this.getClass().getCanonicalName(),
            this.getId()
        ));

        int frameCycleIndex = 0;
        long frameProfileCounter = 0;
        long profilingStartTime = System.currentTimeMillis();
        int frameProfileLength = 600;

        while (online) try {
            byte[] frame = newFrame; //get ptr now, use it for rest of loop
            if (frame == oldFrame) {
                Thread.sleep(10);
                continue;
            }
            frameProfileCounter = (frameProfileCounter + 1) % 600;
            if (frameProfileCounter == 0) {
                long profilingEndTime = System.currentTimeMillis();
                double dTime = (profilingEndTime - profilingStartTime) / 1000.0;
                System.out.printf(
                        "\nThread %d Streamed %d frames at %f frames per second",
                        this.getId(),
                        frameProfileLength,
                        frameProfileLength / dTime
                );
                profilingStartTime = profilingEndTime;
            }
            format(packetFormat,frame.length);
            write(frame);
            write("\r\n\r\n");
            flush();
            oldFrame = frame;

        } catch (java.lang.Exception e) {
            e.printStackTrace();
            online = false;
            break;
        }
    }

    public void shutdown() { online = false; }
}
