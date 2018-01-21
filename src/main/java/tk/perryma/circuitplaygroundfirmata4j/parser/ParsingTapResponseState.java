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
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TAP_DATA;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TAP_MESSAGE;

/**
 *
 * @author dahlem.brian
 */
public class ParsingTapResponseState extends AbstractState {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingTapResponseState.class);
        
    public ParsingTapResponseState(FiniteStateMachine fsm) {
        super(fsm);
    }
    
    @Override
    public void process(byte b) {
        if (b == END_SYSEX) {
            byte[] buffer = getBuffer();
            
            if (buffer.length < 3) {
                LOGGER.debug("Invalid Tap Response received. Ignoring.");
                transitTo(WaitingForMessageState.class);
                return;
            }

            int numTaps = 0;
            if ((buffer[1] & 0b00100000) != 0) {
                numTaps = 2;
            }
            else if ((buffer[1] & 0b00010000) != 0) {
                numTaps = 1;
            }
            
            Event evt = new Event(TAP_MESSAGE, FIRMATA_MESSAGE_EVENT_TYPE);

            evt.setBodyItem(TAP_DATA, numTaps);                
            
            LOGGER.debug("Tap event: " + evt.getBodyItem(TAP_DATA) + " taps");
            
            publish(evt);
            transitTo(WaitingForMessageState.class);                
        }
        else {
            bufferize(b);
        }        
    }    
}
