package mm.server.work;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class USRSignalHandler implements SignalHandler {
    public static void listenTo(String name) {
        Signal signal = new Signal(name);
        Signal.handle(signal, new USRSignalHandler());
    }
 
    public void handle(Signal signal) {
        if (signal.toString().trim().equals("USR1")) {
            //reload config
            Properties.getInstance();
        }
    }
}
