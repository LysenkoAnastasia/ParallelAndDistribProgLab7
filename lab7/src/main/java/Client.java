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
        ZContext context = new ZContext();
        ZMQ.Socket client = context.createSocket(SocketType.DEALER);
        client.setHWM(0);
        client.setIdentity("C".getBytes(ZMQ.CHARSET));
        client.connect("tcp://localhost:5555");
        System.out.println("Setting up test");
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
            req.send(client);
        }

        for (requests = 0;
             requests < SAMPLE_SIZE && !Thread.currentThread()
                     .isInterrupted();
             requests++) {
            ZMsg.recvMsg(client).destroy();
        }
        context.destroySocket(client);
    }

    @Override
    public void close() throws IOException {

    }
}
