package Monitor;

import Control.ControladorMonitor;
import Enum.TipoMedida;

import Modelo.Medida;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Monitor {

    static ControladorMonitor controladorMonitor = new ControladorMonitor();

    public static void main(String[] args) {

        controladorMonitor.establecerLimites("bounds\\config_bounds_" + args[0] + ".txt");
        System.out.println("[MONITOR] -> " + args[0]);

        try (ZContext context = new ZContext()) {

            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect("tcp://localhost:5556");
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
                int valor = Integer.parseInt(parts[1]);
                Medida medida = new Medida(tipo, valor);
                System.out.println(medida);
                if(controladorMonitor.evaluarMedida(valor)){
                    controladorMonitor.persistirMedida(medida);
                }else{
                    //EnviarAlerta
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
