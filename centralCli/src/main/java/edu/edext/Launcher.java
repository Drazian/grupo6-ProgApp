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
    public static int CODE_RESET=42, CODE_ERROR_DB=50;
    
    public static void start(){
        boolean flag=true, reset=false;
        String message, comando="./servidor";//"java -jar"+" servidor.jar";
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
                proc.directory(new File("bin"));
                Logger.info(message);
                proc.inheritIO();
                pid=proc.start();
                exitPID=pid.waitFor();
                if(exitPID==CODE_ERROR_DB)
                    utilsCCli.OS.open(cfgCCli.PATH_CONFIG_DB);
                else if(exitPID==CODE_RESET){
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
