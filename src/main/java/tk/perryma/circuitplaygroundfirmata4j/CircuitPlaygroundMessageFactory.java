package tk.perryma.circuitplaygroundfirmata4j;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import static org.firmata4j.firmata.parser.FirmataToken.START_SYSEX;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CAP_TOUCH_READING;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CAP_TOUCH_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CIRCUIT_PLAYGROUND_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CLEAR_NEOPIXELS_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.PLAY_TONE_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.SET_NEOPIXEL_BRIGHTNESS;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.SET_NEOPIXEL_COLOR_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.STOP_TONE_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.UPDATE_NEOPIXELS_CMD;

/**
 *
 * @author dahlem.brian
 */
public class CircuitPlaygroundMessageFactory {
    
    /*
    {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
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
    
    public static byte[] setNeoPixel(int pixel, int red, int green, int blue) {
        red &= 0xFF;
        green &= 0xFF;
        blue &= 0xFF;
        
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (red >> 1);                          // Pack RGB 8-bit values 
        bytes[1] = (byte) ((red >> 7 << 6) | green >> 2);      // tightly into 4 7-bit
        bytes[2] = (byte) ((green >> 6 << 5) | (blue >> 3));   // bytes
        bytes[3] = (byte) (blue >> 5 << 4);
        
        return new byte[] {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            SET_NEOPIXEL_COLOR_CMD,
            (byte) pixel,
            bytes[0],
            bytes[1],
            bytes[2],
            bytes[3],
            END_SYSEX     
        };
    }
    
    public static byte[] setNeoPixelBrightness(int brightness) {
        brightness = Math.min(100, Math.abs(brightness)); // Limit brightness to 0-100

        return new byte[] {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            SET_NEOPIXEL_BRIGHTNESS,
            (byte) brightness,
            END_SYSEX     
        };
    }
    
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
    
    public static byte[] stopTone = {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            STOP_TONE_CMD,
            END_SYSEX     
    };
    
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
