/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.vecmath.Vector3d;
import jssc.SerialPortList;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.ui.JPinboard;

/**
 *
 * @author bdahl
 */
public class CircuitPlayground extends FirmataDevice {
    private Pin lightSensor;
    private Pin temperatureSensor;
    private Pin microphone;
    private Pin leftButton;
    private Pin rightButton;
    private Pin slideSwitch;
    
    public CircuitPlayground(String portName) throws IOException {
        super(portName);

        // Initialize touch data with invalid reading
        Arrays.fill(touchData, -1);
        
        // Setup default polling listeners
        addAccelerationListener((data) -> this.setAccelerationData(data));
        addTapListener((data) -> this.setTapData(data));
        addTouchListener((pin, data) -> this.setTouchData(pin, data));

        addEventListener(new IODeviceStartListener() {
            @Override
            public void onStart(IOEvent ioe) {
                try {
                    // Connect hardwired peripherials to their pins
                    lightSensor = getPin(23);
                    lightSensor.setMode(Pin.Mode.ANALOG);

                    temperatureSensor = getPin(18);
                    temperatureSensor.setMode(Pin.Mode.ANALOG);

                    microphone = getPin(22);
                    microphone.setMode(Pin.Mode.ANALOG);

                    leftButton = getPin(4);
                    leftButton.setMode(Pin.Mode.INPUT);

                    rightButton = getPin(19);
                    rightButton.setMode(Pin.Mode.INPUT);

                    slideSwitch = getPin(21);
                    slideSwitch.setMode(Pin.Mode.INPUT);
                }
                catch (IOException e) {                
                }
            }
        });
    }
    
    /*
     * Neo Pixel Commands ******************************************************
     */
    
    /**
     * Push the current NeoPixel color buffer out to the physical pixels. After
     * calling this methods the pixels will update their colors to the last 
     * colors set for each pixel.
     */
    public void showNeoPixels() {
        
    }
    
    /**
     * Reset all of the NeoPixel colors in the color buffer to black. The 
     * physical NeoPixels will not change color until showNeoPixels is called.
     */
    public void clearNeoPixels() {
        
    }
    
    /**
     * Adjust the brightness of all the NeoPixels. The value should be 0 to 100
     * where 0 is completely dark and 100 is full brightness. Note that 
     * animating brightness is not recommended as going down to 0 will 'lose'
     * information and not be able to go back up to 100. Instead just use this
     * function to set the brightness once at the start. By default the pixels
     * are set to 20% brightness.
     * 
     * @param brightness
     *          The brightness level to cap all NeoPixels at, as an integer
     *          percentage from 0 - 100.
     */
    public void setNeoPixelBrightness(int brightness) {
        
    } 
    
    /*
     * Speaker/Buzzer Commands *************************************************
     */
    
    /**
     * Play a tone of the specified frequency on the speaker/buzzer. The tone is
     * generated using a square wave so it will have a slightly harsh sound but
     * is great for simple sounds and music. You can specify the duration the 
     * tone should be played, or you can specify to start playing the tone 
     * forever until a stop tone command is sent.
     * 
     * @param freq
     *          The frequency of the tone to play, in hertz in a range of  
     *          0-16383Hz.
     * @param duration 
     *          The length of time to play the tone, in milliseconds 0-16383ms. 
     *          Note that a value of 0 will play a tone without a duration, the
     *          tone will play until stopTone() is called.
     */
    public void playTone(int freq, int duration) {
        
    }
    
    /**
     * Stop the playback of any currently playing tone.
     */
    public void stopTone() {
        
    }
    
    /*
     * Accelerometer Commands **************************************************
     */    
    
    /**
     * Circuit Playground Acceleration Sensitivity
     */
    public enum AccelTapRange {
        /**
         * Set acceleration sensitivity to +/-2G
         */
        G2,        
        /**
         * Set acceleration sensitivity to +/-4G
         */
        G4,        
        /**
         * Set acceleration sensitivity to +/-8G
         */
        G8,        
        /**
         * Set acceleration sensitivity to +/-16G
         */
        G16
    };
    
