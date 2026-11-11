package edu.edext.controlador;

import edu.edext.controlador.ws.IBridgeSOAPService;
import edu.edext.controlador.ws.IBridgeSOAP;
import jakarta.xml.ws.BindingProvider;
import edu.edext.tools.cfgw;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class Fabrica {

    public static IBridgeSOAP getConexion(){
        IBridgeSOAPService service=new IBridgeSOAPService();
        IBridgeSOAP ret=service.getBridgeSOAPPort();
        BindingProvider tmp=(BindingProvider)ret;
        tmp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfgw.getWSDL());
        Logger.info("Conectando con : "+cfgw.getWSDL());
        return ret;
    }
    
}