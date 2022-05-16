package Vista;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


import Controlador.ControladorRegistro;
import Modelo.Usuario;

import java.nio.charset.StandardCharsets;
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
            if(opc.toUpperCase().equals("R")){
                System.out.print("Ingrese su nuevo nombre de usuario: ");
                usuario = scan.nextLine();
                System.out.print("Ingrese su nueva contrasena: ");
                contrasena = scan.nextLine();
                contrasena = controladorRegistro.hashContrasena(contrasena);
                actual = new Usuario(usuario, contrasena);
                controladorRegistro.registrarUsuario(actual);
                System.out.println("Usuario Registrado");
                seguir = false;
            }else if(opc.toUpperCase().equals("I")){
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

        try (ZContext context = new ZContext()) {
            ZMQ.Socket smTemp = context.createSocket(SocketType.SUB);
            smTemp.connect("tcp://" + args[1] + ":" + args[3]);
            smTemp.subscribe("[ ALERTA ]".getBytes());

            ZMQ.Socket smaTemp = context.createSocket(SocketType.SUB);
            smaTemp.connect("tcp://" + args[2] + ":" + args[3]);
            smaTemp.subscribe("[ ALERTA ]".getBytes());

            ZMQ.Socket smPh = context.createSocket(SocketType.SUB);
            smPh.connect("tcp://" + args[1] + ":" + args[4]);
            smPh.subscribe("[ ALERTA ]".getBytes());

            ZMQ.Socket smaPh = context.createSocket(SocketType.SUB);
            smaPh.connect("tcp://" + args[2] + ":" + args[4]);
            smaPh.subscribe("[ ALERTA ]".getBytes());

            ZMQ.Socket smOxi = context.createSocket(SocketType.SUB);
            smOxi.connect("tcp://" + args[1] + ":" + args[5]);
            smOxi.subscribe("[ ALERTA ]".getBytes());

            ZMQ.Socket smaOxi = context.createSocket(SocketType.SUB);
            smaOxi.connect("tcp://" + args[2] + ":" + args[5]);
            smaOxi.subscribe("[ ALERTA ]".getBytes());

            ZMQ.Poller poller = context.createPoller(6);

            poller.register(smTemp, ZMQ.Poller.POLLIN);
            poller.register(smaTemp, ZMQ.Poller.POLLIN);
            poller.register(smPh, ZMQ.Poller.POLLIN);
            poller.register(smaPh, ZMQ.Poller.POLLIN);
            poller.register(smOxi, ZMQ.Poller.POLLIN);
            poller.register(smaOxi, ZMQ.Poller.POLLIN);

            System.out.println("Recibiendo alertas...");
            while (!Thread.currentThread().isInterrupted()) {
                poller.poll();
                String message = "";
                if(poller.pollin(0)){
                    message = smTemp.recvStr();
                }else if(poller.pollin(1)){
                    message = smaTemp.recvStr();
                }else if(poller.pollin(2)){
                    message = smPh.recvStr();
                }else if(poller.pollin(3)){
                    message = smaPh.recvStr();
                }else if(poller.pollin(4)){
                    message = smOxi.recvStr();
                }else if(poller.pollin(5)){
                    message = smaOxi.recvStr();
                }
                System.out.println(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
