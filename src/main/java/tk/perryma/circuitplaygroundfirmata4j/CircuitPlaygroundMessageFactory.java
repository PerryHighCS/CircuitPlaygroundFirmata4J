package tk.perryma.circuitplaygroundfirmata4j;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import static org.firmata4j.firmata.parser.FirmataToken.START_SYSEX;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CAP_TOUCH_READING;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CAP_TOUCH_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CIRCUIT_PLAYGROUND_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.CLEAR_NEOPIXELS_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_SENSE_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.PLAY_TONE_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.REQUEST_ACCEL_READING;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.REQUEST_ACCEL_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.REQUEST_TAP_DATA;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.REQUEST_TAP_DATA_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.SET_ACCEL_RANGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.SET_NEOPIXEL_BRIGHTNESS;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.SET_NEOPIXEL_COLOR_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.STOP_ACCEL_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.STOP_CAP_TOUCH_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.STOP_TAP_DATA_STREAM;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.STOP_TONE_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.UPDATE_NEOPIXELS_CMD;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.byteArray;

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
    
    /**
     * Create a command that will update the physical NeoPixels from the color
     * buffer
     */
    public static byte[] updateNeoPixels = {
        START_SYSEX, 
        CIRCUIT_PLAYGROUND_CMD,
        UPDATE_NEOPIXELS_CMD,
        END_SYSEX     
    };
    
    /**
     * Create a command to erase all colors in the NeoPixel buffer
     */
    public static byte[] clearNeoPixels = {
        START_SYSEX, 
        CIRCUIT_PLAYGROUND_CMD,
        CLEAR_NEOPIXELS_CMD,
        END_SYSEX     
    };
    
    /**
     * Create a command to update the color of one NeoPixel in the color buffer
     * 
     * @param pixel
     *          The NeoPixel index to update
     * @param red
     *          The red component of the new color (0-255)
     * @param green
     *          The green component of the new color (0-255)
     * @param blue
     *          The blue component of the new color (0-255)
     * @return 
     *          A message that will set the color of the given pixel
     */
    public static byte[] setNeoPixel(int pixel, int red, int green, int blue) {
        red &= 0xFF;
        green &= 0xFF;
        blue &= 0xFF;
        
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (red >>> 1);                           // Pack RGB 8-bit values 
        bytes[1] = (byte) ((red >>> 7 << 6) | green >>> 2);      // tightly into 4 7-bit
        bytes[2] = (byte) ((green >>> 6 << 5) | (blue >>> 3));   // bytes
        bytes[3] = (byte) (blue >>> 5 << 4);
        
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
    
    /**
     * Create a command message that will change the brightness of all the
     * NeoPixels
     * 
     * @param brightness
     *          The brightness level to set the neopixels to (0-100)
     * @return 
     *          The message that will set the brightness level of all of the
     *          NeoPixels
     */
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
    
    /**
     * Create a message that will play a note of a given frequency for a given
     * duration
     * 
     * @param freq
     *          The frequency of the note to play (0-16383Hz)
     * @param duration
     *          The length of time to play the note (0-16383ms) 0 plays 
     *          continuously until stopped
     * @return 
     *          The message that will play the given note for the given duration
     */
    public static byte[] playTone(int freq, int duration) {
        return new byte[] {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            PLAY_TONE_CMD,
            (byte)(freq & 0x7F),
            (byte)((freq >>> 7) & 0x7F),
            (byte)(duration & 0x7F),
            (byte)((duration >>> 7) & 0x7F),
            END_SYSEX 
        };
    }
    
    
    /**
     * A message that will stop any playing note
     */
    public static byte[] stopTone = {
        START_SYSEX, 
        CIRCUIT_PLAYGROUND_CMD,
        STOP_TONE_CMD,
        END_SYSEX     
    };
    
    /**
     * A message that requests the current accelerometer reading
     */
    public static byte[] requestAccelData = {
        START_SYSEX, 
        CIRCUIT_PLAYGROUND_CMD,
        REQUEST_ACCEL_READING,
        END_SYSEX
    };
    
    /**
     * A message that requests the Circuit Playground stream the accelerometer
     * readings
     */
    public static byte[] requestAccelStream = {
        START_SYSEX, 
        CIRCUIT_PLAYGROUND_CMD,
        REQUEST_ACCEL_STREAM,
        END_SYSEX     
    };
    
    /**
     * A message that requests the Circuit Playground stop streaming the
     * accelerometer readings
     */
    public static byte[] stopAccelStream = {
        START_SYSEX, 
        CIRCUIT_PLAYGROUND_CMD,
        STOP_ACCEL_STREAM,
        END_SYSEX     
    };
    
    /**
     * Create a message that sets the sensitivity of the Circuit Playground
     * 
     * @param range
     *          The sensitivity range to set to
     * @return 
     *          The message that will update the Circuit Playground's 
     *          accelerometer sensitivity
     */
    public static byte[] setAccelerometerRange(byte range) {
        return new byte[] {
            START_SYSEX, 
            CIRCUIT_PLAYGROUND_CMD,
            SET_ACCEL_RANGE,
            range,
            END_SYSEX     
        };
    }

    /**
     * Create a message that will get the current touch reading for a given pin
     * 
     * @param pin
     *          The pin that will be read for touch data
     * @return 
     *          The message that will request touch data for the given pin
     */
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
    
    /**
     * Create a message that will request that the Circuit Playground stream
     * touch data for a given pin
     * 
     * @param pin
     *          The pin that will be read for touch data
     * @return 
     *          The message that requests a stream of data for the given pin
     */
    public static byte[] requestTouchDataStream(int pin) {
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
    
    /**
     * Create a message that will request that the Circuit Playground stop
     * streaming touch data for a given pin
     * 
     * @param pin
     *          The pin that will be read for touch data
     * @return 
     *          The message that requests a stream of data for the given pin
     */
    public static byte[] stopTouchDataStream(int pin) {
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
                    STOP_CAP_TOUCH_STREAM,
                    (byte)(pin & 0x7f),
                    END_SYSEX 
                };
            default:
                throw new UnsupportedPinException("Pin " + pin + " does not support capacitive touch.");                
        }
    }

    public static byte[] requestTapData = {
        START_SYSEX,
        CIRCUIT_PLAYGROUND_CMD,
        REQUEST_TAP_DATA,
        END_SYSEX 
    };
    
    public static byte[] requestTapDataStream = {
        START_SYSEX,
        CIRCUIT_PLAYGROUND_CMD,
        REQUEST_TAP_DATA_STREAM,
        END_SYSEX 
    };
    
    public static byte[] stopTapDataStream = {
        START_SYSEX,
        CIRCUIT_PLAYGROUND_CMD,
        STOP_TAP_DATA_STREAM,
        END_SYSEX 
    };
    
    public static byte[] setTapConfiguration(int numTaps, int sensitivity) {
        byte[] taps = byteArray((byte)numTaps);
        byte[] thresh = byteArray((byte)sensitivity);
        
        return new byte[] {
            START_SYSEX,
            CIRCUIT_PLAYGROUND_CMD,
            STOP_TAP_DATA_STREAM,
            taps[0],
            taps[1],
            thresh[0],
            thresh[1],
            END_SYSEX
        };
    }
    
    public static byte[] requestColorSense = {
        START_SYSEX,
        CIRCUIT_PLAYGROUND_CMD,
        COLOR_SENSE_CMD,
        END_SYSEX 
    };
}