    Vector3d accel = null;
    
    /**
     * Request a single accelerometer reading. The accelerometer reading can be
     * accessed by calling accelData() or by adding a listener with
     * addAccelerationListener().
     */
    public void requestAccelData() {
        accel = null;
        
        //TODO: add request for tap data
    }

    /**
     * Request that the CircuitPlayground continuously send acceleration data.
     * Acceleration readings will be taken every 20 milliseconds. Data can be
     * accessed by calling accelData() or by adding a listener with
     * addAccelerationListener().
     * 
     * @param enable
     *          Enable or disable the streaming of acceleration data. True 
     *          begins streaming, false stops streaming.
     */
    public void streamAccelData(boolean enable) {
        accel = null;
        
        //TODO: add request for data stream
    }
    
    /**
     * Set the sensitivity of the accelerometer
     * 
     * @param sensitivity
     *          Set the range of acceptable accelerometer values. Increasing the
     *          range will allow you to read higher forces, but will reduce the
     *          accuracy of the readings.
     *          The default range is +/- 2G.
     */
    public void setAccelRange(AccelTapRange sensitivity) {
        
    }
    
    /**
     * Get the last received acceleration data. If no data has been received
     * since the last call to requestAccelData(), this method returns a null.
     * 
     * @return The last acceleration reading received. If requestAccelData() was
     *         called, this method returns null until new data is received, then
     *         returns that new data.
     */
    public Vector3d accelData() {
        return accel;
    }
    
    /**
     * Add a new listener for acceleration data. The AccelerationListener's 
     * onAccelData() method will be called with the acceleration vector whenever
     * acceleration data is received.
     * 
     * @param al
     *          The AccelerationListener to receive acceleration data.
     */
    public final void addAccelerationListener(AccelerationListener al) {
        
    }
    
    private void setAccelerationData(Vector3d data) {
        this.accel = data;
    }
    
    /*
     * Tap Detection Commands **************************************************
     */    
    
    /**
     * The type of tap detected
     */
    public enum Tap {
        /**
         * No Tap Detected
         */
        NONE,
        /**
         * Single Tap Detected
         */
        SINGLE, 
        /**
         * Double Tap Detected
         */
        DOUBLE
    };
    
    Tap tapData = null;
    
    /**
     * Request that the CircuitPlayground send the most recent tap data. The tap
     * data can subsequently be accessed by calling tapData() or through a
     * TapListener.
     */
    public void requestTapData() {
        this.tapData = null;
    }
    
    /**
     * Check the most recent tap detected
     * 
     * @return The type of tap most recently detected. null if no request for
     *         tap data has been sent or if there has not been any tap data
     *         received from the CircuitPlayground since the last request was
     *         sent.
     */
    public Tap tapData() {
        return this.tapData;
    }
    
    /**
     * Request that the CircuitPlayground start continuously sending tap data.
     * Tap readings are sent every 20 milliseconds when enabled.
     * 
     * @param enable
     *          True to begin streaming tap data, false to stop
     */
    public void streamTapData(boolean enable) {
        
    }
    
    /**
     * Set the tap detection configuration
     * @param detect
     *          What type of tap to detect, NONE, SINGLE, or DOUBLE.  Detecting 
     *          DOUBLE taps also detects SINGLE taps.
     * @param sensitivity 
     *          The required force for tap detection. 
     */
    public void setTapConfiguration(Tap detect, AccelTapRange sensitivity) {
        
    } 
    
    /**
     * Add a new listener for tap data. The TapListener's onTap() method will be
     * called whenever tap data is received.
     * 
     * @param tl
     *          The TapListener to receive tap data.
     */
    public final void addTapListener(TapListener tl) {
        
    }
    
    private void setTapData(Tap data) {
        this.tapData = data;
    }
    
    /*
     * Capacitive Touch Commands ***********************************************
     */ 
    
