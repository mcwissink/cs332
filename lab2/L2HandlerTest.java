public class L2HandlerTest {
	public static void main(String[] args) {
		testContructors();
	}

	private static void testContructors() {
		LightSystem system = new LightSystem();
    L2Handler lh1 = new L2Handler(0);
    assert lh1.getMacAddr() == 0;
    assert lh1.toString() == "0";
    lh1.setListener(new L2Listener() {
        public void frameRecieved(L2Handler h, L2Frame f) {
          System.out.println("Received");
          System.out.println(f.getPayload());
        }
      });
    L2Handler lh2 = new L2Handler(1);
    assert lh2.getMacAddr() == 1;
    assert lh2.toString() == "1";
    lh2.send(new L2Frame(lh2.getMacAddr(), 0, 0, "1010"));

    // L2Handler lh2 = new L2Handler("127.0.0.1", 9000, 1);

	}

	private static void testToBinary() {
		assert L2Frame.toBinary(1, 4).equals("0001");
		assert L2Frame.toBinary(2, 4).equals("0010");
		assert L2Frame.toBinary(3, 4).equals("0011");
		assert L2Frame.toBinary(15, 4).equals("1111");
	}

	private static void testComputeErrorCheck() {

	}
}
