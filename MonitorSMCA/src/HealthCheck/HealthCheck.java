package HealthCheck;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMonitor;

public class HealthCheck {

    public static void main(String[] args) {

        try (ZContext context = new ZContext()) {

            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:" + args[2]);
            ZMonitor zMonitor = new ZMonitor(context, socket);
            zMonitor.add(ZMonitor.Event.CONNECTED, ZMonitor.Event.DISCONNECTED);
            zMonitor.start();
            String file = args[0] + args[1] + ".txt";
            ThreadHealth t = new ThreadHealth(file, args[3], args[4]);
            while (!Thread.currentThread().isInterrupted()) {
                ZMonitor.Event event = zMonitor.nextEvent().type;
                System.out.println("[ ESTADO ] -> " + event);
                if(event == ZMonitor.Event.DISCONNECTED && !t.isAlive()){
                    t.start();
                }
                if(event == ZMonitor.Event.CONNECTED && t.isAlive()){
                    System.out.println("[MONITOR AUXILIAR] -> STOP");
                    t.stop();
                    t = new ThreadHealth(file, args[3], args[4]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
