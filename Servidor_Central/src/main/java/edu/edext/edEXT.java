package edu.edext;

import org.tinylog.Logger;
import edu.edext.tools.cfg;
import edu.edext.tools.Utils;
import jakarta.xml.ws.Endpoint;
import edu.edext.logica.BridgeSOAP;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author vdraco
 */
public class edEXT {
    private static int estado=0;
    public static int CODE_RESET=42, CODE_ERROR_DB=50;
    
    public static void main(String[] args) {
        java.util.logging.LogManager.getLogManager().reset();
        Endpoint endpoint=null;
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
        Utils.Disk.delete(cfg.PATH_TEMPORAL_PORT);
        System.gc();
        System.exit(edEXT.estado);
    }
}
