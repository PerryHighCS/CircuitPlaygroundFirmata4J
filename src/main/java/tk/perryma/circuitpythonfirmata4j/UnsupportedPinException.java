package tk.perryma.circuitpythonfirmata4j;

/**
 * Thrown to indicate that an operation has been requested for a pin that does
 * not support that operation.
 */
public class UnsupportedPinException extends IllegalArgumentException {

    public UnsupportedPinException(String string) {
        super(string);
    }
    
}
