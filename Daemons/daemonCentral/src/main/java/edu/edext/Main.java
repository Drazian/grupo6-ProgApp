package edu.edext;

import edu.edext.tools.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tinylog.Logger;
/**
 *
 * @author vdraco
 */
public class Main {
    private static final String HOST="localhost";
    private static final int PUERTO_DAEMON=9999;    
    public static final int CODE_START=40, CODE_GUI=42, CODE_STATUS=44, CODE_RESET=46, CODE_STOP=48, CODE_ERROR_DB=50, CODE_KILL=99;
    private static String comando;
    private static boolean flag=true;

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
        try (ServerSocket listening = new ServerSocket(PUERTO_DAEMON)) {
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
        
        daemon.setName("daemonCli");
//        daemon.setDaemon(true);
        daemon.start();
    }
    

    
    
    public static void main(String[] args) throws InterruptedException {
//SimpleFTP.start();
        
//      ExecutorService daemon=Executors.newSingleThreadExecutor();  
        daemon();
        //System.out.println("\n");
//      daemon.execute(Main::daemon);
//      daemon.execute(() -> daemon(""));
        

        
    }
    
}