import java.awt.event.*;
import javax.swing.*;

/**
 * BitDisplay
 * A display for a BitHandler
 */
public class BitDisplay implements ActionListener, BitListener {
    private BitHandler handler;
    private JTextField receiveField;
    private JTextField sendField;

    /**
     * Constructor that connecst a handler to the display
     * @param handler a BitHandler to display
     */
    public BitDisplay(BitHandler handler) {
	this.handler = handler;

	JFrame frame = new JFrame(handler.toString());
	frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

	receiveField = new JTextField(20);
	receiveField.setEditable(false);
	frame.getContentPane().add(receiveField);

	sendField = new JTextField(20);
	sendField.addActionListener(this);
	frame.getContentPane().add(sendField);

	frame.pack();
	frame.setVisible(true);
	handler.setListener(this);
    }

    /**
     * ActionListener for the sendField. Broadcast the sendField text
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
	new Thread() {
	    public void run() {
		try {
		    handler.broadcast(sendField.getText());
		    // System.out.println("actionPerformed: done sending " + sendField.getText());
		} catch(CollisionException ce) {
		    receiveField.setText("Collision");
		}
	    }
	}.start();
	sendField.selectAll();
    }

    /**
     * BitListener implementation 
     * @param h unused
     * @param bits the bit values that have been received
     */
    public void bitsReceived(BitHandler h, String bits) {
	receiveField.setText(bits);
    }
}
