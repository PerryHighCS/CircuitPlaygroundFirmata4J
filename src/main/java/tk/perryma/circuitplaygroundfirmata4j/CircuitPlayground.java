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
import org.firmata4j.fsm.Event;
import org.firmata4j.ui.JPinboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TOUCH_MESSAGE;


/**
 *
 * @author bdahl
 */
public class CircuitPlayground extends FirmataDevice {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitPlayground.class);

    private Pin lightSensor;
    private Pin temperatureSensor;
    private Pin microphone;
    private Pin leftButton;
    private Pin rightButton;
    private Pin slideSwitch;

    /**
     * Create a CircuitPlayground instance connected to a given serial port.
     * 
     * @param portName
     *          The (OS Dependant) filename for the serial port the Circuit
     *          Playground is connected to
     */
    public CircuitPlayground(String portName) {
        super(portName);

        // Initialize touch data with invalid reading
        Arrays.fill(touchData, -1);

        // Setup default polling listeners
        addAccelerationListener((data) -> this.setAccelerationData(data));
        addTapListener((data) -> this.setTapData(data));
        addTouchListener((pin, data) -> this.setTouchData(pin, data));
    }

    /**
     * Connect to the CircuitPlayground
     * 
     * @throws java.io.IOException 
     *          If a connection cannot be made to the given port or the 
     *          necessary startup commands cannot be sent.
     */
    @Override
    public void start() throws IOException {
        
        // Add listeners for the built in sensors
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
                } catch (IOException e) {
                }
            }
        });
        
        super.start();
    }
    
    /*
     * Neo Pixel Commands ******************************************************
     */
    
    /**
     * Buffer a new color for the selected NeoPixel. The NeoPixel will not
     * change colors until showNeoPixels is called, then all NeoPixels will
     * update to their buffered colors simultaneously.
     * 
     * @param pixelNum 
     *          The number of the NeoPixel to change (0-9)
     * @param color 
     *          The color to make the NeoPixel
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void setNeoPixelColor(int pixelNum, Color color) throws IOException {
        sendMessage(CircuitPlaygroundMessageFactory.setNeoPixel(pixelNum,
            color.getRed(), color.getGreen(), color.getBlue()));
    }
    
    /**
     * Push the current NeoPixel color buffer out to the physical pixels. After
     * calling this methods the pixels will update their colors to the last
     * colors set for each pixel.
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void showNeoPixels() throws IOException {
        sendMessage(CircuitPlaygroundMessageFactory.updateNeoPixels);
    }

    /**
     * Reset all of the NeoPixel colors in the color buffer to black. The
     * physical NeoPixels will not change color until showNeoPixels is called.
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues                              
     */
    public void clearNeoPixels() throws IOException {
        sendMessage(CircuitPlaygroundMessageFactory.clearNeoPixels);
    }

    /**
     * Adjust the brightness of all the NeoPixels. The value should be 0 to 100
     * where 0 is completely dark and 100 is full brightness. Note that
     * animating brightness is not recommended as going down to 0 will 'lose'
     * information and not be able to go back up to 100. Instead just use this
     * function to set the brightness once at the start. By default the pixels
     * are set to 20% brightness.
     *
     * @param brightness The brightness level to cap all NeoPixels at, as an
     *          integer percentage from 0 - 100.
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void setNeoPixelBrightness(int brightness) throws IOException {
        sendMessage(CircuitPlaygroundMessageFactory.setNeoPixelBrightness(brightness));
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
     * @param freq The frequency of the tone to play, in hertz in a range of
     * 0-16383Hz.
     * @param duration The length of time to play the tone, in milliseconds
     * 0-16383ms. Note that a value of 0 will play a tone without a duration,
     * the tone will play until stopTone() is called.

     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void playTone(int freq, int duration) throws IOException {
        sendMessage(CircuitPlaygroundMessageFactory.playTone(freq, duration));
    }

    /**
     * Stop the playback of any currently playing tone.

     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void stopTone() throws IOException {
        sendMessage(CircuitPlaygroundMessageFactory.stopTone);
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

     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void requestAccelData() throws IOException {
        accel = null;

        //TODO: add request for tap data
    }

    /**
     * Request that the CircuitPlayground continuously send acceleration data.
     * Acceleration readings will be taken every 20 milliseconds. Data can be
     * accessed by calling accelData() or by adding a listener with
     * addAccelerationListener().
     *
     * @param enable Enable or disable the streaming of acceleration data. True
     * begins streaming, false stops streaming.

     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void streamAccelData(boolean enable) throws IOException {
        accel = null;

        //TODO: add request for data stream
    }

    /**
     * Set the sensitivity of the accelerometer
     *
     * @param sensitivity Set the range of acceptable accelerometer values.
     * Increasing the range will allow you to read higher forces, but will
     * reduce the accuracy of the readings. The default range is +/- 2G.
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void setAccelRange(AccelTapRange sensitivity) throws IOException {

    }

    /**
     * Get the last received acceleration data. If no data has been received
     * since the last call to requestAccelData(), this method returns a null.
     * Acceleration is measured in meters per second squared
     *
     * @return The last acceleration reading received. If requestAccelData() was
     * called, this method returns null until new data is received, then returns
     * that new data.
     */
    public Vector3d accelData() {
        return accel;
    }

    /**
     * Get the x component of the last received acceleration data. If no data
     * has been received since the last call to requestAccelData(), this method
     * returns Double.NaN.
     * 
     * Acceleration is measured in meters per second squared
     * 
     * @return The x component of the last acceleration reading received, NaN if
     * there is no data.
     */
    public double getAccelX() {
        if (accel == null) {
            return Double.NaN;
        }
        else {
            return accel.x;
        }
    }

    
    /**
     * Get the y component of the last received acceleration data. If no data
     * has been received since the last call to requestAccelData(), this method
     * returns Double.NaN.
     * 
     * Acceleration is measured in meters per second squared
     * 
     * @return The y component of the last acceleration reading received, NaN if
     * there is no data.
     */
    public double getAccelY() {
        if (accel == null) {
            return Double.NaN;
        }
        else {
            return accel.y;
        }
    }
    
    
    /**
     * Get the z component of the last received acceleration data. If no data
     * has been received since the last call to requestAccelData(), this method
     * returns Double.NaN.
     *      
     * Acceleration is measured in meters per second squared
     * 
     * @return The z component of the last acceleration reading received, NaN if
     * there is no data.
     */
    public double getAccelZ() {
        if (accel == null) {
            return Double.NaN;
        }
        else {
            return accel.z;
        }
    }
    
    /**
     * Add a new listener for acceleration data. The AccelerationListener's
     * onAccelData() method will be called with the acceleration vector whenever
     * acceleration data is received.
     *
     * @param al The AccelerationListener to receive acceleration data.
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
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void requestTapData() throws IOException {
        this.tapData = null;
    }

    /**
     * Check the most recent tap detected
     *
     * @return The type of tap most recently detected. null if no request for
     * tap data has been sent or if there has not been any tap data received
     * from the CircuitPlayground since the last request was sent.
     */
    public Tap tapData() {
        return this.tapData;
    }

    /**
     * Request that the CircuitPlayground start continuously sending tap data.
     * Tap readings are sent every 20 milliseconds when enabled.
     *
     * @param enable True to begin streaming tap data, false to stop
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void streamTapData(boolean enable) throws IOException {

    }

    /**
     * Set the tap detection configuration
     *
     * @param detect What type of tap to detect, NONE, SINGLE, or DOUBLE.
     * Detecting DOUBLE taps also detects SINGLE taps.
     * @param sensitivity The required force for tap detection.
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void setTapConfiguration(Tap detect, AccelTapRange sensitivity) throws IOException {

    }

    /**
     * Add a new listener for tap data. The TapListener's onTap() method will be
     * called whenever tap data is received.
     *
     * @param tl The TapListener to receive tap data.
     */
    public final void addTapListener(TapListener tl) {

    }

    private void setTapData(Tap data) {
        this.tapData = data;
    }

    /*
     * Capacitive Touch Commands ***********************************************
     */
    int touchData[] = new int[13];

    /**
     * Request that the CircuitPlayground send the most recent touch data for a
     * specific pin. The data can be checked through subsequent calls to
     * getTouchReading or via a TouchListener
     *
     * @param pin The pin to get touch data for must be one of: 0, 1, 2, 3, 6,
     * 9, 10, or 12.
     
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void requestTouchReading(int pin) throws IOException {
        if (pin >= 0 && pin < touchData.length) {
            touchData[pin] = -1;
            sendMessage(CircuitPlaygroundMessageFactory.requestTouchReading(pin));
        } else {
            throw new IllegalArgumentException("Invalid pin: " + pin);
        }
    }

    /**
     * Get the most recently received touch data for a particular pin. If no
     * touch data has been received for this pin since the most recent call to
     * requestTouchReading, a value of -1 will be returned.
     *
     * @param pin The touch pin to check for data from, must be one of: 0, 1, 2,
     * 3, 6, 9, 10, or 12
     * @return The touch level received. This is raw data from the pin, in a
     * range from 0-2,147,483,647. Higher numbers mean stronger touches. If no
     * data has been received from the CircuitPlayground for this pin since
     * requestTouchReading was last called for this pin, a value of -1 will be
     * received.
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
     * enabled. Readings can be accessed by calling getTouchReading or adding a
     * TouchListener.
     *
     * @param pin The touch pin to check for data from, must be one of: 0, 1, 2,
     * 3, 6, 9, 10, or 12
     * @param enable True to begin streaming touch data, false to stop
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void streamTouchData(int pin, boolean enable) throws IOException {
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
                sendMessage(CircuitPlaygroundMessageFactory.streamTouchData(pin));

                break;
            default:
                throw new UnsupportedPinException("Specified pin does not support touch reading: " + pin);
        }
    }

    /**
     * Add a new listener for tap data. The TapListener's onTap() method will be
     * called whenever tap data is received.
     *
     * @param tl The TapListener to receive tap data.
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
     * 
     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void requestColorSense() throws IOException {
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

    @Override
    protected void handleEvent(Event event) {
        switch (event.getName()) {
            case TOUCH_MESSAGE:
                LOGGER.debug(event.getName());
                break;
            default:
                super.handleEvent(event);
        }
    }
    
    /**
     * Send a binary message, logging the message
     * 
     * @param message 
     * 
     * @throws IOException
     */
    @Override
    public void sendMessage(byte[] message) throws IOException {
        String MessageHex = "";
        
        for (byte b : message) {
            if (!MessageHex.isEmpty()) {
                MessageHex += ", ";
            }
            MessageHex += Integer.toHexString((int)b);
        }        
                
        LOGGER.info("Sending Message: [" + MessageHex + "]");
        super.sendMessage(message);
    }

    public static void main(String[] args) {
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            System.out.println(portName);
        }
        try {
            CircuitPlayground device = new CircuitPlayground("COM6");
            device.start();
            device.ensureInitializationIsDone();
            
            device.setNeoPixelBrightness(30);
            device.setNeoPixelColor(0, Color.red);
            device.setNeoPixelColor(1, Color.orange);
            device.setNeoPixelColor(2, Color.yellow);
            device.setNeoPixelColor(3, Color.green);
            device.setNeoPixelColor(4, Color.blue);
            device.setNeoPixelColor(5, Color.magenta);
            device.setNeoPixelColor(6, Color.pink);
            device.setNeoPixelColor(7, Color.darkGray);
            device.setNeoPixelColor(8, Color.lightGray);
            device.setNeoPixelColor(9, Color.white);
            device.showNeoPixels();
                        
            device.streamTouchData(12, true);
            device.playTone(1000, 100);
            
            JPinboard pinboard = new JPinboard(device);
            JFrame frame = new JFrame("Pinboard Example");
            frame.add(pinboard);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);

        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
        catch (InterruptedException e) {
            System.exit(0);
        }
    }
}
