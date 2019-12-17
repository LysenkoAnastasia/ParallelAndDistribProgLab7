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

        while (!Thread.currentThread().isInterrupted()) {
            
            ZMsg msg = ZMsg.recvMsg(worker);
            msg.send(worker);
        }
    }

    @Override
    public void close() {
    }
}
