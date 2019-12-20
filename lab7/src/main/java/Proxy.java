import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Proxy {

    private  static ZContext context;
    private static long time;
    private static HashMap<ZFrame, Commutator> commutator;

    public static void main(String[] args) {

        try {
            context = new ZContext();
            Socket frontend = context.createSocket(SocketType.ROUTER);
            Socket backend = context.createSocket(SocketType.ROUTER);
            frontend.setHWM(0);
            backend.setHWM(0);
            frontend.bind("tcp://localhost:5559");
            backend.bind("tcp://localhost:5560");
            System.out.println("launch and connect broker.");
            ZMQ.Poller items = context.createPoller(2);
            items.register(frontend, ZMQ.Poller.POLLIN);
            items.register(backend, ZMQ.Poller.POLLIN);
            commutator = new HashMap<>();
            time = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                items.poll(1);
                if (!commutator.isEmpty() && (System.currentTimeMillis()-time) > 5000) {
                    for (Iterator<Map.Entry<ZFrame, Commutator>> it = commutator.entrySet().iterator(); it.hasNext();) {
                        Map.Entry<ZFrame, Commutator> entry = it.next();
                        if (Math.abs(entry.getValue().getTime() - time) > 5000*2) {
                            it.remove();
                        }
                    }
                    //remove();
                }
                time = System.currentTimeMillis();

                if (items.pollin(0)) {
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    if (msg == null) {
                        break;
                    }
                    System.out.println( "MSG: " + msg);
                    if (commutator.isEmpty()) {
                        ZMsg error = new ZMsg();
                        error.add(msg.getFirst());
                        error.add("");
                        error.add("No current");
                        error.send(frontend);
                    }
                    else {
                        String[] data = msg.getLast().toString().split(" ");
                        if (data[0].equals("GET")) {
                            for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
                                if (c.getValue().intersect(data[1])) {
                                    ZFrame cache = c.getKey().duplicate();
                                    msg.addFirst(cache);
                                    msg.send(backend);
                                    //System.out.println(backend);
                                }
                            }
                        }
                        else {
                            if (data[0].equals("PUT")) {
                                for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
                                    if(c.getValue().intersect(data[1])) {
                                        ZMsg tmp = msg.duplicate();
                                        ZFrame cache = c.getKey().duplicate();
                                        tmp.addFirst(cache);
                                        System.out.println("put: " + tmp);
                                        //msg.addFirst(cache);
                                        msg.send(backend);
                                    }
                                }
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
                   // pollin0(frontend, backend, msg);
                }

                if (items.pollin(1)) {
                    ZMsg msg = ZMsg.recvMsg(backend);
                    if (msg == null)
                        break;
                    if (msg.getLast().toString().contains("Heartbleed")) {
                        if (!commutator.containsKey(msg.getFirst())) {
                            ZFrame data = msg.getLast();
                            String[] fields = data.toString().split(" ");
                            Commutator com = new Commutator(fields[1], fields[2], System.currentTimeMillis());
                            commutator.put(msg.getFirst().duplicate(), com);
                        } else {
                            commutator.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
                        }
                    } else {
                        msg.pop();
                        msg.send(frontend);
                    }
                    // pollin1(frontend, backend, msg);
                }
                items.close();
            }

        } catch (ZMQException e) {
            e.printStackTrace();
        }

    }

    private static void remove() {
        //commutator.entrySet().removeIf(entry -> Math.abs(entry.getValue().getTime() - time) > 5000 * 2);
        for (Iterator<Map.Entry<ZFrame, Commutator>> it = commutator.entrySet().iterator(); it.hasNext();) {
            Map.Entry<ZFrame, Commutator> entry = it.next();
            if (Math.abs(entry.getValue().getTime() - time) > 5000*2) {
                it.remove();
            }
        }
    }

    private static void pollin0(Socket frontend, Socket backend, ZMsg msg) {
        System.out.println( "MSG: " + msg);
        if (commutator.isEmpty()) {
            ZMsg error = new ZMsg();
            error.add(msg.getFirst());
            error.add("No current");
            error.send(frontend);
        }
        else {
            String[] data = msg.getLast().toString().split(" ");
            if (data[0].equals("GET")) {
                for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
                    if (c.getValue().intersect(data[1])) {
                        ZFrame cache = c.getKey().duplicate();
                        msg.addFirst(cache);
                        msg.send(backend);
                        //System.out.println(backend);
                    }
                }
            }
            else {
                if (data[0].equals("PUT")) {
                    for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
                        if(c.getValue().intersect(data[1])) {
                            ZMsg tmp = msg.duplicate();
                            ZFrame cache = c.getKey().duplicate();
                            tmp.addFirst(cache);
                            System.out.println(tmp);
                            //msg.addFirst(cache);
                            msg.send(backend);
                        }
                    }
                }
                else {
                    ZMsg error = new ZMsg();
                    error.add(msg.getFirst());
                    error.add("");
                    error.send(frontend);

                }
            }
        }
    }

    private static void pollin1(Socket frontend, Socket backend, ZMsg msg){
        if (msg.getLast().toString().contains("Heartbleed")) {
            if (!commutator.containsKey(msg.getFirst())) {
                ZFrame data = msg.getLast();
                String[] fields = data.toString().split(" ");
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
}
