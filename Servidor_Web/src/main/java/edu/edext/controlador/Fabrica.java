package edu.edext.controlador;

import edu.edext.logica.IBridgeSOAP;
//import edu.edext.controlador.ws.BridgeSOAPService;
//import edu.edext.controlador.ws.IBridgeSOAP;
import jakarta.xml.ws.BindingProvider;
import edu.edext.modelo.cfg;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class Fabrica {

    public static IBridgeSOAP getConexion(){
        IBridgeSOAP ret=null;
//        BridgeSOAPService service=new BridgeSOAPService();
//        IBridgeSOAP ret=service.getBridgeSOAPPort();
        BindingProvider tmp=(BindingProvider)ret;
        tmp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.wsdl());
        Logger.info("Conectando con : "+cfg.wsdl());
        return ret;

    }
    
}