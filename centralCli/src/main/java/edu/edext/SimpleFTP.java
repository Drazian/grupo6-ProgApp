package edu.edext;

import edu.edext.tools.utilsCCli;
import edu.edext.tools.cfgCCli;
import java.io.IOException;

/**
 *
 * @author vdraco
 */
public class SimpleFTP {


    public static void start(String ruta){
        boolean flag=true, reset=false;
        String message, path=cfgCCli.PATH, puerto="1050", unionPath=" -d ", comando="python3 -m http.server ";
        ProcessBuilder proc;
        Process pid;
        int exitPID;
//        puerto=puerto!=null?puerto:PUERTO;
//        System.out.println("puerto '"+puerto+"' , comando : "+comando);
        puerto=utilsCCli.Cast.valueOf(utilsCCli.OS.scanPortFree(puerto));
//        System.out.println("puerto '"+puerto+"' , comando : "+comando);
//        unionPath=utilsCCli.Validate.isEmpty(ruta)?ruta:unionPath;
//        System.out.println("puerto '"+puerto+"' , comando : "+comando);
        comando=comando+puerto+unionPath+ruta;
        System.out.println("Compartiendo : "+ruta);
            try {
                System.out.println("");
                proc=new ProcessBuilder(comando.split(" "));
                proc.inheritIO();
                pid=proc.start();
                exitPID=pid.waitFor();
                pid.destroy();
                System.out.println("Teminando...");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error al compartir archivos.");
            } finally {
                proc=null;
                pid=null;
                if(reset==false){
                }
            }
    }
    
}