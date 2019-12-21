import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.zeromq.*;

import java.util.Scanner;

public class Client{
    private static int SAMPLE_SIZE = 10000;
    private  static ZContext context;
    private  static  ZMQ.Socket socket;
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("connect");
        try {
            context = new ZContext();
            socket = context.createSocket(SocketType.REQ);
            socket.setHWM(0);
            socket.connect("tcp://localhost:5555");
            while (true) {

                String message = in.nextLine();
                if (message.contains("GET") || message.contains("PUT")) {
                    ZMsg msg = new ZMsg();
                    msg.addString(message);
                    msg.send(socket);

                    ZMsg req = ZMsg.recvMsg(socket);
                    if (req == null) {
                        break;
                    }
                    System.out.println("look");
                    String s = req.popString();
                    System.out.println("IN : " + s);
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
