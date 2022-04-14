package HealthCheck;

import Control.ControladorMonitor;
import Enum.TipoMedida;
import Modelo.Medida;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.File;
import java.util.Scanner;

public class ThreadHealth extends Thread {

    ZMQ.Socket socket;
    String connection;
    String sensor;
    String config;
    String databaseHost;
    String alertHost;

    public ThreadHealth(String config, String databaseHost, String alertHost) {
        this.config = config;
        this.databaseHost = databaseHost;
        this.alertHost = alertHost;
        try{
            File file = new File(config);
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                String data = scan.nextLine();
                String[] parts = data.split("-");
                connection = String.valueOf(parts[0]);
                sensor = String.valueOf(parts[1]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

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
