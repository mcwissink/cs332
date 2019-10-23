import java.awt.event.*;
import javax.swing.*;

public class L2Display implements ActionListener, L2Listener {
    private L2Handler handler;
    private JTextField displayField;
    private JTextField addressField;
    private JTextField payloadField;

    /**
     * Constructor for L2Display L2Display displays layer 2 packets
     *
     * @param handler The L2 Handler
     */
    public L2Display(L2Handler handler) {
        this.handler = handler;
        handler.setListener(this);

        JFrame frame = new JFrame(handler.toString());
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        displayField = new JTextField(20);
        displayField.setEditable(false);
        frame.getContentPane().add(displayField);

        frame.getContentPane().add(new JLabel("Address: " + this.handler.toString()));

        addressField = new JTextField(20);
        addressField.addActionListener(this);
        frame.getContentPane().add(addressField);

        frame.getContentPane().add(new JLabel("Payload:"));

        payloadField = new JTextField(20);
        payloadField.addActionListener(this);
        frame.getContentPane().add(payloadField);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Displays a recieved layer 2 packet (frame)
     *
     * @param h The L2Handler
     * @param f The L2Frame to display
     */
    public void frameReceived(L2Handler h, L2Frame f) {
        String display;
        display = "source: " + f.getSource() + ", payload: " + f.getPayload();
        displayField.setText(display);
    }

    /**
     * Send the frame in the addressField
     *
     * @param e The ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        displayField.setText("Sending...");
        new Thread() {
            public void run() {
                handler.send(new L2Frame(Integer.parseInt(addressField.getText()), handler.getMacAddr(), 0,
                        payloadField.getText()));
            }
        }.start();
    }

}
