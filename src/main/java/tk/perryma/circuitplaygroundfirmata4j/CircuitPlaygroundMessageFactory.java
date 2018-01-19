package tk.perryma.circuitplaygroundfirmata4j;

import static org.firmata4j.firmata.parser.FirmataToken.CAP_TOUCH_READING;
import static org.firmata4j.firmata.parser.FirmataToken.CAP_TOUCH_STREAM;
import static org.firmata4j.firmata.parser.FirmataToken.CIRCUIT_PLAYGROUND_CMD;
import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import static org.firmata4j.firmata.parser.FirmataToken.PLAY_TONE_CMD;
import static org.firmata4j.firmata.parser.FirmataToken.START_SYSEX;

/**
 *
 * @author dahlem.brian
 */
public class CircuitPlaygroundMessageFactory {
    
    public static byte[] playTone(int freq, int duration) {
        return new byte[] {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            PLAY_TONE_CMD,
            (byte)(freq & 0x7F),
            (byte)((freq >> 7) & 0x7F),
            (byte)(duration & 0x7F),
            (byte)((duration >> 7) & 0x7F),
            END_SYSEX 
        };
    }
    
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
                    START_SYSEX,
                    CIRCUIT_PLAYGROUND_CMD,
                    CAP_TOUCH_READING,
                    (byte)(pin & 0x7f),
                    END_SYSEX 
                };
            default:
                throw new UnsupportedPinException("Pin " + pin + " does not support capacitive touch.");                
        }
    }
    public static byte[] streamTouchData(int pin) {
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
                    START_SYSEX,
                    CIRCUIT_PLAYGROUND_CMD,
                    CAP_TOUCH_STREAM,
                    (byte)(pin & 0x7f),
                    END_SYSEX 
                };
            default:
                throw new UnsupportedPinException("Pin " + pin + " does not support capacitive touch.");                
        }
    }
}
