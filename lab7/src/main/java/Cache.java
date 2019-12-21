import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.util.HashMap;
import java.util.Scanner;

public class Cache {
    private static Scanner in = new Scanner(System.in);
    private static HashMap<Integer, String> cache = new HashMap<>();
    private Socket worker;
    private ZContext context;
    private  int leftBound;
    private int rightBound;
    private ZMQ.Poller items;
    private static String DELIM = " ";
    private static  int EPSILON_TIME = 5000;

    public Cache(ZContext context) {
        this.context = context;
        leftBound = in.nextInt();
        rightBound = in.nextInt();

        for (int i = leftBound; i <= rightBound; i++) {
            cache.put(i, Integer.toString(i));
        }
        worker = context.createSocket(SocketType.DEALER);
        worker.setHWM(0);
    }


    public static void main(String[] args) {
        try {
            ZContext context = new ZContext();
            Cache cache1 = new Cache(context);
            cache1.handler();

        } catch (ZMQException e) {
            e.printStackTrace();
        }
    }

    private void handler() {
        connect();
        long time = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            items.poll(1);

            if(System.currentTimeMillis() - time > EPSILON_TIME) {
                ZMsg message = new ZMsg();
                message.addLast("Heartbleed" + " " +
                        leftBound + " " +
                        rightBound);
                message.send(worker);
            }
            if (items.pollin(0)) {
                handleDealerPollin();
            }
        }
    }

    private void connect() {
        worker.connect("tcp://localhost:5556");
        items = context.createPoller(1);
        items.register(worker, ZMQ.Poller.POLLIN);
    }

    private void handleDealerPollin() {
        ZMsg msg = ZMsg.recvMsg(worker);
        ZFrame content = msg.getLast();
        String[] contentArr = content.toString().split(DELIM);

        if (contentArr[0].equals("GET")) {
            int pos = Integer.parseInt(contentArr[1]);
            String value = cache.get(pos);
            msg.pollLast();
            msg.addLast(value);
            msg.send(worker);
        }
        if (contentArr[0].equals("PUT")) {
            int pos = Integer.parseInt(contentArr[1]);
            String swap = contentArr[2];
            cache.put(pos, swap);
            msg.send(worker);
        }

    }

}
