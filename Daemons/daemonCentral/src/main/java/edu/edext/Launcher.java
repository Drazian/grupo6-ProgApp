package edu.edext;

import static edu.edext.Main.CODE_GUI;
import static edu.edext.Main.CODE_KILL;
import static edu.edext.Main.CODE_RESET;
import static edu.edext.Main.CODE_START;
import static edu.edext.Main.CODE_STATUS;
import static edu.edext.Main.CODE_STOP;
import edu.edext.tools.Utils;
import edu.edext.tools.cfg;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public final class Launcher {
//    public static int CODE_RESET=42, CODE_ERROR_DB=50;
    private static final String HOST="localhost";
    private static final int PUERTO_SEVER=9998;    
    private static String comando;
    private static boolean flag=true;
    
    public static void start(){
    }



    private static String validate(String comando){
        switch (Utils.Cast.valueof(comando)) {
            case CODE_RESET -> {
                return "Reiniciando el servidor central...";
            }
            case CODE_STATUS -> {
                return "Servidor central operando normalmente.";
            }
            case CODE_START -> {
                //Launcher.start();
                return "Servidor central se esta iniciando.";
            }
            case CODE_GUI -> {
                return "Interface Grafica.";
            }
            case CODE_STOP -> {
                return "Servidor central se esta cerrando.";
            }
            case CODE_KILL -> {
                flag=false;
                return "Terminando el servicio.....";
            }
            default -> {
                return "ERROR: Comando no reconocido por el demonio.";
            }
        }
    }



    
    private static void daemon(){
        Thread daemon=new Thread(() -> {
        try (ServerSocket listening = new ServerSocket(PUERTO_SEVER)) {
            while(flag){
                try (Socket socket = listening.accept();
                     BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)){
                    comando = entrada.readLine();
                    if(comando!= null)
                        salida.println(validate(comando)); }
                catch (IOException e) {
                    System.err.println("Error procesando petición: " + e.getMessage()); } } }
        catch (IOException e) {
            System.err.println("No se pudo abrir el puerto: " + e.getMessage()); }
        finally {
            System.out.println("Servicio terminado."); } });
        
        daemon.setName("daemonCentral");
//        daemon.setDaemon(true);
        daemon.start();
    }
    
    private void exec(){
        boolean flag=true, reset=false;
        String message, comando="java -jar"+" servidor.jar";
        ProcessBuilder proc;
        Process pid;
        int exitPID;
        System.out.println("Inicializando servicio.....");
        Logger.info("Inicializando servicio.....");
        while(flag){
            try {
                proc=new ProcessBuilder(comando.split(" "));
                message=reset?"Reinicio exitoso.":"Inicializacion exitosa.";
                if(reset) reset=false;
                System.out.println(message);
                Logger.info(message);
                proc.inheritIO();
                pid=proc.start();
                exitPID=pid.waitFor();
                if(exitPID==Main.CODE_ERROR_DB)
                    Utils.OS.open(cfg.PATH_CONFIG_DB);
                else if(exitPID==Main.CODE_RESET){
                    System.out.println("Reiniciando servicio.....");
                    Logger.info("Reiniciando servicio.....");
                    Thread.sleep(1500);
                    reset=true;
                }else{
                    System.out.println("Cerrando servicio......");
                    flag=false;
                }
                System.out.println("Servicio SOAP detenido.");
                Thread.sleep(1500);
                pid.destroy();
            } catch (IOException | InterruptedException e) {
                System.err.println("Error durante la Inicializacion.");
                Logger.error(e, "Error durante la Inicializacion := ");
            } finally {
                proc=null;
                pid=null;
                if(reset==false){
                    System.out.println("Servidor detenido.");
                    Logger.info("Servidor detenido.");
                }
            }
        }
    }
    
    
}
