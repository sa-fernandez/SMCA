package Persistencia;

import Interface.IPersistencia;
import Modelo.Medida;
import Modelo.Usuario;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class Persistencia extends UnicastRemoteObject implements IPersistencia {

    Connection connection = null;
    Registry registry;

    public Persistencia(String name) throws java.rmi.RemoteException {
        super();
        try {
            System.out.println("Rebind Object: " + name);
            registry = LocateRegistry.createRegistry(1098);
            registry.rebind(name, this);
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void connectDatabase() throws java.rmi.RemoteException {
        try{
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Test?user=root&password=123Ferchito*");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void persistirUsuario(Usuario usuario) throws java.rmi.RemoteException {
        try{
            String query = "insert into Usuario values(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, usuario.getUsername());
            preparedStatement.setString(2, usuario.getPassword());
            preparedStatement.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void persistirMedida(Medida medida) throws java.rmi.RemoteException {
        try{
            String query = "insert into Medida values(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, medida.getTipo().toString());
            preparedStatement.setDouble(2, medida.getValor());
            preparedStatement.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean verificarContrasena(Usuario usuario) throws java.rmi.RemoteException {
        boolean result = false;
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Usuario");
            while(resultSet.next()){
                if(usuario.getUsername().equals(resultSet.getString("nombreUsu"))) {
                    if (usuario.getPassword().equals(resultSet.getString("contrasenia"))) {
                        result = true;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
