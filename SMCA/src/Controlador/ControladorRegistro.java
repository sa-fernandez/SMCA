package Controlador;

import Interface.IPersistencia;
import Modelo.Usuario;
import Persistencia.Persistencia;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ControladorRegistro {

    IPersistencia iPersistencia = new Persistencia();

    public ControladorRegistro() {
        iPersistencia.connectDatabase();
    }

    public void registrarUsuario(Usuario usuario){
        iPersistencia.persistirUsuario(usuario);
    }

    private boolean verificarUsuario(Usuario usuario){
        return iPersistencia.verificarContrasena(usuario);
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
