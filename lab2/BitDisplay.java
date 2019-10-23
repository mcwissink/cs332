import java.awt.event.*;
import javax.swing.*;

/**
 * BitDisplay creates a front-end for a BitListener. This will allow sending
 * bitstrings to the handler and displaying bitstrings (or collision messages).
 */
public class BitDisplay implements ActionListener, BitListener {
    private BitHandler handler;
    private JTextField receiveField;
    private JTextField sendField;

    /**
     * BitDisplay constructor creates a swing UI for the BitDisplay to use
     * 
     * @param handler
     */
    public BitDisplay(BitHandler handler) {
        this.handler = handler;

        JFrame frame = new JFrame(handler.toString());
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        receiveField = new JTextField(20);
        receiveField.setEditable(false);
        frame.getContentPane().add(receiveField);

        frame.getContentPane().add(new JLabel("Type in 0s and 1s below: hit enter to send"));
        sendField = new JTextField(20);
        sendField.addActionListener(this);
        frame.getContentPane().add(sendField);

        frame.pack();
        frame.setVisible(true);
        handler.setListener(this);
    }

    /**
     * When text is entered in the text field, start a thread and attempt to
     * broadcast the string
     */
    public void actionPerformed(ActionEvent e) {
        new Thread() {
            public void run() {
                try {
                    handler.broadcast(sendField.getText());
                } catch (CollisionException e) {
                    receiveField.setText("Collision!");
                }

                // System.out.println("actionPerformed: done sending " + sendField.getText());
            }
        }.start();
        sendField.selectAll();
    }

    /**
     * Set the text for the UI display to the received bits
     */
    public void bitsReceived(BitHandler h, String bits) {
        receiveField.setText(bits);
    }
}
