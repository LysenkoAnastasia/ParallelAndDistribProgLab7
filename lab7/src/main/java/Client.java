import org.zeromq.*;

import java.util.Scanner;

public class Client{
    private static int SAMPLE_SIZE = 10000;
    private  static ZContext context;
    private  static  ZMQ.Socket socket = context.createSocket(SocketType.REQ);
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            context = new ZContext();
            socket.setHWM(0);
            socket.setIdentity("C".getBytes(ZMQ.CHARSET));
            socket.connect("tcp://localhost:5555");
            while (true) {
                String message = in.nextLine();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (message.contains("GET") || message.contains("PUT")) {
                    ZMsg msg = new ZMsg();
                    msg.addString(message);
                    msg.send(socket);

                    ZMsg req = ZMsg.recvMsg(socket);
                    if (req == null) {
                        break;
                    }
                    msg.popString();
                    req.destroy();
                }
                else  {
                    System.out.println("incorrect");
                }
            }

        } catch (ZMQException ex) {
            ex.printStackTrace();
        }
    }
}
