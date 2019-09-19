public class Test {
    private static String ip = "10.0.1.108";
    private static int port = LightSystem.DEFAULT_PORT;
    public static void main(String args[]) {
	LightSystem system = new LightSystem();
	LightDisplay d1 = new LightDisplay(new LightPanel());
	BitDisplay b1 = new BitDisplay(new BitHandler());
    }
}

