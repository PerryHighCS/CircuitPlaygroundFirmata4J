package tk.perryma.circuitplaygroundfirmata4j;

/**
 * A single method interface that allows processing of capacitive touch on the
 * CircuitPlayground's pin
 * 
 * @author bdahl
 */
public interface TouchListener {
    /**
     * Process a touch on the Circuit Playground.
     * 
     * @param pin
     *          the pin that received touch data
     * @param data
     *          the strength of the touch on that pin - higher numbers are 
     *          stronger touches
     */
    public void onTouch(int pin, int data);
}
