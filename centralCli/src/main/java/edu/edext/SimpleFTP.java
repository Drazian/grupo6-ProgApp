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
        String message, path=cfgCCli.PATH, puerto="8080", unionPath=" -d "+path+utilsCCli.OS.getSeparador()+"output", comando="python3 -m http.server ";
        ProcessBuilder proc;
        Process pid;
        int exitPID;
        puerto=utilsCCli.Cast.valueOf(utilsCCli.OS.scanPortFree(puerto));
        unionPath=utilsCCli.Validate.isEmpty(ruta)?ruta:unionPath;
        comando=comando+puerto+unionPath;
            try {
                proc=new ProcessBuilder(comando.split(" "));
                proc.inheritIO();
                pid=proc.start();
                exitPID=pid.waitFor();
                //Thread.sleep(30000);
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