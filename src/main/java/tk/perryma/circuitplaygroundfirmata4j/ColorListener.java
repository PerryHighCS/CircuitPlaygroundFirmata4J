package tk.perryma.circuitplaygroundfirmata4j;

import java.awt.Color;

/**
 * A single method interface that allows processing of color sensing data from
 * the Circuit Playground
 * 
 * @author bdahl
 */
public interface ColorListener {
    /**
     * Process a newly sensed color from the color sense macro
     * 
     * @param c 
     *          The sensed color
     */
    public void onColor(Color c);
}
