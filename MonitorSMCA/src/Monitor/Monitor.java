package Monitor;

import Control.ControladorMonitor;
import Enum.TipoMedida;
import Modelo.Medida;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Monitor {

    static ControladorMonitor controladorMonitor;

    public static void main(String[] args) {

        try (ZContext context = new ZContext()) {

            ZMQ.Socket alertSocket = context.createSocket(SocketType.PUB);
            alertSocket.bind("tcp://*:" + args[1]);

            controladorMonitor = new ControladorMonitor(args[4], alertSocket);
            controladorMonitor.establecerLimites("bounds\\config_bounds_" + args[0] + ".txt");
            System.out.println("[MONITOR] -> " + args[0]);

            /**
             * Health Check.
             */
            ZMQ.Socket hSocket = context.createSocket(SocketType.REP);
            hSocket.bind("tcp://*:" + args[5]);

            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect("tcp://" + args[2] + ":" + args[3]);
            socket.subscribe(args[0].getBytes());

            while (!Thread.currentThread().isInterrupted()) {
                String message = socket.recvStr();
                System.out.println("Received: " + message);
                String[] parts = message.split(" ");
                String topic = parts[0];
                TipoMedida tipo = null;
                if(topic.equals("TEMP")){
                    tipo = TipoMedida.TEMP;
                }else if(topic.equals("OXI")){
                    tipo = TipoMedida.OXI;
                }else if(topic.equals("PH")){
                    tipo = TipoMedida.PH;
                }
                double valor = Double.parseDouble(parts[1]);
                Medida medida = new Medida(tipo, valor);
                System.out.println(medida);
                if(controladorMonitor.evaluarError(valor)){
                    controladorMonitor.persistirMedida(medida);
                    if(!controladorMonitor.evaluarRango(valor)){
                        controladorMonitor.enviarAlerta(medida.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
