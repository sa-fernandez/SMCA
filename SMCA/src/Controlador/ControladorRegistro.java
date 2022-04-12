package Controlador;

import Interface.IPersistencia;
import Modelo.Usuario;
import Persistencia.Persistencia;

import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;

public class ControladorRegistro {

    IPersistencia iPersistencia;

    public ControladorRegistro(String databaseHost) {
        /**
         * Bind Database.
         */
        try {
            System.out.println("Binding rmi://" + databaseHost + ":" + 1098 + "/PersistenciaSMCA...");
            Registry registry = LocateRegistry.getRegistry(databaseHost, 1098);
            iPersistencia = (IPersistencia) registry.lookup("PersistenciaSMCA");
            System.out.println("[PersistenciaSMCA] : ON");
            iPersistencia.connectDatabase();
        } catch(Exception e) {
            System.err.println("System exception" + e);
        }
    }

    public void registrarUsuario(Usuario usuario){
        try {
            iPersistencia.persistirUsuario(usuario);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean verificarUsuario(Usuario usuario){
        try {
            return iPersistencia.verificarContrasena(usuario);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean iniciarSesion(Usuario usuario){
        return verificarUsuario(usuario);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String hashContrasena(String contrasena){
        String hashContrasena = "";
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            hashContrasena = this.bytesToHex(hash);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return hashContrasena;
    }

}
