package tk.perryma.circuitplaygroundfirmata4j;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import static org.firmata4j.firmata.parser.FirmataToken.START_SYSEX;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CAP_TOUCH_READING;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CAP_TOUCH_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CIRCUIT_PLAYGROUND_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CLEAR_NEOPIXELS_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.PLAY_TONE_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.UPDATE_NEOPIXELS_CMD;

/**
 *
 * @author dahlem.brian
 */
public class CircuitPlaygroundMessageFactory {
    
    /*
    {
            START_SYSEX, 
            END_SYSEX     
    };
    */
    
    public static byte[] updateNeoPixels = {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            UPDATE_NEOPIXELS_CMD,
            END_SYSEX     
    };
    
    public static byte[] clearNeoPixels = {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            CLEAR_NEOPIXELS_CMD,
            END_SYSEX     
    };
    
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
