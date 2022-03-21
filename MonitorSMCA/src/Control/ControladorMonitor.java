package Control;

import Interface.IPersistencia;
import Modelo.Medida;
import Persistencia.Persistencia;

import java.io.File;
import java.util.Scanner;

public class ControladorMonitor {

    IPersistencia iPersistencia = new Persistencia();
    int limitMin;
    int limitMax;

    public ControladorMonitor() {
        iPersistencia.connectDatabase();
    }

    public int getLimitMin() {
        return limitMin;
    }

    public void setLimitMin(int limitMin) {
        this.limitMin = limitMin;
    }

    public int getLimitMax() {
        return limitMax;
    }

    public void setLimitMax(int limitMax) {
        this.limitMax = limitMax;
    }

    public void establecerLimites(String filename){
        try{
            File file = new File(filename);
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                String data = scan.nextLine();
                String[] parts = data.split("-");
                limitMin = Integer.parseInt(parts[0]);
                limitMax = Integer.parseInt(parts[1]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void persistirMedida(Medida medida){
        iPersistencia.persistirMedida(medida);
    }

    public void enviarFallo(){

    }

    public boolean evaluarMedida(int medida){
        if(medida >= limitMin && medida <= limitMax){
            return true;
        }else{
            return false;
        }
    }

}
