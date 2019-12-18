import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Worker {
    private static ZContext context = new ZContext();
    private static Scanner in = new Scanner(System.in);
    private static int leftBound;
    private static int rightBound;
    private static HashMap<Integer, String> cache = new HashMap<>();

    public static void main(String[] args) {

        leftBound = in.nextInt();
        rightBound = in.nextInt();

        for(int i = leftBound; i <= rightBound; i++) {
            cache.put(i, Integer.toString(i));
        }

        Socket worker = context.createSocket(SocketType.DEALER);
        worker.setHWM(0);
        worker.setIdentity("W".getBytes(ZMQ.CHARSET));
        worker.connect("tcp://localhost:5556");
        ZMQ.Poller items = context.createPoller(1);

        while (!Thread.currentThread().isInterrupted()) {
            items.poll();
        }
    }

    public void close() {
    }
}
