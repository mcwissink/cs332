import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class initializes a LightSystem that is used as a communication platform
 * used by the LightPanels
 */
public class LightSystem extends Thread {
    public static final int DEFAULT_PORT = 9223;
    public static final String HIGH = "H";
    public static final String LOW = "L";

    private static Random random = new Random();

    /**
     * Returns a reference to a random object used by LightPanel to generate a
     * random integer for its ID
     * 
     * @return: random - a Random object
     */
    public static Random getRandom() {
        return random;
    }

    private Set clientOutputStreams = new HashSet();
    private boolean isHigh = false;
    private int port;

    /**
     * Default Constructor: calls the explicit constructor with DEFAULT_PORT
     */
    public LightSystem() {
        this(DEFAULT_PORT);
    }

    /**
     * Explicit Constructor: stores teh given port and starts the progam
     * 
     * @param port: The LightSystem will run on this given port
     */
    public LightSystem(int port) {
        this.port = port;
        start();
    }

    /**
     * Starts a server for the LightPanel clients. Creates new threads for each
     * connection and notifies the client when the thread has started Reads incoming
     * information from each client
     */
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println(clientSocket + " connected");

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientOutputStreams.add(out);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                LightSystemThread thread = new LightSystemThread(this, in);
                thread.start();
                notifyClient(out);
                // serverSocket.close();
            }
        } catch (BindException e) {
            throw new RuntimeException("LightSystem/other already running on port");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If the system is off, turn it on and notify all other clients
     */
    public void switchOn() {
        if (!isHigh) {
            isHigh = true;
            notifyClients();
        }
    }

    /**
     * If the system is on, turn it off and notify all other clients
     */
    public void switchOff() {
        if (isHigh) {
            isHigh = false;
            notifyClients();
        }
    }

    /**
     * Loop through all the clients and notify them to turn on or off
     */
    private void notifyClients() {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            PrintWriter clientOutputStream = (PrintWriter) it.next();
            notifyClient(clientOutputStream);
        }
    }

    /**
     * Notify an individual client to turn on or off based on the status of the
     * system
     * 
     * @param clientOut: individual client to notify
     */
    private void notifyClient(PrintWriter clientOutputStream) {
        if (isHigh) {
            clientOutputStream.println(HIGH);
        } else {
            clientOutputStream.println(LOW);
        }
    }
}
