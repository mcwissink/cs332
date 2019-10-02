public class L2Frame {
    public static int BCAST_ADDR = 15;
    private int source;
    private int destination;
    private int vlanId;
    private int length;
    private String payload;
    public L2Frame(int source, int destination, int vlanId, String payload) {
	this.source = source;
	this.destination = destination;
	this.vlanId = vlanId;
	this.payload = payload;
    }

    static String toBinary(int value, int length) {
	String binaryValue = Integer.toBinaryString(value);
	if (binaryValue.length() > length) {
	    return binaryValue;
	} else {
	    char[] padding = new char[length - binaryValue.length()];
	    java.util.Arrays.fill(padding, '0');
	    return new String(padding) + binaryValue;
	}
    }

    static int computeErrorCheck(String bits) {
	int bitTotal = 0
	char[] bitChars = bits.toCharArray();
	for (char bit: bitChars) {
	    bitTotal += Character.getNumericValue(bit);
	}
	return bitTotal % 2; 
	   
    }

    String toString() {
	return "0"
	    + toBinary(destination, 4)
	    + toBinary(source, 4)
	    + toBinary(type, 2)
	    + toBinary(vlanId, 2)
	    + toBinary(length, 8)
	    + toBinary(errorCheck, 1)
	    + payload;
    }

    int getSource() {
	return source;
    }

    int getDestination() {
	return destination;
    }

    int getVlanId() {
	return vlanId;
    }

    String getPayload() {
	return payload;
    }

    int getLength() {
	return length;
    }
}
