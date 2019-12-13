import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

public class Broker {
    ZContext ctx = new ZContext();
    Socket responder = ctx.createSocket(SocketType.DEALER);
    responder.bind()

}
