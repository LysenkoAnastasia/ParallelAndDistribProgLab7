import org.zeromq.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

public class Client{
    private static int SAMPLE_SIZE = 10000;
    ZContext context = new ZContext();
    ZMQ.Socket socket = context.createSocket(SocketType.REQ);
    Scanner in = new Scanner(System.in);

    public void main(String[] args) {
        socket.setHWM(0);
        socket.setIdentity("C".getBytes(ZMQ.CHARSET));
        socket.connect("tcp://localhost:5555");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ZMsg msg = new ZMsg();
            msg.send(socket);

            ZMsg req = ZMsg.recvMsg(socket);
            if (req == null) {
                break;
            }

        }
    }

    public void close() {
        context.destroySocket(socket);
    }
}
