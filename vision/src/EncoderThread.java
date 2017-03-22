import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgcodecs;

public class EncoderThread extends Thread {
    public Mat         workImageNew = null;
    public Mat         workImageOld = null;
    public BytePointer outputImage  = null;
    public IntPointer  encodeOpts   = null;
    public boolean     online       = false;


    public EncoderThread(int qualityPercent) {
        //Technically these should be key/val pairs,
        //but put(key,val) actually compiles to put(index,val).
        encodeOpts = new IntPointer(3).put(
            opencv_imgcodecs.CV_IMWRITE_JPEG_QUALITY,qualityPercent,0
        );
    }

    @Override
    public void run() {

        setName(String.format(
                "%s.%d",
                this.getClass().getCanonicalName(),
                this.getId()
        ));

        online = true;
        while (online) try {
            //Atomic read image
            Mat workImage = workImageNew;

            //Do we have any work to do?
            if (workImage == workImageOld) {
                Thread.sleep(50);
                continue;
            }

            //Pointer to our jpeg structure
            BytePointer jpegPtr = new BytePointer();

            opencv_imgcodecs.imencode(
                ".jpeg",
                workImage,
                jpegPtr,
                encodeOpts
            );

            outputImage  = jpegPtr;
            workImageOld = workImage;

        } catch (java.lang.Exception e) {
            e.printStackTrace();
            online = false;
            break;
        }
    }

    public             EncoderThread  (             ) { this(30)    ; }
    public void        setWorkItem    (Mat workImage) { workImageNew = workImage ; }
    public void        shutdown       (             ) { online = false           ; }
    public BytePointer getOutputImage (             ) { return outputImage       ; }
}
