import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.io.Closeable;
import java.io.IOException;

public class Client implements Closeable {
    private static int SAMPLE_SIZE = 10000;
    ZContext context = new ZContext();
    ZMQ.Socket socket = context.createSocket(SocketType.REQ);
    int requests;
    long start;

    public void run() {
        socket.setHWM(0);
        socket.setIdentity("C".getBytes(ZMQ.CHARSET));
        socket.connect();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         long now = System.currentTimeMillis();
        start = System.currentTimeMillis();

        for (requests = 0; requests < SAMPLE_SIZE; requests++) {
            ZMsg req = new ZMsg();
            req.addString("client");
            req.send(socket);
        }

        for (requests = 0;
             requests < SAMPLE_SIZE && !Thread.currentThread()
                     .isInterrupted();
             requests++) {
            ZMsg.recvMsg(socket).destroy();
        }
        context.destroySocket(socket);
    }

    @Override
    public void close() throws IOException {

    }
}
