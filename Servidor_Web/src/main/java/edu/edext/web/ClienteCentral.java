package edu.edext.web;

import edu.edext.cliente.Publicador;
import edu.edext.cliente.PublicadorService;
import jakarta.xml.ws.BindingProvider;
import java.util.Map;
import org.tinylog.Logger;

/**
 * Acceso a los Web Services del Servidor Central (reemplaza a Fabrica.getInstance().getIControlador()).
 * El contrato (WSDL) va dentro del war; la direccion real se toma de ws.url en ~/.edEXT/web.properties.
 */
public final class ClienteCentral {

    // Sin estos limites, si el Servidor Central no responde la pagina quedaria esperando indefinidamente
    private static final int TIMEOUT_CONEXION_MS = 5000;
    private static final int TIMEOUT_RESPUESTA_MS = 30000;

    private static Publicador puerto;

    private ClienteCentral() {}

    public static synchronized Publicador get() {
        if (puerto == null) {
            String url = ConfiguracionWeb.get(ConfiguracionWeb.WS_URL);
            Publicador nuevo = new PublicadorService().getPublicadorPort();
            Map<String, Object> contexto = ((BindingProvider) nuevo).getRequestContext();
            contexto.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
            contexto.put("com.sun.xml.ws.connect.timeout", TIMEOUT_CONEXION_MS);
            contexto.put("com.sun.xml.ws.request.timeout", TIMEOUT_RESPUESTA_MS);
            Logger.info("Cliente de Web Services del Servidor Central apuntando a {}", url);
            puerto = nuevo;
        }
        return puerto;
    }
}
