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
            // Wait for next request from client
            String string = responder.recvStr (0);
            System.out.printf ("Received request: [%s]\n", string);
// Do some 'work'
            Thread.sleep (1000);
// Send reply back to client
            responder.send ("World");
// We never get here but clean up anyhow
            responder.close();
            ctx.term();
        }
    }
}
