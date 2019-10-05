import java.awt.event.*;
import javax.swing.*;

/**
 * this class implements a LightDisplay that references a LightPanel, contains
 * the UI elements to show the status of the light and to turn it on and off
 */
public class LightDisplay extends Thread implements ActionListener {
    private LightPanel panel;
    private ImageIcon lightOffIcon;
    private ImageIcon lightOnIcon;
    private JLabel lightLabel;

    /**
     * Constructor Set up the LightDisplay
     * 
     * @param panel
     */
    public LightDisplay(LightPanel panel) {
        this.panel = panel;

        JFrame frame = new JFrame();
        frame.setTitle(panel.toString());

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));

        lightOffIcon = new ImageIcon("lightoff.gif");
        lightOnIcon = new ImageIcon("lighton.gif");

        lightLabel = new JLabel(lightOffIcon);
        lightLabel.setBorder(BorderFactory.createEtchedBorder());
        frame.getContentPane().add(lightLabel);

        JButton onButton = new JButton("ON");
        onButton.setMnemonic(KeyEvent.VK_N);
        onButton.setActionCommand("on");
        onButton.addActionListener(this);
        frame.getContentPane().add(onButton);

        JButton offButton = new JButton("OFF");
        offButton.setMnemonic(KeyEvent.VK_F);
        offButton.setActionCommand("off");
        offButton.addActionListener(this);
        frame.getContentPane().add(offButton);

        frame.pack();
        frame.setVisible(true);

        start();
    }

    /**
     * When an event occurs, check the status of the event if it is on -> switch the
     * panel to on and vice versa
     * 
     * @param e: event that happens (switch flipped on or off)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("on"))
            panel.switchOn();
        else
            panel.switchOff();
    }

    /**
     * Continuously check if the light is set to on, if it is -> set set the icon to
     * the on picture and vice versa
     */
    public void run() {
        while (true) {
            if (panel.isOn())
                lightLabel.setIcon(lightOnIcon);
            else
                lightLabel.setIcon(lightOffIcon);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }
}
