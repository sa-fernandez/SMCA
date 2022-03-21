package Sensor;

import org.zeromq.ZMQ;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Sensor extends Thread {

    ZMQ.Socket socket;
    String tipo;
    int tiempo;
    String config;

    File file;
    int limitMin, limitMax;
    ArrayList<Double> probs = new ArrayList<>();

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
            probs.add(Double.parseDouble(parts2[0]));
            probs.add(Double.parseDouble(parts2[1]));
            probs.add(Double.parseDouble(parts2[2]));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private double medidaRandom(){
        ArrayList<Double> generador = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < probs.get(0) * 10; i++){
            generador.add(rand.nextDouble(limitMax - limitMin) + limitMin);
        }
        for(int i = 0; i < probs.get(1) * 10; i++){
            generador.add(rand.nextDouble(limitMax) + limitMax);
        }
        for(int i = 0; i < probs.get(2) * 10; i++){
            generador.add(-rand.nextDouble());
        }
        return generador.get(rand.nextInt(generador.size()));
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Random rand = new Random();
                LocalDateTime localDateTime = LocalDateTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss");
                String message = tipo + " " + (Math.round(medidaRandom() * 100.0) / 100.0) + " " + dateTimeFormatter.format(localDateTime);
                socket.send(message);
                Thread.sleep(tiempo * 1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}