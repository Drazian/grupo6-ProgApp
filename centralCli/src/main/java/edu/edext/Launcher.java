package edu.edext;

import edu.edext.tools.cfgCCli;
import edu.edext.tools.utilsCCli;
import java.io.File;
import java.io.IOException;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public final class Launcher {
    public static int CODE_RESET=46, CODE_ERROR_DB=50, CODE_DUPLICIDAD=52;
    
    public static void start(){
        start(null);
    }
    public static void start(String nombre){
        boolean flag=true, reset=false;
        String comando, separador=utilsCCli.OS.getSeparador();
        if(!utilsCCli.Validate.isEmpty(nombre))
            if(nombre.endsWith(".jar"))
                comando="java -jar "+nombre;
            else
                comando="."+separador+nombre;
        else
            comando="./servidor";
//        if(utilsCCli.Disk.isExist(separador+"bin"+separador+comando))
        
        String message; //"./servidor";//"java -jar"+" servidor.jar";
        ProcessBuilder proc;
        Process pid;
        int exitPID=0;
        System.out.println("Inicializando servicio.....");
        Logger.info("Inicializando servicio.....");
        while(flag){
            try {
                proc=new ProcessBuilder(comando.split(" "));
                message=reset?"Reinicio exitoso.":"Inicializacion exitosa.";
                if(reset) reset=false;
                System.out.println(message);
                proc.directory(new File("bin"));
                Logger.info(message);
                proc.inheritIO();
                pid=proc.start();
                exitPID=pid.waitFor();
                if(exitPID==CODE_ERROR_DB){
                    utilsCCli.OS.open(cfgCCli.PATH_CONFIG_DB);
                    System.err.println("Error en la DB durante la Inicializacion.");
                    Logger.error("Error en la DB durante la Inicializacion.");
                    flag=false; }
                else if(exitPID==CODE_RESET){
                    System.out.println("Reiniciando servicio.....");
                    Logger.info("Reiniciando servicio.....");
                    Thread.sleep(1500);
                    reset=true; }
                else{
                    System.out.println("Cerrando servicio......");
                    flag=false; }
                Thread.sleep(1500);
                pid.destroy(); }
            catch (IOException | InterruptedException e) {
                System.err.println("Error durante la Inicializacion.");
                Logger.error(e, "Error durante la Inicializacion := ");
                flag=false; }
            finally {
                proc=null;
                pid=null;
                if(reset==false && exitPID!=CODE_DUPLICIDAD){
                    System.out.println("Servidor detenido.");
                    Logger.info("Servidor detenido."); } }
        }
    }
}