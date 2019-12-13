import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

public class Broker implements Runnable {

    @Override
    public void run() {
        try {
            ZContext context = new ZContext();
            Socket responder = context.createSocket(SocketType.DEALER);
            responder.connect("tcp://localhost:5560");

            while (!Thread.currentThread().isInterrupted()) {
                ZMQ.Poller items = context.createPoller(2);
                items.register(responder, ZMQ.Poller.POLLIN);
                Thread.sleep(1000);
                responder.send ("World");
                items.close();
            }
            responder.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
