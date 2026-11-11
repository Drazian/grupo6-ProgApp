package edu.edext;

import edu.edext.tools.Utils;
import edu.edext.tools.cfg;
import java.io.IOException;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class SimpleFTP {

    public static void start(){
        boolean flag=true, reset=false;
        String message, path=cfg.PATH, puerto="8080", nombreFolder="output", union=" -d "+path+Utils.OS.getSeparador(), comando="python3 -m http.server ";
        ProcessBuilder proc;
        Process pid;
        int exitPID;
        
        puerto=Utils.Cast.valueOf(Utils.OS.scanPortFree(puerto));
        comando=comando+puerto+union+nombreFolder;
        //System.out.println("Preparando instalacion..... : "+comando);
//        Logger.info("Inicializando servicio.....");
            try {
                proc=new ProcessBuilder(comando.split(" "));
//                message=reset?"Reinicio exitoso.":"Inicializacion exitosa.";
                System.out.println("Archivos compartidos en "+puerto);
//                if(reset) reset=false;
//                System.out.println(message);
//                Logger.info(message);
                proc.inheritIO();
                pid=proc.start();
//                exitPID=pid.waitFor();
//                if(exitPID==42){
//                    System.out.println("Reiniciando servicio.....");
//                    Logger.info("Reiniciando servicio.....");
//                    Thread.sleep(1500);
//                    reset=true;
//                }else{
//                    System.out.println("Cerrando servicio......");
//                    flag=false;
//                }
//                System.out.println("Servicio SOAP detenido.");
                Thread.sleep(30000);
                pid.destroy();
            } catch (IOException | InterruptedException e) {
                System.err.println("Error al compartir archivos.");
                Logger.error(e, "Error al compartir archivos ");
            } finally {
                proc=null;
                pid=null;
                if(reset==false){
//                    System.out.println("Servidor detenido.");
//                    Logger.info("Servidor detenido.");
                }
            }

        
    }
    
}
