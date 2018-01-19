/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitpythonfirmata4j;

import org.firmata4j.firmata.FirmataDevice;

/**
 *
 * @author bdahl
 */
public class CircuitPlayground extends FirmataDevice {
    
    public CircuitPlayground(String portName) {
        super(portName);
    }
    
}
