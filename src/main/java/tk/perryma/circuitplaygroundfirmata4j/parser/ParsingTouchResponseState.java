package tk.perryma.circuitplaygroundfirmata4j.parser;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import static org.firmata4j.firmata.parser.FirmataToken.FIRMATA_MESSAGE_EVENT_TYPE;
import static org.firmata4j.firmata.parser.FirmataToken.PIN_ID;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.parseByte;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.parseInt;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.FiniteStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TOUCH_LEVEL;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TOUCH_MESSAGE;

/**
 *
 * @author dahlem.brian
 */
public class ParsingTouchResponseState extends AbstractState {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingTouchResponseState.class);
        
    public ParsingTouchResponseState(FiniteStateMachine fsm) {
        super(fsm);
    }
    
    @Override
    public void process(byte b) {
        if (b == END_SYSEX) {
            byte[] buffer = getBuffer();
            
            if (buffer.length < 11) {
                LOGGER.debug("Invalid Touch Response received. Ignoring.");
                transitTo(WaitingForMessageState.class);
                return;
            }

            Event evt = new Event(TOUCH_MESSAGE, FIRMATA_MESSAGE_EVENT_TYPE);

            evt.setBodyItem(PIN_ID, parseByte(buffer[1], buffer[2]));                
            int level = parseInt(buffer, 3) >> 1;                
            evt.setBodyItem(TOUCH_LEVEL, level);

            LOGGER.debug("Touch event on pin [" + evt.getBodyItem(PIN_ID) + "] :" + evt.getBodyItem(TOUCH_LEVEL));
            publish(evt);
            transitTo(WaitingForMessageState.class);                
        }
        else {
            bufferize(b);
        }        
    }    
}
