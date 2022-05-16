package MonitorAuxiliar;

import HealthCheck.ThreadHealth;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMonitor;

public class MonitorAuxiliar {

    public static void main(String[] args) {

        try (ZContext context = new ZContext()) {

            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://" + args[1] + ":" + args[6]);
            ZMonitor zMonitor = new ZMonitor(context, socket);
            zMonitor.add(ZMonitor.Event.CONNECTED, ZMonitor.Event.DISCONNECTED);
            zMonitor.start();

            ZMQ.Socket alertSocket = context.createSocket(SocketType.PUB);
            alertSocket.bind("tcp://*:" + args[2]);

            ThreadHealth t = new ThreadHealth(args[0], args[3], args[4], args[5], alertSocket);
            while (!Thread.currentThread().isInterrupted()) {
                ZMonitor.Event event = zMonitor.nextEvent().type;
                System.out.println("[ ESTADO ] -> " + event);
                if(event == ZMonitor.Event.DISCONNECTED && !t.isAlive()){
                    t.start();
                }
                if(event == ZMonitor.Event.CONNECTED && t.isAlive()){
                    System.out.println("[MONITOR AUXILIAR] -> STOP");
                    t.stop();
                    t = new ThreadHealth(args[0], args[3], args[4], args[5], alertSocket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
