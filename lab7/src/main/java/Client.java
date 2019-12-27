import org.zeromq.*;

import java.util.Scanner;

public class Client{
    private  ZContext context;
    private ZMQ.Socket socket;
    private Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("connect");
        try {
            ZContext context = new ZContext();
            Client client = new Client(context);
            client.connect();
            client.handle();

        } catch (ZMQException ex) {
            ex.printStackTrace();
        }
    }

    public Client(ZContext context) {
        this.context = context;
        this.socket = context.createSocket(SocketType.REQ);
        this.socket.setHWM(0);
    }

    public void connect() {
        socket.connect("tcp://localhost:5556");
    }

    public void handle() {
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
                String s = req.popString();
                System.out.println("MSG: " + s);
                req.destroy();
            }
            else  {
                System.out.println("incorrect");
            }
        }
    }
}