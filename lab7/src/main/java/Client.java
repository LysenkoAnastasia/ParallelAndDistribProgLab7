import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client implements Runnable {
    private static int SAMPLE_SIZE = 10000;

    @Override
    public void run() {
        ZContext ctx = new ZContext();
        ZMQ.Socket client = ctx.createSocket(SocketType.DEALER);
        client.setHWM(0);
        client.setIdentity("C".getBytes(ZMQ.CHARSET));
        client.connect("tcp://localhost:5555");
        System.out.println("Setting up test");
    }
}
