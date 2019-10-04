public class L2Handler implements BitListener {
    BitHandler handler;
    BitListener listener;
    L2Listener l2Listener;
    int macAddr;

    public L2Handler(int macAddr) {
        this("localhost", LightSystem.DEFAULT_PORT, macAddr);
    }
    public L2Handler(String host, int port, int macAddr) {
        this.macAddr = macAddr;
        handler = new BitHandler(host, port);
        handler.setListener(this);
    }

    @Override
    public String toString() {
        return Integer.toString(macAddr);
    }

    int getMacAddr() {
        return macAddr;
    }

    public void setListener(L2Listener l) {
        l2Listener = l;
    }	

    void send(L2Frame frame) {
        while (!handler.isSilent()) {
            BitHandler.pause(BitHandler.HALFPERIOD);
        }
        try {
            handler.broadcast(frame.toString());
        } catch (CollisionException e)  {
            // TODO fix this
            System.out.println("Collisssions");
        }
        
    }

	public void bitsReceived(BitHandler h, String bits) {
		l2Listener.frameReceived(L2Handler.this, new L2Frame(bits));
	}
}
