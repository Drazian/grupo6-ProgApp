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

    public static void start(String ruta){
        boolean flag=true, reset=false;
        String message, path=cfg.PATH, puerto="8080", unionPath=" -d "+path+Utils.OS.getSeparador()+"output", comando="python3 -m http.server ";
        ProcessBuilder proc;
        Process pid;
        int exitPID;
        
        puerto=Utils.Cast.valueOf(Utils.OS.scanPortFree(puerto));
        unionPath=Utils.Validate.isEmpty(ruta)?ruta:unionPath;
        comando=comando+puerto+unionPath;
        //System.out.println("Preparando instalacion..... : "+comando);
//        Logger.info("Inicializando servicio.....");
            try {
                proc=new ProcessBuilder(comando.split(" "));
//                message=reset?"Reinicio exitoso.":"Inicializacion exitosa.";
                System.out.println("Archivos compartidos en "+puerto);
                proc.inheritIO();
                pid=proc.start();
                Thread.sleep(30000);
                pid.destroy();
            } catch (IOException | InterruptedException e) {
                System.err.println("Error al compartir archivos.");
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