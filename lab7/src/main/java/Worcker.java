import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class Worcker implements Runnable{

    @Override
    public void run()  {
        ZContext context = new ZContext();
        Socket worker = context.createSocket(SocketType.DEALER);
        worker.setHWM(0);


        while (!Thread.currentThread().isInterrupted()) {
        }
    }
    {

    }
}
