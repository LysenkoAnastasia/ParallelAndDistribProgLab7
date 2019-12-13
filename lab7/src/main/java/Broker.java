import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

public class Broker implements Runnable {
  try( ZContext ctx = new ZContext())

    {
        Socket responder = ctx.createSocket(SocketType.DEALER);
        responder.connect("tcp://localhost:5560");

        while () {

        }
    }

    @Override
    public void run() {

    }
}
