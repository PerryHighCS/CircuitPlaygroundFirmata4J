package tk.perryma.circuitplaygroundfirmata4j.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import tk.perryma.circuitplaygroundfirmata4j.CircuitPlayground;

/**
 *
 * @author bdahl
 */
public class CircuitPlaygroundStatus extends JPanel {
    private Image background = null;
    boolean leftButton;
    boolean rightButton;
    boolean slideSwitch;
    
    public CircuitPlaygroundStatus(CircuitPlayground cp) {
        super();
        
        try {
            background = ImageIO.read(this.getClass().getResource("/img/circuitPlayground.png"));
        
            setSize(background.getWidth(null), background.getHeight(null));
            leftButton = cp.leftButtonPressed();
            rightButton = cp.rightButtonPressed();
            slideSwitch = cp.switchOn();
        }
        catch (IOException e) {}
        
        cp.addLeftButtonListener((val) -> {
            leftButton = val;
            showLeftButton(null);
        });
        
        cp.addRightButtonListener((val) -> {
            rightButton = val;
            showRightButton(null);
        });
        
        cp.addSwitchListener((val) -> {
            slideSwitch = val;
            showSlideSwitch(null);
        });

    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(background, 0, 0, this);
        
        showLeftButton(g);
        showRightButton(g);
        showSlideSwitch(g);
    }

    private void showLeftButton(Graphics g) {
        boolean dispose = false;
        if (g == null) {
            g = this.getGraphics();
            dispose = true;
        }
        
        if (leftButton) {
            g.setColor(Color.GREEN);
        }
        else {
            g.setColor(Color.BLACK);
        }
        g.fillOval(238, 367, 42, 42);
        
        if (dispose) {
            g.dispose();
        }
    }

    private void showRightButton(Graphics g) {
        boolean dispose = false;
        if (g == null) {
            g = this.getGraphics();
            dispose = true;
        }
        
        if (rightButton) {
            g.setColor(Color.GREEN);
        }
        else {
            g.setColor(Color.BLACK);
        }
        g.fillOval(520, 367, 42, 42);
        
        if (dispose) {
            g.dispose();
        }
    }

    private void showSlideSwitch(Graphics g) {
        boolean dispose = false;
        if (g == null) {
            g = this.getGraphics();
            dispose = true;
        }
        
        if (slideSwitch) {
            g.setColor(Color.GREEN);
        }
        else {
            g.setColor(Color.BLACK);
        }
        g.fillRect(379, 522, 41, 14);
        
        if (dispose) {
            g.dispose();
        }
    }
}
