package tk.perryma.circuitplaygroundfirmata4j.parser;

import static org.firmata4j.firmata.parser.FirmataToken.END_SYSEX;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import static org.firmata4j.firmata.parser.FirmataToken.FIRMATA_MESSAGE_EVENT_TYPE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.MIDIParser.parseByte;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.Event;
import org.firmata4j.fsm.FiniteStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_BLUE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_GREEN;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_MESSAGE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_RED;

/**
 *
 * @author dahlem.brian
 */
public class ParsingColorSenseResponseState extends AbstractState {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingColorSenseResponseState.class);
        
    public ParsingColorSenseResponseState(FiniteStateMachine fsm) {
        super(fsm);
    }
    
    @Override
    public void process(byte b) {
        if (b == END_SYSEX) {
            byte[] buffer = getBuffer();
            
            if (buffer.length < 7) {
                LOGGER.debug("Invalid Color Sense Response received. Ignoring.");
                transitTo(WaitingForMessageState.class);
                return;
            }

            Event evt = new Event(COLOR_MESSAGE, FIRMATA_MESSAGE_EVENT_TYPE);

            evt.setBodyItem(COLOR_RED, parseByte(buffer[1], buffer[2]));
            evt.setBodyItem(COLOR_GREEN, parseByte(buffer[3], buffer[4]));
            evt.setBodyItem(COLOR_BLUE, parseByte(buffer[5], buffer[6]));                

            LOGGER.debug("Color sensed [" + 
                    ((Byte)evt.getBodyItem(COLOR_RED) & 0xFF) + ", " + 
                    ((Byte)evt.getBodyItem(COLOR_GREEN) & 0xFF) + ", " + 
                    ((Byte)evt.getBodyItem(COLOR_BLUE) & 0xFF) + "]");
            
            publish(evt);
            transitTo(WaitingForMessageState.class);                
        }
        else {
            bufferize(b);
        }        
    }    
}
