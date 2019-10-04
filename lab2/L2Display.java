import java.awt.event.*;
import javax.swing.*;

public class L2Display implements ActionListener, L2Listener {
	private L2Handler handler;
	private JTextField displayField;
	private JTextField addressField;
	private JTextField payloadField;

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

	public void frameRecieved(L2Handler h, L2Frame f){
        System.out.println(f.getPayload());
    }

	public void actionPerformed(ActionEvent e) {
		displayField.setText("Sending...");
		new Thread() {
			public void run() {
				handler.send(new L2Frame(0, 1, 0, "hello"));
			}
		}.start();
	}

}
