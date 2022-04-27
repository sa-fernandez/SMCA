package Control;

import Interface.IControladorAlerta;
import Interface.IPersistencia;
import Modelo.Medida;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ControladorMonitor {

    IPersistencia iPersistencia;
    IControladorAlerta iControladorAlerta;
    double limitMin;
    double limitMax;

    public ControladorMonitor(String databaseHost, String alertHost) {
        /**
         * Bind Database.
         */
        try {
            System.out.println("Binding rmi://" + databaseHost + ":" + 1098 + "/PersistenciaSMCA...");
            Registry registry = LocateRegistry.getRegistry(databaseHost, 1098);
            iPersistencia = (IPersistencia) registry.lookup("PersistenciaSMCA");
            System.out.println("[PersistenciaRMI] : ON");
            iPersistencia.connectDatabase();
        } catch(Exception e) {
            System.err.println("System exception" + e);
        }
        /**
         * Bind Alert.
         */
        try {
            System.out.println("Binding rmi://" + alertHost + ":" + 1099 + "/AlertRMI...");
            Registry registry = LocateRegistry.getRegistry(alertHost, 1099);
            iControladorAlerta = (IControladorAlerta) registry.lookup("AlertRMI");
            System.out.println("[AlertRMI] : ON");
        } catch(Exception e) {
            System.err.println("System exception" + e);
        }
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
        try {
            iPersistencia.persistirMedida(medida);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void enviarAlerta(String alerta){
        try{
            iControladorAlerta.desplegarAlerta(alerta);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean evaluarMedida(double medida){
        if(medida >= limitMin && medida <= limitMax){
            return true;
        }else{
            return false;
        }
    }

}
