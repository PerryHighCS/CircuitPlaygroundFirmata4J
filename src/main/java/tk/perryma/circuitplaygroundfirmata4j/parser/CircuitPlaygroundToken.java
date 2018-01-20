/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j.parser;

/**
 *
 * @author bdahl
 */
public class CircuitPlaygroundToken {
        
    // Circuit Playground messages and responses
    public static final byte SET_NEOPIXEL_COLOR_CMD  = 0x10; // Set the buffered color for one NeoPixel
    public static final byte UPDATE_NEOPIXELS_CMD    = 0x11; // Update NeoPixel colors from buffer
    public static final byte CLEAR_NEOPIXELS_CMD     = 0x12; // Clear all NeoPixels to black
    public static final byte SET_NEOPIXEL_BRIGHTNESS = 0x13; // Set the max brightness for all neopixels
    public static final byte PLAY_TONE_CMD           = 0x20; // Play a tone for a specified duration
    public static final byte STOP_TONE_CMD           = 0x20; // Stop any currently playing tone
    public static final byte ACCEL_RESPONSE          = 0x36; // Accelerometer read response
    public static final byte TAP_RESPONSE            = 0x37; // Tap detection response
    public static final byte CIRCUIT_PLAYGROUND_CMD  = 0x40; // Start a Circuit Playground Command
    public static final byte CIRCUIT_PLAYGROUND_MSG  = 0x40; // Start a Circuit Playground Command
    public static final byte CAP_TOUCH_READING       = 0x40; // Request a single capacitive touch reading
    public static final byte CAP_TOUCH_STREAM        = 0x41; // Request continuous capacitive touch readings
    public static final byte TOUCH_RESPONSE          = 0x43; // Capacitive touch response
    public static final byte COLOR_SENSE_RESPONSE    = 0x51; // Color sense response
    
    // event types and names
    public static final String TOUCH_MESSAGE = "touch";
    public static final String TOUCH_LEVEL = "touchLevel";
}
