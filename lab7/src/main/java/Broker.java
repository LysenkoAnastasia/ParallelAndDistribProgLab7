import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

public class Broker implements Runnable {

    @Override
    public void run() {
        ZContext ctx = new ZContext();
        Socket responder = ctx.createSocket(SocketType.DEALER);
        responder.connect("tcp://localhost:5560");

        while (!Thread.currentThread().isInterrupted()) {
            ZMQ.Poller items = ctx.createPoller(2);
            items.register(frontend, ZMQ.Poller.POLLIN);
        }
    }
}
