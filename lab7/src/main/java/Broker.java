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

            ZMQ.Poller items = context.createPoller(2);
            items.register(frontend, ZMQ.Poller.POLLIN);
            items.register(backend, ZMQ.Poller.POLLIN);
            boolean more = false;
            byte[] message;

            while (!Thread.currentThread().isInterrupted()) {
                items.poll();
                
            }
            responder.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
