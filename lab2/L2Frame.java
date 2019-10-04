public class L2Frame {
    public static int BCAST_ADDR = 15;
    private int source;
    private int destination;
    private int vlanId;
    private int type;
    private int payloadSize;
    private int checkSum;
    private String payload;

    /**
     * This constructor constructs an L2Frame based on the passed in arguments
     *
     * @param destination MAC Address of the destination
     * @param source      MAC Address of the source
     * @param vlanId      ID of the VLAN for the packet
     * @param payload     Payeload of the packet
     */
    public L2Frame(int destination, int source, int vlanId, String payload) {
        this.source = source;
        this.destination = destination;
        this.vlanId = vlanId;
        this.payloadSize = payload.length();
        this.payload = payload;
        this.type = 0;
    }

    /**
     * This constructor constucts an L2Frame based on a bitstring
     *
     * @param bits A bitstring representing an L2Frame
     */
    public L2Frame(String bits) {
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

    /**
     * Convert a L2Frame bitstring into a decimal number
     *
     * @param bits A bitstring representing an L2Frame
     * @return int A decimal number representation of an L2Frame
     */
    int toDecimal(String bits) {
        return Integer.parseInt(bits, 2);
    }

    /**
     * Convert an L2Frame from decimal representation to bitstring
     *
     * @param value  An int representing an L2Frame
     * @param length Length of the L2Frame
     * @return String A bitstring representing an L2Frame
     */
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

    /**
     * Generate CRC for a bitstring
     *
     * @param bits Bitstring representing an L2Frame
     * @return int The CRC bit for the bitstring
     */
    static int computeErrorCheck(String bits) {
        int bitTotal = 0;
        char[] bitChars = bits.toCharArray();
        for (char bit : bitChars) {
            bitTotal += Character.getNumericValue(bit);
        }
        return bitTotal % 2;

    }

    /**
     * Convert an L2Frame to a (bit)String with a CRC bit at the end
     *
     * @return String A Bitstring representing an L2Frame with a leading 0 and a CRC bit
     */
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

    /**
     * Getter for this.source
     *
     * @return int The source MAC
     */
    int getSource() {
        return source;
    }

    /**
     * Getter for this.destination
     *
     * @return int The destination MAC
     */
    int getDestination() {
        return destination;
    }

    /**
     * Getter for this.vlanId
     *
     * @return int The VLAN ID
     */
    int getVlanId() {
        return vlanId;
    }

    /**
     * Getter for this.payload
     *
     * @return String The payload
     */
    String getPayload() {
        return payload;
    }

    /**
     * Getter for this.payloadSize
     *
     * @return int The size/length of the payload in bits
     */
    int getLength() {
        return payloadSize;
    }
}
