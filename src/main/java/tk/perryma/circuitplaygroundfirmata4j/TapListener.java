package tk.perryma.circuitplaygroundfirmata4j;

import tk.perryma.circuitplaygroundfirmata4j.CircuitPlayground.Tap;

/**
 * A single method interface that allows processing of taps on the Circuit 
 * Playground
 * 
 * @author bdahl
 */
public interface TapListener {
    /**
     * Process a tap on the Circuit Playground.
     * 
     * @param tap
     *          The type of tap detected
     */
    public void onTap(Tap tap);
}
