/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector3d;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import static org.firmata4j.firmata.parser.FirmataToken.PIN_ID;
import org.firmata4j.fsm.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCELERATION_MESSAGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_X;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_Y;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_Z;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_BLUE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_GREEN;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_MESSAGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_RED;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TAP_DATA;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TAP_MESSAGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TOUCH_LEVEL;
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
    private Pin led;

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
        addEventListener(new IODeviceStartAdapter() {
            @Override
            public void onStart(IOEvent ioe) {
                try {
                    clearNeoPixels();
                    showNeoPixels();
                    
                    // Connect hardwired peripherials to their pins
                    led = getPin(13);
                    led.setMode(Pin.Mode.OUTPUT);
                    led.setValue(0);
                    
                    lightSensor = getPin(23);
                    lightSensor.setMode(Pin.Mode.ANALOG);

                    temperatureSensor = getPin(18);
                    temperatureSensor.setMode(Pin.Mode.ANALOG);
                    
                    // Add a listener that will turn the analog reading to
                    // a temperature reading
                    temperatureSensor.addEventListener(new PinValueChangeAdapter(){
                        @Override
                        public void onValueChange(IOEvent event) {
                            final double THERM_SERIES_OHMS  = 10000.0;  // Resistor value in series with thermistor.
                            final double THERM_NOMINAL_OHMS = 10000.0;  // Thermistor resistance at 25 degrees C.
                            final double THERM_NOMIMAL_C    = 25.0;     // Thermistor temperature at nominal resistance.
                            final double THERM_BETA         = 3950.0;   // Thermistor beta coefficient.
                            int val = (int)event.getValue();
                            
                            if (val == 0) { 
                                tempC = Double.NaN;
                            }
                            else {    
                                // Convert analog values 0-1024 to temperature C
                                double resistance = ((1023.0 * 
                                        THERM_SERIES_OHMS) / val);
                                resistance -= THERM_SERIES_OHMS;
                                double steinhart = resistance /
                                        THERM_NOMINAL_OHMS;
                                steinhart = Math.log(steinhart);
                                steinhart /= THERM_BETA;
                                steinhart += 1.0 / (THERM_NOMIMAL_C + 273.15);
                                steinhart = 1.0 / steinhart;
                                steinhart -= 273.15;
                                tempC = steinhart;
                            }
                                                        
                            tempChangeListeners.forEach(tcl -> {
                                tcl.onTempChange(tempC);                                
                            });
                        }                        
                    });
                    
                    microphone = getPin(22);
                    microphone.setMode(Pin.Mode.ANALOG);
                    
                    // Add a listener that updates registered listeners
                    // with the microphone level
                    microphone.addEventListener(new PinValueChangeAdapter() {
                        @Override
                        public void onValueChange(IOEvent event) {
                            micLevel = (int)event.getValue();
                            
                            microphoneListeners.forEach(ml -> {
                                ml.onMicrophoneChange(micLevel);
                            });
                        }
                    });

                    leftButton = getPin(4);
                    leftButton.setMode(Pin.Mode.INPUT);
                    
                    // Add a listener that updates registered listeners with
                    // the button value
                    leftButton.addEventListener(new PinValueChangeAdapter() {
                        @Override
                        public void onValueChange(IOEvent event) {
                            leftVal = ((int)event.getValue() != 0);
                            
                            leftButtonListeners.forEach(bl -> {
                                bl.onChange(leftVal);
                            });

                        }                        
                    });

                    rightButton = getPin(19);
                    rightButton.setMode(Pin.Mode.INPUT);

                    // Add a listener that updates registered listeners with
                    // the button value
                    rightButton.addEventListener(new PinValueChangeAdapter() {
                        @Override
                        public void onValueChange(IOEvent event) {
                            rightVal = ((int)event.getValue() != 0);

                            rightButtonListeners.forEach(bl -> {
                                bl.onChange(rightVal);
                            });
                        }                        
                    });

                    slideSwitch = getPin(21);
                    slideSwitch.setMode(Pin.Mode.INPUT);

                    // Add a listener that updates registered listeners with
                    // the switch value
                    slideSwitch.addEventListener(new PinValueChangeAdapter() {
                        @Override
                        public void onValueChange(IOEvent event) {
                            switchVal = ((int)event.getValue() != 0);

                            switchListeners.forEach(bl -> {
                                bl.onChange(switchVal);
                            });
                        }                     
                    });
                } catch (IOException e) {
                }
            }
        });
        
        super.start();
    }
    
    /*
     * Simple Peripherials *****************************************************
     */
    
    /**
     * Turn the built-in LED on pin 13 on/off
     * 
     * @param on
     *          true if LED should turn on, false for off
     * 
     * @throws IOException 
     *          if the command cannot be sent to the Circuit Playground 
     */
    public void setLED(boolean on) throws IOException {
        if (on) {
            led.setValue(1);
        }
        else {
            led.setValue(0);
        }
    }
    
    public int getLightLevel() {
        return (int)lightSensor.getValue();
    }

    private double tempC;
    private final List<TemperatureChangeListener> tempChangeListeners =
            new ArrayList<>();
    
    /**
     * Get the current temperature in Celsius
     * 
     * @return the current temperature 
     */
    public double getTemperatureC() {
        return tempC;
    }
    
    /**
     * Get the current temperature in Fahrenheit
     * 
     * @return the current temperature
     */
    public double getTemperatureF() {
        if (Double.isNaN(tempC)) {
            return Double.NaN;
        }
        
        return tempC * 9.0 / 5.0 + 32.0;
    }
    
    /**
     * Add a listener that will receive updates on temperature changes
     * 
     * @param tcl
     *          The listener that will receive temperature change updates
     */
    public void addTempListener(TemperatureChangeListener tcl) {
        tempChangeListeners.add(tcl);
    }
    
    /**
     * Prevent a listener from receiving updates on temperature changes
     * 
     * @param tcl
     *          The listener that will no longer receive temperature change 
     *          updates
     */
    public void removeTempListener(TemperatureChangeListener tcl) {
        tempChangeListeners.remove(tcl);
    }
    
    /**
     * Prevent all listeners from receiving updates on temperature changes
     */
    public void removeAllTempListeners() {
        tempChangeListeners.clear();
    }
    
    private int micLevel;
    private final List<MicrophoneListener>
            microphoneListeners = new ArrayList<>();
    
    /**
     * Get the current level of the microphone input
     * 
     * @return a value from 0-1024 
     */
    public int getSoundLevel() {
        return micLevel;
    }
    
    /**
     * Add a listener to receive updates on the microphone level
     * 
     * @param ml
     *          The listener that will receive microphone level updates
     */
    public void addMicListener(MicrophoneListener ml) {
        microphoneListeners.add(ml);
    }
    
    /**
     * Stop a listener from receiving updates on the microphone level
     * 
     * @param ml 
     *          The listener that will no longer receive updates on the 
     *          microphone level
     */
    public void removeMicListener(MicrophoneListener ml) {
        microphoneListeners.remove(ml);
    }
    
    /**
     * Prevent all listeners from receiving updates on the microphone level
     */
    public void removeAllMicListeners() {
        microphoneListeners.clear();
    }
    
    private boolean leftVal;
    private boolean rightVal;
    private boolean switchVal;
    
    private final List<ButtonListener> leftButtonListeners = 
        new ArrayList<>();
    private final List<ButtonListener> rightButtonListeners = 
        new ArrayList<>();
    private final List<ButtonListener> switchListeners = 
        new ArrayList<>();
    
    
    /**
     * Determine the state of the left button.
     * 
     * @return true if the left button is pressed, false if it is released 
     */
    public boolean leftButtonPressed() {
        return leftButton.getValue() != 0;
    }
    
    /**
     * Add a listener that will receive updates about the left button.
     * 
     * @param bl 
     *          The listener that will receive updates on the left button. 
     */
    public void addLeftButtonListener(ButtonListener bl) {
        leftButtonListeners.add(bl);
    }

    /**
     * Prevent a listener from receiving updates about the left button.
     * 
     * @param bl
     *          The listener that will no longer receive updates about the 
     *          left button
     */
    public void removeLeftButtonListener(ButtonListener bl) {
        leftButtonListeners.remove(bl);
    }

    /**
     * Prevent all listeners from receiving updates about the left button.
     */
    public void removeAllLeftButtonListeners() {
        leftButtonListeners.clear();
    }
    
    /**
     * Determine the state of the right button.
     * 
     * @return true if the right button is pressed, false if it is released 
     */
    public boolean rightButtonPressed() {
        return rightButton.getValue() != 0;
    }
    
    /**
     * Add a listener that will receive updates about the right button.
     * 
     * @param bl 
     *          The listener that will receive updates on the right button. 
     */
    public void addRightButtonListener(ButtonListener bl) {
        rightButtonListeners.add(bl);
    }
    
    /**
     * Prevent a listener from receiving updates about the right button.
     * 
     * @param bl
     *          The listener that will no longer receive updates about the 
     *          right button
     */
    public void removeRightButtonListener(ButtonListener bl) {
        rightButtonListeners.remove(bl);
    }
    
    /**
     * Prevent all listeners from receiving updates about the right button.
     */
    public void removeAllRightButtonListeners() {
        rightButtonListeners.clear();
    }

    /**
     * Determine if the slide switch is on
     * 
     * @return true if the slide switch is on, false otherwise 
     */
    public boolean switchOn() {
        return slideSwitch.getValue() != 0;
    }

    /**
     * Add a listener that will receive updates on the slide switch
     * 
     * @param bl The listener that will receive updates about the slide switch 
     */
    public void addSwitchListener(ButtonListener bl) {
        switchListeners.add(bl);
    }
    
    /**
     * Prevent a listener from receiving updates about the slide switch
     * 
     * @param bl the listener to stop receiving updates about the slide switch 
     */
    public void removeSwitchListener(ButtonListener bl) {
        switchListeners.remove(bl);
    }
    
    /**
     * Prevent all listeners from receiving updates about the slide switch.
     */
    public void removeAllSwitchListeners() {
        switchListeners.clear();
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

    private Vector3d accel = null;
    private final List<AccelerationListener> accelListeners =
            new ArrayList<>();

    /**
     * Request a single accelerometer reading. The accelerometer reading can be
     * accessed by calling accelData() or by adding a listener with
     * addAccelerationListener().

     * @throws java.io.IOException 
     *          If command cannot be sent due to connection issues
     */
    public void requestAccelData() throws IOException {
        accel = null;
        sendMessage(CircuitPlaygroundMessageFactory.requestAccelData);
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

        if (enable) {
            sendMessage(CircuitPlaygroundMessageFactory.requestAccelStream);
        }
        else {
            sendMessage(CircuitPlaygroundMessageFactory.stopAccelStream);
        }
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
        byte sense = 0;
        
        switch(sensitivity) {
            case G2:
                sense = 0;
                break;
            case G4:
                sense = 1;
                break;
            case G8:
                sense = 2;
                break;
            case G16:
                sense = 3;
                break;
        }
        sendMessage(CircuitPlaygroundMessageFactory.setAccelerometerRange(sense));
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
        accelListeners.add(al);
    }
    
    /**
     * Prevent an AccelerationListener from receiving acceleration data
     * 
     * @param al The Acceleration to stop from receiving acceleration data
     */
    public final void removeAccelerationListener(AccelerationListener al) {
        accelListeners.remove(al);
    }
    
    /**
     * Prevent all AccelerationListeners from receiving acceleration data
     */
    public final void removeAllAccelerationListeners() {
        accelListeners.clear();
    }

    private void setAccelerationData(Vector3d data) {
        this.accel = data;
    }

    private void postAcceleration(Event event) {
        
        Vector3d accelVector = new Vector3d((Float)event.getBodyItem(ACCEL_X),
                (Float)event.getBodyItem(ACCEL_Y),
                (Float)event.getBodyItem(ACCEL_Z));
        
        setAccelerationData(accelVector);
        
        accelListeners.forEach((al) -> {
            al.onAccelData(accelVector);            
        });
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

    private Tap tapData = null;
    private final List<TapListener> tapListeners = new ArrayList<>();

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
        sendMessage(CircuitPlaygroundMessageFactory.requestTapData);
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
        if (enable) {
            sendMessage(CircuitPlaygroundMessageFactory.requestTapDataStream);
        }
        else {
            sendMessage(CircuitPlaygroundMessageFactory.stopTapDataStream);
        }
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
        int numTaps = 0;
        int threshold = 0;
        
        if (detect == Tap.SINGLE) {
            numTaps = 1;
        }
        else if (detect == Tap.DOUBLE) {
            numTaps = 2;
        }
        
        switch (sensitivity) {
            case G2:
                threshold = 80;
                break;
            case G4:
                threshold = 30;
                break;
            case G8:
                threshold = 15;
                break;
            case G16:
                threshold = 5;
                break;
        }
        
        sendMessage(CircuitPlaygroundMessageFactory.setTapConfiguration(numTaps, threshold));
    }

    /**
     * Add a new listener for tap data. The TapListener's onTap() method will be
     * called whenever tap data is received.
     *
     * @param tl The TapListener to receive tap data.
     */
    public final void addTapListener(TapListener tl) {
        tapListeners.add(tl);
    }
    
    /**
     * Prevent a TapListener from receiving tap data
     * 
     * @param tl the TapListener to stop from receiving tap data 
     */
    public final void removeTapListener(TapListener tl) {
        tapListeners.remove(tl);
    }
    
    /**
     * Prevent all TapListeners from receiving tap data
     */
    public final void removeAllTapListeners() {
        tapListeners.clear();
    }

    private void setTapData(Tap data) {
        this.tapData = data;
    }

    private void postTapEvent(Event event) {
        final Tap t;
        
        switch ((Integer)event.getBodyItem(TAP_DATA)) {
            case 0:
                t = Tap.NONE;
                break;
            case 1:
                t = Tap.SINGLE;
                break;
            case 2:
                t = Tap.DOUBLE;
                break;
            default:
                t = null;
        }
        setTapData(t);
        
        tapListeners.forEach((tl) -> {
            tl.onTap(t);
        });
    }
    
    /*
     * Capacitive Touch Commands ***********************************************
     */
    private final int touchData[] = new int[13];
    private final List<TouchListener> touchListeners = new ArrayList<>();

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
                if (enable) {
                    sendMessage(CircuitPlaygroundMessageFactory.requestTouchDataStream(pin));
                }
                else {
                    sendMessage(CircuitPlaygroundMessageFactory.stopTouchDataStream(pin));
                }
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
        touchListeners.add(tl);
    }
    
    /**
     * Stop a TouchListener from receiving touch data
     * 
     * @param tl the TouchListener to stop receiving touch data
     */
    public final void removeTouchListener(TouchListener tl) {
        touchListeners.remove(tl);
    }
    
    /**
     * Stop all touch listeners from receiving touch data
     */
    public final void removeAllTouchListeners() {
        touchListeners.clear();
    }

    private void setTouchData(int pin, int data) {
        this.touchData[pin] = data;
    }
    
    private void postTouchEvent(Event event) {
        int pin = (Byte)event.getBodyItem(PIN_ID);
        int level = (Integer)event.getBodyItem(TOUCH_LEVEL);
        
        setTouchData(pin, level);
        
        touchListeners.forEach((tl) -> {
            tl.onTouch(pin, level);
        });
    }

    /*
     * Color Sensing Commands **************************************************
     */
    private Color sensed = null;
    private final List<ColorListener> colorListeners = new ArrayList<>();

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
        sendMessage(CircuitPlaygroundMessageFactory.requestColorSense);
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

    /**
     * Add a new listener for Color sensing data. The ColorListener's onColor() 
     * method will be called whenever color sense data is received.
     *
     * @param cl The ColorListener to receive color data
     */
    public void addColorListener(ColorListener cl) {
        colorListeners.add(cl);
    }
    
    /**
     * Stop a color listener from receiving color data.
     * 
     * @param cl The color listener to remove
     */
    public void removeColorListener(ColorListener cl) {
        colorListeners.remove(cl);
    }    
    
    /**
     * Stop all color listeners from receiving color data
     */
    public void removeAllColorListeners() {
        colorListeners.clear();
    }
    
    private void setSensedColor(Color c) {
        sensed = c;
    }
    
    private void postSensedColor(Event event) {
        Color c = new Color((Byte)event.getBodyItem(COLOR_RED) & 0xFF,
            (Byte)event.getBodyItem(COLOR_GREEN) & 0xFF,
            (Byte)event.getBodyItem(COLOR_BLUE) & 0xFF);
        
        setSensedColor(c);
        
        colorListeners.forEach((cl) -> {
            cl.onColor(c);
        });
    }
    
    @Override
    protected void handleEvent(Event event) {
        switch (event.getName()) {
            case ACCELERATION_MESSAGE:
                postAcceleration(event);
                break;
                
            case TOUCH_MESSAGE:
                postTouchEvent(event);
                break;
                
            case TAP_MESSAGE:
                postTapEvent(event);
                break;
                
            case COLOR_MESSAGE:
                postSensedColor(event);
                break;
                
            default:
                super.handleEvent(event);
        }
    }
}
