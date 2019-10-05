public class L2FrameTest {
	public static void main(String[] args) {
		testContructors();
		testToBinary();
		testComputeErrorCheck();
		testToString();
		System.out.println("All tests passed!");

	}

	private static void testContructors() {
		L2Frame l1 = new L2Frame(0, 1, 0, "Hello");
		assert l1.getSource() == 0;
		assert l1.getDestination() == 1;
		assert l1.getVlanId() == 0;
		assert l1.getPayload() == "Hello";
	}

	private static void testToBinary() {
		assert L2Frame.toBinary(1, 4).equals("0001");
		assert L2Frame.toBinary(2, 4).equals("0010");
		assert L2Frame.toBinary(3, 4).equals("0011");
		assert L2Frame.toBinary(15, 4).equals("1111");
	}

	private static void testComputeErrorCheck() {
		assert L2Frame.computeErrorCheck("0") == 0;
		assert L2Frame.computeErrorCheck("1") == 1;
		assert L2Frame.computeErrorCheck("00011000") == 0;
		assert L2Frame.computeErrorCheck("100011011100111") == 1;
	}

	private static void testToString() {
		L2Frame l1 = new L2Frame(0, 1, 0, "111111");
		assert l1.toString().equals("000010000000000000000111111");
	}
}
