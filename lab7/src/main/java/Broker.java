import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

public class Broker implements Runnable {

    @Override
    public void run() {
        try {
            ZContext context = new ZContext();
            Socket frontend = context.createSocket(SocketType.ROUTER);
            Socket backend = context.createSocket(SocketType.DEALER);
            frontend.bind("tcp://*:5559");
            backend.bind("tcp://*:5560");
            System.out.println("launch and connect broker.");

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
