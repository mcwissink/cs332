public class L2Handler implements L2Listener {
    BitHandler handler;
    L2Listener listener;
    int macAddr;
       
    public L2Handler(String host, int port, int macAddr) {
	this.macAddr = macAddr;
	handler = new BitHandler(host, port);
	handler.setListener(this);
    }

    @Override
    public toString() {
	return this.macAddr;
    }

    int getMacAddr() {
	return macAddr;
    }
	
}
