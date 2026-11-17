package edu.edext;

import java.io.File;
import org.tinylog.Logger;
import edu.edext.tools.cfg;
import java.io.IOException;
import edu.edext.tools.Utils;
import jakarta.xml.ws.Endpoint;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import edu.edext.logica.BridgeSOAP;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author vdraco
 */
public class edEXT {
    private static int estado=0;
    public static int CODE_RESET=46, CODE_ERROR_DB=50, CODE_DUPLICIDAD=52;
    private static FileLock appLock;
    private static FileChannel appChannel;
    
    public static void main(String[] args) {
        java.util.logging.LogManager.getLogManager().reset();
        Endpoint endpoint=null;
        if(!isAppUnica()){
            System.out.println("El Servidor ya se encuentra activo");
            System.exit(CODE_DUPLICIDAD); }
        CountDownLatch pid = new CountDownLatch(1);
        cfg.init();
        try {
            System.out.println("Iniciando Servidor Central...");
            Logger.info("Iniciando Servidor Central...");
            String wsdlURl=cfg.getWSDL();
            endpoint = Endpoint.publish(wsdlURl, new BridgeSOAP());
            System.out.println("Publicacion SOAP exitosa.");
            Logger.info("Publicacion SOAP exitosa en "+wsdlURl);
            edu.edext.presentacion.index.main(args, pid);
            pid.await();
            System.out.println("Deteniendo servicio SOAP ......");
            Thread.sleep(1500);
        } catch (Exception e) {
            Logger.error(e," Error al levantar el servicio SOAP ");
            System.err.println("Error al levantar el servicio SOAP ....\nPor favor revise la configuracion en "+cfg.PATH_CONFIG_FILE);
        } finally {
            if(endpoint != null && endpoint.isPublished()){
                endpoint.stop();
                System.out.println("Servicio SOAP detenido.");
                Logger.info("Servicio SOAP detenido.");
            }
        }
        close();
    }
    
    public static int exit(int estado){
        edEXT.estado=estado;
        close();
        return estado;
    }
    
    private static void close(){
        Utils.Disk.delete(cfg.PATH_JAXWS_PROPERTIES);
//        Utils.Disk.delete(cfg.PATH_TEMPORAL_PORT);
        System.gc();
        System.exit(edEXT.estado);
    }
    
    private static boolean isAppUnica(){
        try {
            File tmp=new File(cfg.PATH_FILE_LOCK, cfg.FILE_LOCK);
            appChannel=new RandomAccessFile(tmp, "rw").getChannel();
            appLock=appChannel.tryLock();
            //System.out.println("bloqueo exitoso");
            if(appLock!=null){
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try { if(appLock!=null) appLock.release(); if(appChannel!=null) appChannel.close(); tmp.delete(); } catch (Exception e) {}
                }));
                return true; } }
        catch (IOException e) {}
        return false;
    }
}