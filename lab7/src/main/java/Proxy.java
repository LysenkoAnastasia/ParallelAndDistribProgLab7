import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.util.HashMap;

public class Proxy {

    private ZContext context;
    private long time;
    private HashMap<ZFrame, Commutator> commutator;
    private Socket frontend;
    private Socket backend;
    private static String DELIM = " ";
    private static  int EPSILON_TIME = 5000;

    public static void main(String[] args) {

        try {
            ZContext context = new ZContext();
            Proxy proxy = new Proxy(context);
            proxy.bind();
            proxy.handle();

        } catch (ZMQException e) {
            e.printStackTrace();
        }
    }

    public Proxy(ZContext context) {
        this.context = context;
        this.frontend = context.createSocket(SocketType.ROUTER);
        this.backend = context.createSocket(SocketType.ROUTER);
        frontend.setHWM(0);
        backend.setHWM(0);
    }

    public void bind() {
        frontend.bind("tcp://localhost:5556");
        backend.bind("tcp://localhost:5560");
    }

    private void handle() {
        ZMQ.Poller items = context.createPoller(2);
        items.register(frontend, ZMQ.Poller.POLLIN);
        items.register(backend, ZMQ.Poller.POLLIN);
        commutator = new HashMap<>();
        time = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted()) {
            items.poll(1);
            if (!commutator.isEmpty() && (System.currentTimeMillis()-time) > EPSILON_TIME) {
                remove();
            }
            time = System.currentTimeMillis();

            if (items.pollin(0)) {
                ZMsg msg = ZMsg.recvMsg(frontend);
                if (msg == null) {
                    break;
                }
                handleClientPollin(msg);
            }

            if (items.pollin(1)) {
                ZMsg msg = ZMsg.recvMsg(backend);
                if (msg == null)
                    break;
                handleDealerPollin(msg);
            }
        }
    }

    private void remove() {
        commutator.entrySet().removeIf(entry -> Math.abs(entry.getValue().getTime() - time) > EPSILON_TIME * 2);
    }

    private void handleClientPollin(ZMsg msg) {
        System.out.println( "MSG: " + msg);
        if (commutator.isEmpty()) {
            ZMsg error = new ZMsg();
            error.add(msg.getFirst());
            error.add("");
            error.add("No current");
            error.send(frontend);
        }
        else {
            String[] data = msg.getLast().toString().split(DELIM);
            if (data[0].equals("GET")) {
                receiveGet(data, backend, msg);
            }
            else {
                if (data[0].equals("PUT")) {
                   receivePut(data, backend, msg);
                }
                else {
                    ZMsg error = new ZMsg();
                    error.add(msg.getFirst());
                    error.add("");
                    error.add("error");
                    error.send(frontend);

                }
            }
        }
    }

    private void handleDealerPollin(ZMsg msg){
        if (msg.getLast().toString().contains("Heartbleed")) {
            if (!commutator.containsKey(msg.getFirst())) {
                ZFrame data = msg.getLast();
                String[] fields = data.toString().split(DELIM);
                Commutator com = new Commutator(fields[1], fields[2], System.currentTimeMillis());
                commutator.put(msg.getFirst().duplicate(), com);
            } else {
                commutator.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
            }
        } else {
            msg.pop();
            msg.send(frontend);
        }
    }

    private void receiveGet(String[] data, Socket backend, ZMsg msg) {
        for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
            if (c.getValue().intersect(data[1])) {
                ZFrame cache = c.getKey().duplicate();
                msg.addFirst(cache);
                msg.send(backend);
            }
        }
    }

    private void receivePut(String[] data, Socket backend, ZMsg msg) {
        for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
            if(c.getValue().intersect(data[1])) {
                ZMsg tmp = msg.duplicate();
                ZFrame cache = c.getKey().duplicate();
                tmp.addFirst(cache);
                System.out.println("put: " + tmp);
                tmp.send(backend);
            }
        }
    }
}
