package tk.perryma.circuitplaygroundfirmata4j;

import static org.firmata4j.firmata.parser.FirmataToken.CAP_TOUCH_READING;
import static org.firmata4j.firmata.parser.FirmataToken.CIRCUIT_PLAYGROUND_CMD;

/**
 *
 * @author dahlem.brian
 */
public class CircuitPlaygroundMessageFactory {
    public static byte[] requestTouchReading(int pin) {
        switch (pin) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 6:
            case 9:
            case 10:
            case 12:
                return new byte[] {
                    CIRCUIT_PLAYGROUND_CMD,
                    CAP_TOUCH_READING,
                    (byte)(pin & 0x7f)
                };
            default:
                throw new UnsupportedPinException("Pin " + pin + " does not support capacitive touch.");                
        }
    }
    
}
