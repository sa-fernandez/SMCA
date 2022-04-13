package Sensor;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ServidorSensor {

    public static void main(String[] args) {

        System.out.println("[ServidorSensor] Inicializacion de sensores");
        try (ZContext context = new ZContext()) {

            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.bind("tcp://" + args[0] + ":" + args[1]);

            String[] topics = {"TEMP", "OXI", "PH"};
            boolean seguir = true;
            String tipo, config, opc;
            int tiempo;
            ArrayList<Sensor> sensors = new ArrayList<>();
            Scanner scanLine = new Scanner(System.in);
            Scanner scanInt = new Scanner(System.in);

            do {
                System.out.print("(R -> Generacion aleatoria || I -> Instancia manual) > ");
                opc = scanLine.nextLine();
                if (opc.toUpperCase().equals("R")) {
                    int cant;
                    do {
                        System.out.print("Digite cantidad de sensores (hilos) a generar [MAXIMO 15] > ");
                        cant = scanInt.nextInt();
                    } while (cant > 15);
                    Random rand = new Random();
                    for (int i = 0; i < cant; i++) {
                        tipo = topics[rand.nextInt(topics.length)];
                        tiempo = rand.nextInt(20 - 5) + 5;
                        config = "configs\\config_" + tipo + ".txt";
                        Sensor sensor = new Sensor(socket, tipo, tiempo, config);
                        sensor.start();
                        System.out.println(sensor);
                        sensors.add(sensor);
                    }
                    do {
                        System.out.print("(P -> Parar todos los sensores) > ");
                        opc = scanLine.nextLine();
                        if (opc.equals("P")) {
                            seguir = false;
                        }
                    } while (seguir);
                } else if (opc.toUpperCase().equals("I")) {
                    do {
                        System.out.print("Tipo de sensor [TEMP, OXI, PH]: ");
                        tipo = scanLine.nextLine();
                        tipo = tipo.toUpperCase();
                        System.out.print("Cantidad de tiempo (segundos) para generar medida: ");
                        tiempo = scanInt.nextInt();
                        System.out.print("Ruta a archivo de configuracion: ");
                        config = scanLine.nextLine();
                        Sensor sensor = new Sensor(socket, tipo, tiempo, config);
                        sensor.start();
                        System.out.println(sensor);
                        sensors.add(sensor);
                        System.out.print("(S -> Incluir nuevo sensor || N -> Parar todos los sensores) > ");
                        opc = scanLine.nextLine();
                        opc = opc.toUpperCase();
                        if (opc.equals("N")) {
                            seguir = false;
                        }
                    } while (seguir);
                } else {
                    System.out.println("Intentelo nuevamente");
                }
            } while (seguir);

            for(Sensor s : sensors){
                s.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}