    int touchData[] = new int[12];
    
    /**
     * Request that the CircuitPlayground send the most recent touch data for a
     * specific pin. The data can be checked through subsequent calls to
     * getTouchReading or via a TouchListener
     * 
     * @param pin
     *      The pin to get touch data for must be one of: 0, 1, 2, 3, 6, 9, 10,
     *      or 12.
     */
    public void requestTouchReading(int pin) {
        switch (pin) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 6:
            case 9:
            case 10:
            case 12:
                touchData[pin] = -1;
                
                //TODO: Add request for given pin
                
                break;
            default:
                throw new UnsupportedPinException("Specified pin does not support touch reading: " + pin);
        }
    }
    
    /**
     * Get the most recently received touch data for a particular pin. If no
     * touch data has been received for this pin since the most recent call to
     * requestTouchReading, a value of -1 will be returned.
     * 
     * @param pin
     *          The touch pin to check for data from, must be one of: 0, 1, 2, 
     *          3, 6, 9, 10, or 12
     * @return The touch level received. This is raw data from the pin, in a 
     *         range from 0-2,147,483,647. Higher numbers mean stronger touches.
     *         If no data has been received from the CircuitPlayground for this
     *         pin since requestTouchReading was last called for this pin, a
     *         value of -1 will be received.
     */
    public int getTouchReading(int pin) {
        switch (pin) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 6:
            case 9:
            case 10:
            case 12:
                return touchData[pin];
            default:
                throw new UnsupportedPinException("Specified pin does not support touch reading: " + pin);
        }    
    }
    
    /**
     * Request that the CircuitPlayground start continuously sending touch data
     * for a given pin. Touch readings are sent every 20 milliseconds when 
     * enabled. Readings can be accessed by calling getTouchReading or adding
     * a TouchListener.
     * 
     * @param pin
     *          The touch pin to check for data from, must be one of: 0, 1, 2, 
     *          3, 6, 9, 10, or 12
     * @param enable
     *          True to begin streaming touch data, false to stop
     */
    public void streamTouchData(int pin, boolean enable) {
        switch (pin) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 6:
            case 9:
            case 10:
            case 12:
                touchData[pin] = -1;
                
                //TODO: Add request for given pin
                
                break;
            default:
                throw new UnsupportedPinException("Specified pin does not support touch reading: " + pin);
        }
    }
    
    
    /**
     * Add a new listener for tap data. The TapListener's onTap() method will be
     * called whenever tap data is received.
     * 
     * @param tl
     *          The TapListener to receive tap data.
     */
    public final void addTouchListener(TouchListener tl) {
        
    }
    
    private void setTouchData(int pin, int data) {
        this.touchData[pin] = data;
    }
    
    /*
     * Color Sensing Commands **************************************************
     */ 
    
    Color sensed = null;
    
    /**
     * Request that the Circuit Playground perform a color sense operation and
     * send back the color detected. This command will perform a single color
     * sense using the light sensor and NeoPixel #1. The response can be checked
     * through subsequent calls to getSensedColor or by adding a 
     * SensedColorListener.
     */
    public void requestColorSense() {
        sensed = null;
        
        //TODO: add a request for data
    }
    
    /**
     * Check the color sensed by the CircuitPlayground. Color is sensed by 
     * quickly flashing NeoPixel #1 red, green, and blue. The strength of light
     * detected during each flash corresponds to the red, green, and blue 
     * components of the returned color.
     * 
     * @return the sensed color 
     */
    public Color getSensedColor() {
        return sensed;
    }
    
    public static void main(String[] args) {
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            System.out.println(portName);
        }
        try {
            CircuitPlayground device = new CircuitPlayground("COM7");
            device.start();
            device.ensureInitializationIsDone();
            
            /*
            JPinboard pinboard = new JPinboard(device);
            JFrame frame = new JFrame("Pinboard Example");
            frame.add(pinboard);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
            */
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
        catch (InterruptedException e) {            
        }
    }
}
