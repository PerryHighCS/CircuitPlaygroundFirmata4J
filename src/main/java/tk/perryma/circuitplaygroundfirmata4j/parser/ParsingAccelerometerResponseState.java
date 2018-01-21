/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j.parser;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import static org.firmata4j.firmata.parser.FirmataToken.FIRMATA_MESSAGE_EVENT_TYPE;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.FiniteStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCELERATION_MESSAGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_X;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_Y;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_Z;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.parseFloat;


/**
 *
 * @author dahlem.brian
 */
public class ParsingAccelerometerResponseState extends AbstractState {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingAccelerometerResponseState.class);
    
    public ParsingAccelerometerResponseState(FiniteStateMachine fsm) {
        super(fsm);
    }
    
    @Override
    public void process(byte b) {
        if (b == END_SYSEX) {
            byte[] buffer = getBuffer();
            
            if (buffer.length < 25) {
                LOGGER.debug("Invalid Acceleration Response received. Ignoring.");
                transitTo(WaitingForMessageState.class);
                return;
            }

            Event evt = new Event(ACCELERATION_MESSAGE, FIRMATA_MESSAGE_EVENT_TYPE);

            evt.setBodyItem(ACCEL_X, parseFloat(buffer, 1));                
            evt.setBodyItem(ACCEL_Y, parseFloat(buffer, 9));                
            evt.setBodyItem(ACCEL_Z, parseFloat(buffer, 17));                            

            LOGGER.debug("Acceleration event: <" + evt.getBodyItem(ACCEL_X) + 
                    ", " + evt.getBodyItem(ACCEL_Y) +
                    ", " + evt.getBodyItem(ACCEL_Z) + ">");
            publish(evt);
            transitTo(WaitingForMessageState.class);                
        }
        else {
            bufferize(b);
        }        
    }    
}
