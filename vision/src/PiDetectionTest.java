import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.system.SystemInfo;
import com.pi4j.system.impl.DefaultSystemInfoProvider;

/**
 * Created by kevok on 2/12/17.
 */
public class PiDetectionTest {
    public static void main(String args[]) {
        try {
            if (System.getProperty("nogpio") != null) {
                System.out.println("Testing");
            } else {
                System.out.println(
                        GpioFactory.getInstance()
                );
            }
        } catch (Exception e) {e.printStackTrace();}

    }
}
