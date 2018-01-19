package tk.perryma.circuitplaygroundfirmata4j;

import javax.vecmath.Vector3d;

/**
 * A single method interface for creating listeners that can receive 
 * CircuitPlayground acceleration data
 * 
 * @author bdahl
 */
public interface AccelerationListener {
    /**
     * Process acceleration data as it is received from the CircuitPlayground
     * 
     * @param accel
     *          The acceleration vector experienced by the CircuitPlayground
     */
    public void onAccelData(Vector3d accel);
}
