package tk.perryma.circuitplaygroundfirmata4j.ui;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.Pin.Mode;
import org.firmata4j.PinEventListener;
import tk.perryma.circuitplaygroundfirmata4j.CircuitPlayground;

/**
 *
 * @author bdahl
 */
public class PinConfig extends javax.swing.JPanel implements PinEventListener {

    private CircuitPlayground cp;
    private Pin pin;
    private int pinNum;
    
    /**
     * Creates new PinConfig panel
     */
    public PinConfig() {
        this(null, -1);
    }
    
    public PinConfig(CircuitPlayground cp, int pinNum) {
        this.cp = cp;
        this.pinNum = pinNum;
        
        initComponents();
        
        if (cp != null) { 
            this.pin = cp.getPin(pinNum);
            
            for (Mode m : pin.getSupportedModes()) {
                modeSel.addItem(m.toString());
            }
            modeSel.setSelectedItem(pin.getMode().toString());
        
            modeSel.addActionListener((evt) -> {
                String newMode = (String)modeSel.getSelectedItem();
                System.out.println("Pin " + pinNum + " to " + newMode);
                try {
                    pin.setMode(Mode.valueOf(newMode));
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                        "Could not communicate with Circuit Playground",
                        "Communications Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
    
    @Override
    public void onModeChange(IOEvent event) {
        showMode(event.getPin().getMode());
    }

    private void showMode(Mode m) {
        int x = panel.getX();
        int y = panel.getY();
        
        remove(panel);
              
        switch (m) {
            case PULLUP:
            case INPUT:
                panel = new DigitalInput(pin);
                break;
            case ANALOG:
                panel = new AnalogInput(pin);
                break;
            case OUTPUT:
                panel = new DigitalOutput(pin);
                break;
                
        }
        
        add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, -1, -1));
        revalidate();
        repaint();
        
        panel.repaint();
        panel.showValue(pin.getValue());
        
    }
    
    @Override
    public void onValueChange(IOEvent event) {
        panel.showValue(event.getValue());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pinNumLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        modeSel = new javax.swing.JComboBox<>();
        panel = new tk.perryma.circuitplaygroundfirmata4j.ui.PinPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setMaximumSize(new java.awt.Dimension(300, 100));
        setMinimumSize(new java.awt.Dimension(300, 100));
        setPreferredSize(new java.awt.Dimension(300, 100));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pinNumLabel.setText("Pin: " + pinNum);
        add(pinNumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 17, -1, -1));

        jLabel2.setText("Mode:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 17, -1, -1));

        add(modeSel, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 14, 100, -1));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 43, Short.MAX_VALUE)
        );

        add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 43, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    public void showPin() {
        pin.addEventListener(this);
        showMode(pin.getMode());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox<String> modeSel;
    private tk.perryma.circuitplaygroundfirmata4j.ui.PinPanel panel;
    private javax.swing.JLabel pinNumLabel;
    // End of variables declaration//GEN-END:variables
}
