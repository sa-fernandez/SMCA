package Interface;

import Modelo.Medida;
import Modelo.Usuario;

import java.rmi.Remote;

public interface IPersistencia extends Remote {

    public void connectDatabase() throws java.rmi.RemoteException;
    public void persistirUsuario(Usuario usuario) throws java.rmi.RemoteException;
    public void persistirMedida(Medida medida) throws java.rmi.RemoteException;
    public boolean verificarContrasena(Usuario usuario) throws java.rmi.RemoteException;

}
