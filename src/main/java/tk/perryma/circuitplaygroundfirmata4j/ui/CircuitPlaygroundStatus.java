package tk.perryma.circuitplaygroundfirmata4j.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
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
    LEDButton led;
    ColorButton[] neoPixel;
    
    public CircuitPlaygroundStatus(CircuitPlayground cp) {
        super();
        // Initialize LED display
        led = new LEDButton();
        led.setLocation(479, 82);
        led.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                led.toggle();
                
                try {
                    cp.setLED(led.isOn());
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(led, 
                        "Could not toggle LED.", ex.toString(),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(led);
        
        // Initialize NeoPixel colors
        neoPixel = new ColorButton[10];
        for (int i = 0; i < neoPixel.length; i++) {
            neoPixel[i] = new ColorButton(i, Color.BLACK);
            add(neoPixel[i]);
            neoPixel[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ColorButton source = ((ColorButton)e.getSource());
                    
                    Color c = JColorChooser.showDialog(source, 
                            "Choose a color", source.getColor());
                    
                    if (c != null) {
                        try {
                            source.setColor(c);
                            cp.setNeoPixelColor(source.getNum(), c);
                            cp.showNeoPixels();
                        }
                        catch (IOException ex) {
                            JOptionPane.showMessageDialog(source, 
                                    "Could not change color", ex.toString(),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }
        neoPixel[0].setLocation(258, 169);
        neoPixel[1].setLocation(174, 259);
        neoPixel[2].setLocation(142, 377);
        neoPixel[3].setLocation(174, 501);
        neoPixel[4].setLocation(267, 587);
        neoPixel[5].setLocation(500, 583);
        neoPixel[6].setLocation(590, 490);
        neoPixel[7].setLocation(618, 381);
        neoPixel[8].setLocation(588, 262);
        neoPixel[9].setLocation(498, 170);
        
        try {
            // Set the background
            background = ImageIO.read(this.getClass().getResource("/img/circuitPlayground.png"));
            setSize(background.getWidth(null), background.getHeight(null));
            
            // Initialize button states
            leftButton = cp.leftButtonPressed();
            rightButton = cp.rightButtonPressed();
            slideSwitch = cp.switchOn();
        }
        catch (IOException e) {}
        
        // Add listeners to autoupdate button displays
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
