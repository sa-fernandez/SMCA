package Persistencia;

import Interface.IPersistencia;
import Modelo.Medida;
import Modelo.Usuario;

import java.sql.*;

public class Persistencia implements IPersistencia {

    Connection connection = null;

    public Persistencia() {
    }

    public void connectDatabase(){
        try{
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Test?user=root&password=123Ferchito*");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void persistirUsuario(Usuario usuario){
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

    public void persistirMedida(Medida medida){
        try{
            String query = "insert into Medida values(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, medida.getTipo().toString());
            preparedStatement.setInt(2, medida.getValor());
            preparedStatement.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean verificarContrasena(Usuario usuario){
        boolean result = false;
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Usuario");
            while(resultSet.next()){
                if(usuario.getPassword().equals(resultSet.getString("contrasenia"))){
                    result = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
