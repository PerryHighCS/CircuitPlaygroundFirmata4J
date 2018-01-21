/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.perryma.circuitplaygroundfirmata4j.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 *
 * @author bdahl
 */
public class ColorButton extends JLabel {
    private Color color;
    private final int num;
    
    public ColorButton(int num, Color color) {
        super();
        this.color = color;
        this.num = num;
        
        setSize(39, 39);
        
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval(0, 0, getWidth(), getHeight());
    }
    
    public void setColor(Color c) {
        color = c;
        repaint();
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getNum() {
        return num;
    }
}
