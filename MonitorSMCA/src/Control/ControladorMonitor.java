package Control;

import Interface.IPersistencia;
import Modelo.Medida;
import Persistencia.Persistencia;

import java.io.File;
import java.util.Scanner;

public class ControladorMonitor {

    IPersistencia iPersistencia = new Persistencia();
    double limitMin;
    double limitMax;

    public ControladorMonitor() {
        iPersistencia.connectDatabase();
    }

    public double getLimitMin() {
        return limitMin;
    }

    public void setLimitMin(double limitMin) {
        this.limitMin = limitMin;
    }

    public double getLimitMax() {
        return limitMax;
    }

    public void setLimitMax(double limitMax) {
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

    public boolean evaluarMedida(double medida){
        if(medida >= limitMin && medida <= limitMax){
            return true;
        }else{
            return false;
        }
    }

}
