public class L2HandlerTest {
    public static void main(String[] args) {
        final String payload = "1010";

        System.out.println("Running tests...");
        LightSystem system = new LightSystem();
        L2Handler lh1 = new L2Handler(0);
        assert lh1.getMacAddr() == 0;
        assert lh1.toString() == "0";
        lh1.setListener(new L2Listener() {
            public void frameReceived(L2Handler h, L2Frame f) {
                assert f.getPayload() == payload;
                System.out.println("Tests passed. Done!");
            }
        });
        L2Handler lh2 = new L2Handler(1);
        lh2.setListener(new L2Listener() {
            public void frameReceived(L2Handler h, L2Frame f) {
            }
        });
        assert lh2.getMacAddr() == 1;
        assert lh2.toString() == "1";
        lh2.send(new L2Frame(lh2.getMacAddr(), 0, 0, payload));
    }
}
