import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import java.io.Closeable;
import java.io.IOException;

public class Worker implements Closeable {

    public static void main(String[] args) {
        ZContext context = new ZContext();
        Socket worker = context.createSocket(SocketType.DEALER);
        worker.setHWM(0);
        worker.setIdentity("W".getBytes(ZMQ.CHARSET));
        worker.connect("tcp://localhost:5556");
        ZMQ.Poller items = context.createPoller(1);

        while (!Thread.currentThread().isInterrupted()) {
            items.poll();
        }
    }

    @Override
    public void close() {
    }
}
