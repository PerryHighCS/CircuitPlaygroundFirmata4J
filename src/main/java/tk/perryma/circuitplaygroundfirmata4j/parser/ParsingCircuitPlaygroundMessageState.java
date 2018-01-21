/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j.parser;

import java.util.HashMap;
import java.util.Map;
import org.firmata4j.firmata.parser.WaitingForMessageState;
import org.firmata4j.fsm.AbstractState;
import org.firmata4j.fsm.FiniteStateMachine;
import org.firmata4j.fsm.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.ACCEL_RESPONSE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.COLOR_SENSE_RESPONSE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TAP_RESPONSE;
import static tk.perryma.circuitplaygroundfirmata4j.parser.CircuitPlaygroundToken.TOUCH_RESPONSE;

/**
 *
 * @author dahlem.brian
 */
public class ParsingCircuitPlaygroundMessageState extends AbstractState {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingCircuitPlaygroundMessageState.class);
    private static final Map<Byte, Class<? extends State>> STATES;
    
    static {
        STATES = new HashMap<>();
        STATES.put(TOUCH_RESPONSE , ParsingTouchResponseState.class);
        STATES.put(ACCEL_RESPONSE, ParsingAccelerometerResponseState.class); 
        STATES.put(COLOR_SENSE_RESPONSE, ParsingColorSenseResponseState.class);
        STATES.put(TAP_RESPONSE, ParsingTapResponseState.class);
    }
    
    public ParsingCircuitPlaygroundMessageState(FiniteStateMachine fsm) {
        super(fsm);
    }

    @Override
    public void process(byte b) {
        Class<? extends State> nextState = STATES.get(b);
        if (nextState == null) {
            LOGGER.error("Unsupported Circuit Playground message {}.", b);
            nextState = WaitingForMessageState.class;
        }
        LOGGER.debug("CP Message: " + nextState.getSimpleName());
        transitTo(nextState);
    }
}

