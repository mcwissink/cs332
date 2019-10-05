import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class implements a LightPanel to represent a light with a switch that a
 * user and flip on and off to turn on and off the light. Keeps track of a
 * unique ID for the light it represents
 */
public class LightPanel extends Thread {
    private static Set idsUsed = new HashSet();

    private int id;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isHigh = false;

    /**
     * Default Constructor: calls explicit constructor with a default host and port
     * of the LightSystem
     */
    public LightPanel() {
        this("localhost", LightSystem.DEFAULT_PORT);
    }

    /**
     * Explicit Constructor: stores the given host and port info creates a
     * connection to the server with host and port starts the client
     * 
     * @param host: name of the Host
     * @param port: port of the Host
     */
    public LightPanel(String host, int port) {
        do {
            id = LightSystem.getRandom().nextInt(15) + 1;
        } while (!idsUsed.add(new Integer(id)));

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            start();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Invalid host:  " + host);
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to LightSystem");
        }
    }

    /**
     * Sends a message to the LightSystem that this light panel has turned on
     */
    public void switchOn() {
        out.println(LightSystem.HIGH);
    }

    /**
     * Sends a message to the LightSystem that this light panel has turned off
     */
    public void switchOff() {
        out.println(LightSystem.LOW);
    }

    /**
     * Closes the connection to the LightSystem
     */
    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Infinite loop that listens to changes on the LightSystem and updates the
     * light panel accordingly. throws an expection when the LightPanel loses
     * connection
     */
    public void run() {
        try {
            String line = in.readLine();
            while (line != null) {
                if (line.equals(LightSystem.HIGH))
                    isHigh = true;
                else if (line.equals(LightSystem.LOW))
                    isHigh = false;
                line = in.readLine();
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
