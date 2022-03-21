package ServidorPS;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class ServidorPS {

    public static void main(String[] args) {
        System.out.println("[Sistema Publicador-Suscriptor] Publisher ZeroMQ");

        try (ZContext context = new ZContext()) {

            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.bind("tcp://" + args[0] + ":" + args[1]);

            ZMQ.Socket tSocket = context.createSocket(SocketType.SUB);
            tSocket.connect("tcp://" + args[0] + ":" + args[2]);
            tSocket.subscribe("TEMP".getBytes());

            ZMQ.Socket oSocket = context.createSocket(SocketType.SUB);
            oSocket.connect("tcp://" + args[0] + ":" + args[2]);
            oSocket.subscribe("OXI".getBytes());

            ZMQ.Socket pSocket = context.createSocket(SocketType.SUB);
            pSocket.connect("tcp://" + args[0] + ":" + args[2]);
            pSocket.subscribe("PH".getBytes());

            ZMQ.Poller poller = context.createPoller(3);

            poller.register(tSocket, ZMQ.Poller.POLLIN);
            poller.register(oSocket, ZMQ.Poller.POLLIN);
            poller.register(pSocket, ZMQ.Poller.POLLIN);

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Recibiendo medida...");
                poller.poll();
                String message = "";
                if(poller.pollin(0)){
                    message = tSocket.recvStr();
                }else if(poller.pollin(1)){
                    message = oSocket.recvStr();
                }else if(poller.pollin(2)){
                    message = pSocket.recvStr();
                }
                System.out.println("Sending..." + message);
                socket.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
