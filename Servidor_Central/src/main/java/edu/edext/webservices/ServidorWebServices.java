package edu.edext.webservices;

import edu.edext.tools.cfg;
import jakarta.xml.ws.Endpoint;
import org.tinylog.Logger;

/**
 * Publica los Web Services del Servidor Central con el servidor HTTP que trae la JDK
 * (Endpoint.publish usa com.sun.net.httpserver). La URL se lee de ws.url en ~/.edEXT/servidor.properties.
 *
 * La Estacion de Trabajo (index) los publica al arrancar. Para un nodo sin pantalla se puede publicar
 * solo el servidor: java -cp servidor.jar edu.edext.webservices.ServidorWebServices
 */
public final class ServidorWebServices {

    private static Endpoint endpoint;
    private static String urlPublicada;

    private ServidorWebServices() {}

    /** Publica en la URL configurada en ws.url. */
    public static String publicar() {
        return publicar(cfg.get(cfg.WS_URL));
    }

    /** Publica en la URL indicada; si ya estaba publicado, lo detiene antes. */
    public static synchronized String publicar(String url) {
        detener();
        endpoint = Endpoint.publish(url, new Publicador());
        urlPublicada = url;
        Logger.info("Web Services publicados en {} (WSDL: {}?wsdl)", url, url);
        return url;
    }

    public static synchronized void detener() {
        if (endpoint != null) {
            endpoint.stop();
            Logger.info("Web Services detenidos en {}", urlPublicada);
        }
        endpoint = null;
        urlPublicada = null;
    }

    // endpoint solo queda asignado si Endpoint.publish termino bien, y detener() lo vuelve a null
    public static synchronized boolean estaPublicado() {
        return endpoint != null;
    }

    public static synchronized String getUrlPublicada() {
        return urlPublicada;
    }

    /** Solo el Servidor Central, sin la Estacion de Trabajo (para un nodo sin interfaz grafica). */
    public static void main(String[] args) {
        String url = publicar();
        Runtime.getRuntime().addShutdownHook(new Thread(ServidorWebServices::detener));
        System.out.println("Web Services de edEXT publicados en " + url + "  (WSDL: " + url + "?wsdl)");
        System.out.println("Ctrl+C para detener.");
    }
}
