public class L2Frame {
	public static int BCAST_ADDR = 15;
	private int source;
	private int destination;
	private int vlanId;
	private int type;
	private int payloadSize;
	private int checkSum;
	private String payload;

	public L2Frame(int source, int destination, int vlanId, String payload) {
		this.source = source;
		this.destination = destination;
		this.vlanId = vlanId;
        this.payloadSize = payload.length();
		this.payload = payload;
		this.type = 0;
	}
	
	public L2Frame(String bits){
        bits = bits.substring(1, bits.length());
		this.destination = toDecimal(bits.substring(0, 4));
		this.source = toDecimal(bits.substring(4, 8));
		this.type = toDecimal(bits.substring(8, 10));
		this.vlanId = toDecimal(bits.substring(10, 12));
		this.payloadSize = toDecimal(bits.substring(12, 20));
		this.payload = bits.substring(20, 20 + payloadSize);
		this.checkSum = toDecimal(bits.substring(20 + payloadSize, bits.length()));
		if (computeErrorCheck(bits.substring(0, bits.length() - 1)) != checkSum) {
			throw new IllegalArgumentException();
		}
	}

	int toDecimal(String bits) {
		return Integer.parseInt(bits, 2);
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
		int bitTotal = 0;
		char[] bitChars = bits.toCharArray();
		for (char bit : bitChars) {
			bitTotal += Character.getNumericValue(bit);
		}
		return bitTotal % 2;

	}

	@Override
	public String toString() {
		String frame = "0"
            + toBinary(destination, 4)
            + toBinary(source, 4)
            + toBinary(type, 2)
            + toBinary(vlanId, 2)
            + toBinary(payloadSize, 8)
            + payload;
        return frame + toBinary(computeErrorCheck(frame), 1);
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
		return payloadSize;
	}
}