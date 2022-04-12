package Vista;

import Controlador.ControladorAlerta;
import Controlador.ControladorRegistro;
import Modelo.Usuario;

import java.util.Scanner;

public class SistemaCalidad {

    static ControladorRegistro controladorRegistro;

    public static void main(String[] args) {
        controladorRegistro = new ControladorRegistro(args[0]);
        System.out.println("====================================");
        System.out.println("=========SISTEMA DE CALIDAD=========");
        System.out.println("====================================");
        Scanner scan = new Scanner(System.in);
        boolean seguir = true;
        String usuario, contrasena;
        Usuario actual = new Usuario();
        do{
            System.out.print("(R -> Registro || I -> Ingreso) > ");
            String opc = scan.nextLine();
            opc.toUpperCase();
            if(opc.equals("R")){
                System.out.print("Ingrese su nuevo nombre de usuario: ");
                usuario = scan.nextLine();
                System.out.print("Ingrese su nueva contrasena: ");
                contrasena = scan.nextLine();
                contrasena = controladorRegistro.hashContrasena(contrasena);
                actual = new Usuario(usuario, contrasena);
                controladorRegistro.registrarUsuario(actual);
                System.out.println("Usuario Registrado");
                seguir = false;
            }else if(opc.equals("I")){
                System.out.print("Ingrese su nombre de usuario: ");
                usuario = scan.nextLine();
                System.out.print("Ingrese su contrasena: ");
                contrasena = scan.nextLine();
                contrasena = controladorRegistro.hashContrasena(contrasena);
                actual = new Usuario(usuario, contrasena);
                if(controladorRegistro.iniciarSesion(actual)){
                    System.out.println("Ingreso exitoso");
                    seguir = false;
                }else{
                    System.out.println("Ingreso fallido, intentelo nuevamente");
                }
            }else{
                System.out.println("Intentelo nuevamente");
            }
        }while(seguir);

        System.out.println();
        System.out.println("====================================");
        System.out.println("===============Inicio===============");
        System.out.println("====================================");
        System.out.println(actual);

        System.out.println("ALERTAS !");

        try{
            ControladorAlerta controladorAlertaTest = new ControladorAlerta("AlertRMI");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
