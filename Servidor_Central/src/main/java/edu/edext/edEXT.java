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
    public static void main(String[] args) {
        java.util.logging.LogManager.getLogManager().reset();
        Endpoint endpoint=null;
        CountDownLatch pid = new CountDownLatch(1);
        cfg.init();
        try {
            System.out.println("Iniciando Servidor Central...");
            Logger.info("Iniciando Servidor Central...");
            String wsdlURl=cfg.wsdl();
            endpoint = Endpoint.publish(wsdlURl, new BridgeSOAP());
            System.out.println("Publicacion SOAP exitosa en "+wsdlURl);
            Logger.info("Publicacion SOAP exitosa en "+wsdlURl);
            edu.edext.presentacion.index.main(args, pid);
            pid.await();
        } catch (Exception e) {
            Logger.error(e," Error al levantar el servicio SOAP ");
            System.err.println("Error al levantar el servicio SOAP ....\nPor favor revise la configuracion en "+cfg.PATH_FILE);
        } finally {
            if(endpoint != null && endpoint.isPublished()){
                System.out.println("Deteniendo el servicio SOAP ......");
                endpoint.stop();
                System.out.println("Servicio SOAP detenido.");
                Logger.info("Servicio SOAP detenido.");
            }
            Utils.Disk.delete(cfg.PATH_FILE_PROPERTIES);
            System.gc();
            System.exit(0);
        }
    }
}