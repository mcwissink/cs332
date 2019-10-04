public class L2Handler implements BitListener {
    BitHandler handler;
    BitListener listener;
    L2Listener l2Listener;
    int macAddr;

    /**
     * L2Handler constructor using defaults for all values
     *
     * @param macAddr The MAC address for the LightSystem
     */
    public L2Handler(int macAddr) {
        this("localhost", LightSystem.DEFAULT_PORT, macAddr);
    }

    /**
     * L2Handler constructor
     *
     * @param host    The hostname of the LightSystem
     * @param port    The port number of the LightSystem
     * @param macAddr The MAC address of the LightSystem
     */
    public L2Handler(String host, int port, int macAddr) {
        this.macAddr = macAddr;
        handler = new BitHandler(host, port);
        handler.setListener(this);
    }

    /**
     * Convert an L2Handler to a String (return it's MAC)
     *
     * @return String The MAC address
     */
    @Override
    public String toString() {
        return Integer.toString(macAddr);
    }

    /**
     * Getter for this.macAddr
     *
     * @return int The MAC address
     */
    int getMacAddr() {
        return macAddr;
    }

    /**
     * Setter for this.l2listener
     *
     * @param l The L2Listener to set
     */
    public void setListener(L2Listener l) {
        l2Listener = l;
    }

    /**
     * Send an L2Frame
     *
     * @param frame The L2Frame to send
     */
    void send(L2Frame frame) {
        while (!handler.isSilent()) {
            BitHandler.pause(BitHandler.HALFPERIOD);
        }
        try {
            handler.broadcast(frame.toString());
        } catch (CollisionException e) {
            //shouldn't have to deal with collisions
        }
    }

    /**
     * Create a new L2Frame based on recieved bits
     *
     * @param h    The BitHandler
     * @param bits The String representing the L2Frame
     */
    public void bitsReceived(BitHandler h, String bits) {
        L2Frame frame = new L2Frame(bits);
        if (frame.getDestination() == macAddr) {
            l2Listener.frameReceived(L2Handler.this, new L2Frame(bits));
        }
    }
}
