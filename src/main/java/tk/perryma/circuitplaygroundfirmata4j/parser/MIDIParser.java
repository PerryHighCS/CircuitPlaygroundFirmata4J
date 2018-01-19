/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j.parser;

/**
 *
 * @author dahlem.brian
 */
public class MIDIParser {
    public static byte parseByte(byte b1, byte b2) {
        return (byte)((b1 & 0x7f) | ((b2 & 0x01) << 7));
    }
    
    public static int parseInt(byte[] b, int startidx) {
        if (b.length < startidx + 8) {
            throw new IllegalArgumentException("Invalid MIDI integer");
        }
        int val = 0;
        
        for (int i = startidx + 7; i > 0; i -= 2) {
            val <<= 8;
            val |= (b[i - 1] & 0x7F);
            val |= (b[i] & 0x01) << 7;
        }
        
        return val;
    }
}
