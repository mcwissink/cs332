public class Test2 {
    private static String ip = "10.0.1.108";
    private static int port = LightSystem.DEFAULT_PORT;
    public static void main(String args[]) {
	// LightDisplay d1 = new LightDisplay(new LightPanel(ip, port));
	// LightDisplay d2 = new LightDisplay(new LightPanel(ip, port));
	BitDisplay b1 = new BitDisplay(new BitHandler(ip, port));
    }
}
