package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IControladorAlerta extends Remote {

    public void desplegarAlerta(String alerta) throws RemoteException;

}
