/**
 * BitHandler
 * A client for the SocketSystem server
 */
public class BitHandler extends Thread {
    public static final int HALFPERIOD = 500;

    private static final String SILENCE = "SILENCE";
    private static final String EXPECT_ZERO = "EXPECT_ZERO";
    private static final String EXPECT_ONE = "EXPECT_ONE";
    private static final String HALF_ZERO = "HALF_ZERO";
    private static final String HALF_ONE = "HALF_ONE";
    private static final String GARBAGE = "GARBAGE";

    private LightPanel panel;
    private BitListener listener;
    private String state = SILENCE;

    /**
     * Default constructor that uses the DEFAULT PORT value from the LightSystem
     */
    public BitHandler() {
	this("localhost", LightSystem.DEFAULT_PORT);
    }

    /**
     * Constructor that sets up the client
     * @param host the host server
     * @param port the port that the server is listening on
     */
    public BitHandler(String host, int port) {
	panel = new LightPanel(host, port);
	start();
    }

    /**
     * Stop execution of the thread
     * @param milliseconds the amount of time to stop for
     */
    public static void pause(int milliseconds) {
	try {
	    Thread.sleep(milliseconds);
	} catch (InterruptedException e) {
	    // should never get here, but
	    // let's print something just in case
	    e.printStackTrace();
	}
    }

    /**
     * Check for a collision
     * @param if the light should be on
     */
    private void checkCollision(boolean on) throws CollisionException {
	if ((on && !panel.isOn()) || (!on && panel.isOn())) {
	    throw new CollisionException();
	}
    }

    /**
     * Turn the light system on (if it isn't already), then wait half a period. Then
     * turn the light off, for half a period.
     */
    public void broadcastZero() throws CollisionException {
	panel.switchOn();
	pause(HALFPERIOD);
	checkCollision(true);
	panel.switchOff();
	pause(HALFPERIOD);
	checkCollision(false);
    }

    /**
     * Turn the light system off (if it isn't already), then wait half a period.
     * Then turn the light on, for half a period.
     */
    public void broadcastOne() throws CollisionException {
	panel.switchOff();
	pause(HALFPERIOD);
	checkCollision(false);
	panel.switchOn();
	pause(HALFPERIOD);
	checkCollision(true);
    }

    /**
     * Given a string of bits (0s and 1s), send each bit using broadcastOne/Zero().
     * Build up a string of successfully sent bits (called "broadcasted"). Switch
     * the light off when done.
     */
    public void broadcast(String bits) throws CollisionException {
	for (char bit: bits.toCharArray()) {
	    switch(bit) {
	    case '0':
		broadcastZero();
		break;
	    case '1':
		broadcastOne();
		break;
	    }
	}
    }

    /**
     * @return the panel string
     */
    public String toString() {
	return panel.toString();
    }

    /**
     * Repeatedly (and as fast as possible), check if the panel's light has changed
     * from on to off or vice versa. When it does, check how much time passed
     * between transitions. Based on this time and which direction the transition
     * was made, determine if a 0 or 1 bit was received. Send the result off to the
     * registered Listener.
     */
    public void run() {
	long lastTransition = System.currentTimeMillis();
	String bits = "";
	boolean wasOn = false;
	while (true) {
	    long time = System.currentTimeMillis();
	    String lastState = state;

	    if (panel.isOn() != wasOn) { // a transition was made: on to off or vice versa
		if (time - lastTransition > HALFPERIOD * 1.5) {
		    // full delay
		    if (state.equals(SILENCE)) {
			/* all broadcasts start with a 0, after silence for a while */
			state = EXPECT_ZERO;
		    } else if (state.equals(EXPECT_ZERO)) {
			bits = "";
			state = SILENCE;
		    } else if (state.equals(EXPECT_ONE)) {
			state = EXPECT_ZERO;
			notifyReceived(bits);
			bits = "";
		    } else if (state.equals(HALF_ZERO)) {
			bits += "0";
			state = HALF_ONE;
		    } else if (state.equals(HALF_ONE)) {
			bits += "1";
			state = HALF_ZERO;
		    } else if (state.equals(GARBAGE))
			state = HALF_ZERO;
		} else {
		    // half delay
		    if (state.equals(SILENCE))
			state = EXPECT_ZERO;
		    else if (state.equals(EXPECT_ZERO))
			state = HALF_ZERO;
		    else if (state.equals(EXPECT_ONE))
			state = HALF_ONE;
		    else if (state.equals(HALF_ZERO)) {
			bits += "0";
			state = EXPECT_ZERO;
		    } else if (state.equals(HALF_ONE)) {
			bits += "1";
			state = EXPECT_ONE;
		    } else if (state.equals(GARBAGE))
			state = HALF_ZERO;
		}
		lastTransition = time;
		wasOn = !wasOn;
	    } else if (time - lastTransition > 3 * HALFPERIOD) {
		// timeout

		// System.out.println(this + ": timeout");

		if (state.equals(SILENCE)) {
		    // no transition
		} else if (state.equals(EXPECT_ZERO)) {
		    bits = "";
		    state = GARBAGE;
		}

		if (state.equals(EXPECT_ONE)) {
		    state = SILENCE;
		    notifyReceived(bits);
		    bits = "";
		} else if (state.equals(HALF_ZERO)) {
		    state = SILENCE;
		    notifyReceived(bits + "0");
		    bits = "";
		} else if (state.equals(HALF_ONE)) {
		    bits = "";
		    state = GARBAGE;
		} else if (state.equals(GARBAGE)) {
		    // no transition
		}
	    }

	    // System.out.println(getID() + " is alive");

	    pause(1);
	}
    }

    /**
     * Set a BitListener
     * @param l a BitListener
     */
    public void setListener(BitListener l) {
	listener = l;
    }

    /**
     * @return is the state machine in silent state
     */
    public boolean isSilent() {
	return state.equals(SILENCE);
    }
    
    /**
     * @return handler id
     */
    public int getID() {
	return panel.getID();
    }

    /**
     * Update the listener with the new bits
     * @param bits the values received from the LightPanel
     */
    private void notifyReceived(final String bits) {
	if (listener == null)
	    return;
	new Thread() {
	    public void run() {
		listener.bitsReceived(BitHandler.this, bits);
	    }
	}.start();
	// System.out.println(this + " received bits: " + bits);
    }
}
