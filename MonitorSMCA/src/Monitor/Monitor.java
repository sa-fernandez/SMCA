package Monitor;

import Control.ControladorMonitor;
import Enum.TipoMedida;

import Modelo.Medida;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.FileWriter;

public class Monitor {

    static ControladorMonitor controladorMonitor;

    public static void main(String[] args) {

        controladorMonitor = new ControladorMonitor(args[4], args[5]);
        controladorMonitor.establecerLimites("bounds\\config_bounds_" + args[0] + ".txt");
        System.out.println("[MONITOR] -> " + args[0]);

        try{
            FileWriter fileWriter = new FileWriter("configs\\config_" + args[0] + ".txt");
            fileWriter.write("tcp://" + args[1] + ":" + args[2] + "-" + args[0]);
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        try (ZContext context = new ZContext()) {

            /**
             * Health Check.
             */
            ZMQ.Socket hSocket = context.createSocket(SocketType.REP);
            hSocket.bind("tcp://localhost:" + args[3]);

            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect("tcp://" + args[1] + ":" + args[2]);
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
                if(controladorMonitor.evaluarMedida(valor)){
                    controladorMonitor.persistirMedida(medida);
                }else{
                    controladorMonitor.enviarAlerta(medida.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
