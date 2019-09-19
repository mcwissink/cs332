import java.io.*;
import java.net.*;
import java.util.*;

/**
 * LightSystem
 * A server that handles clients connecting to the light system
 */
public class LightSystem extends Thread {
    public static final int DEFAULT_PORT = 9223;
    public static final String HIGH = "H";
    public static final String LOW = "L";

    private static Random random = new Random();

    /**
     * Gets a random number
     */
    public static Random getRandom() {
	return random;
    }

    private Set clients = new HashSet();
    private boolean isHigh = false;
    private int port;

    /**
     * Default constructor that uses the DEFAULT PORT value
     */
    public LightSystem() {
	this(DEFAULT_PORT);
    }

    /**
     * Constructor that sets the port and starts the system
     * @param port the port for the server to listen on 
     */
    public LightSystem(int port) {
	this.port = port;
	start();
    }

    /**
     * Run the light system server
     */
    public void run() {
	try {
	    ServerSocket serverSocket = new ServerSocket(port);
	    while (true) {
		Socket clientSocket = serverSocket.accept();

		System.out.println(clientSocket + " connected");

		PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
		clients.add(clientOut);
		BufferedReader clientSocketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		LightSystemThread thread = new LightSystemThread(this, clientSocketIn);
		thread.start();
		notifyClient(clientOut);
	    }
	} catch (BindException e) {
	    throw new RuntimeException("LightSystem/other already running on port");
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Turn the switch off and notify the clients of the change
     */
    public void switchOn() {
	if (!isHigh) {
	    isHigh = true;
	    notifyClients();
	}
    }

    /**
     * Turn the switch on and notify the clients of the change
     */
    public void switchOff() {
	if (isHigh) {
	    isHigh = false;
	    notifyClients();
	}
    }

    /**
     * Notify all clients
     */
    private void notifyClients() {
	Iterator it = clients.iterator();
	while (it.hasNext()) {
	    PrintWriter clientOut = (PrintWriter) it.next();
	    notifyClient(clientOut);
	}
    }

    /**
     * Push a notification to a client
     */
    private void notifyClient(PrintWriter clientOut) {
	if (isHigh)
	    clientOut.println(HIGH);
	else
	    clientOut.println(LOW);
    }
}
