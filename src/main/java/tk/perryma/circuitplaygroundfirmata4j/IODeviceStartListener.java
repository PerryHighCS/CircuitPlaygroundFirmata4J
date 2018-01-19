/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j;

import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;

/**
 *
 * @author dahlem.brian
 */
public abstract class IODeviceStartListener implements IODeviceEventListener {

    @Override
    public abstract void onStart(IOEvent ioe);

    @Override
    public void onStop(IOEvent ioe) {}

    @Override
    public void onPinChange(IOEvent ioe) {}

    @Override
    public void onMessageReceive(IOEvent ioe, String string) {}
    
}
