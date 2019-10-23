public class Test {
    public static void main(String[] args) {
        // LightSystem system = new LightSystem();
        // LightDisplay d1 = new LightDisplay(new LightPanel());
        // LightDisplay d2 = new LightDisplay(new LightPanel());

        // BitDisplay b = new BitDisplay(new BitHandler());
        // BitDisplay b2 = new BitDisplay(new BitHandler());

        LightSystem system = new LightSystem();
        L2Display d1 = new L2Display(new L2Handler(0));
        L2Display d2 = new L2Display(new L2Handler(1));
    }
}
