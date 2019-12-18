import org.zeromq.SocketType;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Proxy {

    public static void main(String[] args) {
        HashMap<ZFrame, Commutator> commutator = new HashMap<>();
        try {
            ZContext context = new ZContext();
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

            long time = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                items.poll();
                if (!commutator.isEmpty() && (System.currentTimeMillis()-time )> 5000) {
                    for (Iterator<Map.Entry<ZFrame, Commutator>> it = commutator.entrySet().iterator();)

                }
                if (items.pollin(0)) {
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    if (msg == null) {
                        break;
                    }

                    if (commutator.isEmpty()) {
                        ZMsg error = new ZMsg();
                        error.add(msg.getFirst());
                        error.send(frontend);
                    }
                    else {
                        String[] data = msg.getLast().toString().split(" ");
                        if (data[0].equals("Get")) {
                            for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
                                ZFrame cache = c.getKey().duplicate();
                                msg.addFirst(cache);
                                msg.send(backend);
                            }
                        }
                        else {
                            for (HashMap.Entry<ZFrame, Commutator> c : commutator.entrySet()) {
                                ZFrame cache = c.getKey().duplicate();
                                msg.addFirst(cache);
                                msg.send(backend);
                            }
                        }
                    }
                }

                if (items.pollin(1)) {
                    ZMsg msg = ZMsg.recvMsg(backend);
                    if (msg == null)
                        break;
                    ZFrame address = msg.pop();
                    address.destroy();
                    msg.addFirst(new ZFrame("C"));
                    msg.send(frontend);
                }
                items.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
