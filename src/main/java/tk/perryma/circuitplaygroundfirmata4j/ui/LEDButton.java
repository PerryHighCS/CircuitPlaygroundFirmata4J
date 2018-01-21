package tk.perryma.circuitplaygroundfirmata4j.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 *
 * @author bdahl
 */
public class LEDButton extends JLabel {
    private boolean on;
    
    public LEDButton() {
        super();
        
        setSize(15, 15);
        
        repaint();
    }
    
    public void setOn(boolean on) {
        this.on = on;        
        repaint();
    }
    
    public void toggle() {
        on = !on;
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        if (on) {
            g.setColor(Color.red);
        }
        else {
            g.setColor(Color.black);
        }
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    public boolean isOn() {
        return on;
    }
}
