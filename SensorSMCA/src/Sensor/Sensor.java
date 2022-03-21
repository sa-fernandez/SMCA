package Sensor;

import org.zeromq.ZMQ;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class Sensor extends Thread {

    ZMQ.Socket socket;
    String tipo;
    int tiempo;
    String config;

    File file;
    int limitMin, limitMax;
    double correcto, fuera, error;

    public Sensor(ZMQ.Socket socket, String tipo, int tiempo, String config) {
        this.socket = socket;
        this.tipo = tipo;
        this.tiempo = tiempo;
        this.config = config;

        try{
            file = new File(config);
            Scanner scan = new Scanner(file);
            String limits = scan.nextLine();
            String[] parts1 = limits.split("-");
            limitMin = Integer.parseInt(parts1[0]);
            limitMax = Integer.parseInt(parts1[1]);
            String porcentajes = scan.nextLine();
            String[] parts2 = porcentajes.split(",");
            correcto = Double.parseDouble(parts2[0]);
            fuera = Double.parseDouble(parts2[1]);
            error = Double.parseDouble(parts2[2]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Random rand = new Random();
                String message = tipo + " " + String.valueOf(rand.nextInt(100 - 60) + 60);
                socket.send(message);
                Thread.sleep(tiempo * 1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}