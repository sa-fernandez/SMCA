package HealthCheck;

import Control.ControladorMonitor;
import Enum.TipoMedida;
import Modelo.Medida;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ThreadHealth extends Thread {

    ZMQ.Socket socket;
    String host;
    String port;
    String sensor;
    String databaseHost;
    String alertHost;

    public ThreadHealth(String sensor, String host, String port, String databaseHost, String alertHost) {
        this.sensor = sensor;
        this.host = host;
        this.port = port;
        this.databaseHost = databaseHost;
        this.alertHost = alertHost;
    }

    @Override
    public void run() {

        String connection = "tcp://" + host + ":" + port;

        ControladorMonitor controladorMonitor = new ControladorMonitor(databaseHost, alertHost);
        controladorMonitor.establecerLimites("bounds\\config_bounds_" + sensor + ".txt");

        System.out.println("[MONITOR AUXILIAR] -> " + sensor + " | " + connection);

        try (ZContext context = new ZContext()){

            socket = context.createSocket(SocketType.SUB);
            socket.connect(connection);
            socket.subscribe(sensor.getBytes());

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
