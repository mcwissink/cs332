public class Test {
    private static String ip = "10.0.1.108";
    private static int port = LightSystem.DEFAULT_PORT;
    public static void main(String args[]) {
	LightSystem system = new LightSystem();
	LightDisplay d1 = new LightDisplay(new LightPanel());
	LightDisplay d2 = new LightDisplay(new LightPanel());
	BitHandler b1 = new BitHandler(ip, port);
	b1.broadcast("1010");
    }
}

