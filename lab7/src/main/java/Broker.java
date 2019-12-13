import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Broker {
    ZContext ctx = new ZContext();
    ZMQ.Socket frontend = ctx.createSocket(SocketType.DEALER);
    ZMQ.Socket backend = ctx.createSocket(SocketType.DEALER);
    

}
