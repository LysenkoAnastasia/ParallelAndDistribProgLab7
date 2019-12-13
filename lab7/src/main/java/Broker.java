import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Broker {
    ZContext ctx = new ZContext();
    ZMQ.Socket responder = ctx.createSocket(SocketType.DEALER);
    responder.connect

}
