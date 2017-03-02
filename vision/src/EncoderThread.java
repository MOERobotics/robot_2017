import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacpp.opencv_core.Mat;

import java.util.Iterator;

public class EncoderThread extends Thread {
    public Mat workImageNew = null;
    public Mat workImageOld = null;
    public BytePointer outputImage = null;
    public OpenCVFrameConverter.ToMat FrameToMatConverter = new OpenCVFrameConverter.ToMat();
    public IntPointer jpeg_quality = new IntPointer(1).put(1);

    @Override
    public void run() {
        setName(String.format(
                "%s.%d",
                this.getClass().getCanonicalName(),
                this.getId()
        ));
        while (true) try {
            Mat workImage = workImageNew;
            if (workImage == workImageOld) {
                Thread.sleep(50);
                continue;
            }

            BytePointer ptr = new BytePointer();

            opencv_imgcodecs.imencode(
                ".jpeg", workImage,
                ptr, jpeg_quality
            );

            this.outputImage = ptr;
            workImageOld = workImage;

        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void setWorkItem(Mat workImage){
        this.workImageNew = workImage;
    }
}
