package tk.perryma.circuitplaygroundfirmata4j.parser;

import org.firmata4j.firmata.parser.WaitingForMessageState;
import static org.firmata4j.firmata.parser.FirmataToken.FIRMATA_MESSAGE_EVENT_TYPE;
import static org.firmata4j.firmata.parser.FirmataToken.PIN_ID;
import static org.firmata4j.firmata.parser.FirmataToken.TOUCH_LEVEL;
import static org.firmata4j.firmata.parser.FirmataToken.TOUCH_MESSAGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.parseByte;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.parseInt;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.FiniteStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dahlem.brian
 */
public class ParsingTouchResponseState extends AbstractState {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingTouchResponseState.class);
    
    private int count = 0;
    
    public ParsingTouchResponseState(FiniteStateMachine fsm) {
        super(fsm);
    }
    
    @Override
    public void process(byte b) {
        if (count == 11) {
            byte[] buffer = getBuffer();

            Event evt = new Event(TOUCH_MESSAGE, FIRMATA_MESSAGE_EVENT_TYPE);

            evt.setBodyItem(PIN_ID, parseByte(buffer[1], buffer[2]));                
            int level = parseInt(buffer, 3) >> 1;                
            evt.setBodyItem(TOUCH_LEVEL, level);

            LOGGER.info("Touch event on pin [" + evt.getBodyItem(PIN_ID) + "] :" + evt.getBodyItem(TOUCH_LEVEL));
            publish(evt);
            transitTo(WaitingForMessageState.class);                
            count = 0;
        }
        else {
            bufferize(b);
            count++;
        }        
    }    
}
