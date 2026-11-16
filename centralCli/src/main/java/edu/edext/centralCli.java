package edu.edext;

import edu.edext.tools.utilsCCli;
import edu.edext.tools.cfgCCli;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author vdraco
 */
public class centralCli {
    private static final String HOST = "localhost";
//    private static final int PUERTO = 9999;    
    private static final int CODE_START=40, CODE_GUI=42, CODE_STATUS=44, CODE_RESET=46, CODE_STOP=48, CODE_ERROR_DB=50, CODE_KILL=99;
    private static String comando, listen;
    private static long pid;
    private static boolean kill;
//    private static int puerto=PUERTO, reitentos=1;
    
    private static void preayuda(){
        System.out.println("Sintaxis : edEXT [opciones]");
        System.out.println("\n\t-?     -h   -help\tmuestra la lista de comandos permitidos ");
        System.out.println("\t--?    --h  --help\t");
    }

    private static void ayuda(){
        preayuda();
        System.out.println("\n");
//        System.out.println("\t-gui    -g  -form \tactiva la interfaz grafica");
        System.out.println("\tstart    |  encender\tInicia el Servidor ");
//        System.out.println("\tstatus   |  estado\tMuestra los valores de los SOAP");
//        System.out.println("\tconnect  |  conectar\tEnvia una señal de sincronizacion");
//        System.out.println("\trestart  |  reiniciar\treinicia el Servidor");
//        System.out.println("\tstop     |  apagar\tapaga el Servidor");
//        System.out.println("\tkill     |  matar\t\ttermina el Servicio");
        System.out.println("\tshared   |  compartir\t\tcomparte el directorio seleccionado");

    }
    
    public static boolean isEmpty(String cadena){
        return cadena==null||cadena.isBlank();
    }
    
    public static void main(String[] args) {
        if(args.length==0)
            preayuda();
        else if(args[0].equals("?") || args[0].equals("-?") || args[0].equals("--?") || 
                args[0].toLowerCase().equals("h") || args[0].toLowerCase().equals("-h") || args[0].toLowerCase().equals("--h") || 
                args[0].toLowerCase().equals("help") || args[0].toLowerCase().equals("-help") || args[0].toLowerCase().equals("--help")){
            ayuda(); }
        else if(args[0].toLowerCase().equals("--g") || args[0].toLowerCase().equals("--gui") || args[0].toLowerCase().equals("--form") ||
                args[0].toLowerCase().equals("-g") || args[0].toLowerCase().equals("-gui") || args[0].toLowerCase().equals("-form") || 
                args[0].toLowerCase().equals("g") || args[0].toLowerCase().equals("gui") || args[0].toLowerCase().equals("form")){
            comando=String.valueOf(CODE_GUI); }
        else if(args[0].toLowerCase().equals("start") || args[0].toLowerCase().equals("encender") ||
                args[0].toLowerCase().equals("-start") || args[0].toLowerCase().equals("-encender") ||
                args[0].toLowerCase().equals("--start") || args[0].toLowerCase().equals("--encender")){
            Launcher.start();
            comando=String.valueOf(CODE_START); }
        else if(args[0].toLowerCase().equals("status") || args[0].toLowerCase().equals("estado") ||
                args[0].toLowerCase().equals("-status") || args[0].toLowerCase().equals("--estado") ||
                args[0].toLowerCase().equals("--status") || args[0].toLowerCase().equals("--estado")){
            comando=String.valueOf(CODE_STATUS); }
        else if(args[0].toLowerCase().equals("stop") || args[0].toLowerCase().equals("apagar") ||
                args[0].toLowerCase().equals("-stop") || args[0].toLowerCase().equals("-apagar") ||
                args[0].toLowerCase().equals("--stop") || args[0].toLowerCase().equals("--apagar")){
            comando=String.valueOf(CODE_STOP); }
        else if(args[0].toLowerCase().equals("restart") || args[0].toLowerCase().equals("reiniciar") ||
                args[0].toLowerCase().equals("-restart") || args[0].toLowerCase().equals("-reiniciar") ||
                args[0].toLowerCase().equals("--restart") || args[0].toLowerCase().equals("--reiniciar")){
            comando=String.valueOf(CODE_RESET); }
        else if(args[0].toLowerCase().equals("kill") || args[0].toLowerCase().equals("matar") ||
                args[0].toLowerCase().equals("-kill") || args[0].toLowerCase().equals("-matar") ||
                args[0].toLowerCase().equals("--kill") || args[0].toLowerCase().equals("--matar")){
            kill=true;
            comando=String.valueOf(CODE_KILL); }
        
        // **************************** comandos ocultos ****************************
        else if(args[0].toLowerCase().equals("deploy") || args[0].toLowerCase().equals("desplegar")){
        }
        else if(args[0].toLowerCase().equals("shared") || args[0].toLowerCase().equals("compartir")){
            String path;
            if(args.length==2)
                path=args[1].toLowerCase().trim();
            else
                path=utilsCCli.Disk.openPathOS();
            if(!utilsCCli.Validate.isEmpty(path)) 
                SimpleFTP.start(path);
            
        }
        else preayuda();

//        if(!isEmpty(comando)){
//            try (Socket socket = new Socket(HOST, utilsCCli.Cast.valueof(cfgCCli.getPuerto()));
//                 PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
//                 BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
//                    salida.println(comando);
//                    listen=entrada.readLine();
//                    System.out.println("respuesta : "+listen);
////                    if(listen.startsWith("exit:"))
////                        pid=Long.parseLong(listen.substring(listen.lastIndexOf(":")+1));
////                    try {
////                        Thread.sleep(1000);
////                        utilsCCli.OS.exec("kill "+listen.substring(listen.lastIndexOf(":")+1));
////                    } catch (InterruptedException e) {                }
//                        
////                            System.out.println("llego el pid : "+listen.substring(listen.lastIndexOf(":")+1));
//            }
//            catch (IOException e) {
//                if(!kill){
//                    System.err.println("Error: No se pudo conectar con el demonio.");
//                    System.out.println("Cargado demonio ....");
//                    System.out.println("Cliente escuchando ..."+cfgCCli.getPuerto());
//                    utilsCCli.OS.exec("bin/daemonCentral");
//                }
//            }
//        }
    }
}