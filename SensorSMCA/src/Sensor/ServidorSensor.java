package Sensor;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.Scanner;

public class ServidorSensor {

    public static void main(String[] args) {

        System.out.println("[ServidorSensor] Inicializacion de sensores");
        try (ZContext context = new ZContext()) {

            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.bind("tcp://*:" + args[0]);

            boolean seguir = true;
            String tipo, config, opc;
            int tiempo;
            ArrayList<Sensor> sensors = new ArrayList<>();
            Scanner scanLine = new Scanner(System.in);
            Scanner scanInt = new Scanner(System.in);

            do{
                System.out.print("Tipo de sensor [TEMP, OXI, PH]: ");
                tipo = scanLine.nextLine();
                tipo = tipo.toUpperCase();
                System.out.print("Cantidad de tiempo (segundos) para generar medida: ");
                tiempo = scanInt.nextInt();
                System.out.print("Ruta a archivo de configuracion: ");
                config = scanLine.nextLine();
                Sensor sensor = new Sensor(socket, tipo, tiempo, config);
                sensor.start();
                sensors.add(sensor);
                System.out.print("(S -> Incluir nuevo sensor || N -> Parar todos los sensores) > ");
                opc = scanLine.nextLine();
                opc = opc.toUpperCase();
                if(opc.equals("N")){
                    seguir = false;
                }
            }while(seguir);

            for(Sensor s : sensors){
                s.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}