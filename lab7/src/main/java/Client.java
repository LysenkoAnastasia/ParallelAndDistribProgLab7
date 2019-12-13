import org.zeromq.*;
import zmq.Command;

import java.io.Closeable;
import java.io.IOException;

public class Client implements Closeable {
    private static int SAMPLE_SIZE = 10000;
    ZContext context = new ZContext();
    ZMQ.Socket socket = context.createSocket(SocketType.REQ);
    int requests;
    long start;

    public void main(String[] args) {
        socket.setHWM(0);
        socket.setIdentity("C".getBytes(ZMQ.CHARSET));
        socket.connect("tcp://localhost:5555");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            Command command = Command.f
        }


    }

    @Override
    public void close() {
        context.destroySocket(socket);
    }
}
