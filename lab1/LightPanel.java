import java.io.*;
import java.net.*;
import java.util.*;

/**
 * LightPanel
 * A client for the SocketSystem server
 */
public class LightPanel extends Thread {
    private static Set idsUsed = new HashSet();

    private int id;
    private Socket socket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;
    private boolean isHigh = false;

    /**
     * Default constructor that uses the DEFAULT PORT value from the LightSystem
     */
    public LightPanel() {
	this("localhost", LightSystem.DEFAULT_PORT);
    }

    /**
     * Constructor that sets up the client
     * @param host the host server
     * @param port the port that the server is listening on
     */
    public LightPanel(String host, int port) {
	do {
	    id = LightSystem.getRandom().nextInt(15) + 1;
	} while (!idsUsed.add(new Integer(id)));

	try {
	    socket = new Socket(host, port);
	    socketOut = new PrintWriter(socket.getOutputStream(), true);
	    socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    start();
	} catch (UnknownHostException e) {
	    throw new RuntimeException("Invalid host:  " + host);
	} catch (IOException e) {
	    throw new RuntimeException("Unable to connect to LightSystem");
	}
    }

    /**
     * 
     */
    public void switchOn() {
	socketOut.println(LightSystem.HIGH);
    }

    public void switchOff() {
	socketOut.println(LightSystem.LOW);
    }

    /**
     * Properly close and clean up the socket
     */
    public void close() {
	try {
	    socketOut.close();
	    socketIn.close();
	    socket.close();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Run the light panel client
     */
    public void run() {
	try {
	    String line = socketIn.readLine();
	    while (line != null) {
		if (line.equals(LightSystem.HIGH))
		    isHigh = true;
		else if (line.equals(LightSystem.LOW))
		    isHigh = false;
		line = socketIn.readLine();
	    }
	} catch (Exception e) {
	    System.out.println("LightPanel disconnected");
	    throw new RuntimeException(e);
	}
    }

    public boolean isOn() {
	return isHigh;
    }

    public String toString() {
	return "#" + id;
    }

    public int getID() {
	return id;
    }
}
