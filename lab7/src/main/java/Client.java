import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class Client implements Runnable {
    private static int SAMPLE_SIZE = 10000;
    int requests;
    long start;

    @Override
    public void run() {
        ZContext ctx = new ZContext();
        ZMQ.Socket client = ctx.createSocket(SocketType.DEALER);
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

    }
}
