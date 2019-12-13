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

            boolean more = false;
            byte[] message;

            while (!Thread.currentThread().isInterrupted()) {
                ZMQ.Poller items = context.createPoller(2);
                items.register(frontend, ZMQ.Poller.POLLIN);
                items.register(backend, ZMQ.Poller.POLLIN);
                items.poll();
                if (items.pollin(0)) {
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    if (msg == null) {
                        break;
                    }
                    ZFrame address = msg.pop();
                    address.destroy();
                    msg.addFirst(new ZFrame("W"));
                    msg.send(backend);
                }
                if (items.pollin(1)) {
                    ZMsg msg = ZMsg.recvMsg(backend);
                    if (msg == null)
                        break; // Interrupted
                    ZFrame address = msg.pop();
                    address.destroy();
                    msg.addFirst(new ZFrame("C"));
                    msg.send(frontend);
                }
                items.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
