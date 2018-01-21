/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j;

import org.firmata4j.IOEvent;
import org.firmata4j.PinEventListener;

/**
 *
 * @author bdahl
 */
public abstract class PinValueChangeAdapter implements PinEventListener {

    @Override
    public void onModeChange(IOEvent event) { }

    @Override
    public abstract void onValueChange(IOEvent event);
}
