package Controlador;

import Interface.IControladorAlerta;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ControladorAlerta extends UnicastRemoteObject implements IControladorAlerta {

    Registry registry;

    public ControladorAlerta(String name) throws RemoteException {
        super();
        try{
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind(name, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void desplegarAlerta(String alerta) throws RemoteException {
        System.out.println("[ ALERTA ] -> " + alerta);
    }

}
