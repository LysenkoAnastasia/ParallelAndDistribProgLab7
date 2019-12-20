import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.zeromq.*;

import java.util.Scanner;

public class Client{
    private static int SAMPLE_SIZE = 10000;
    private  static ZContext context;
    private  static  ZMQ.Socket socket;
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("HERE");
        try {
            context = new ZContext();
            socket = context.createSocket(SocketType.REQ);
            socket.setHWM(0);
            //socket.setIdentity("C".getBytes(ZMQ.CHARSET));
            socket.connect("tcp://localhost:5555");
            while (true) {

                String message = in.nextLine();
                //System.out.println(message);
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                if (message.contains("GET") || message.contains("PUT")) {
                    System.out.println("get or put");
                    ZMsg msg = new ZMsg();
                    msg.addString(message);

                    msg.send(socket);

                    ZMsg req = ZMsg.recvMsg(socket);
                    if (req == null) {
                        break;
                    }
                    String s = msg.popString();
                    System.out.println( "MSG ");
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
