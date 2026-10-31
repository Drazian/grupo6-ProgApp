package edu.edext.web;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.tinylog.Logger;

/**
 * Configuracion externa del Servidor Web (req. 7.7 y 7.8): ~/.edEXT/web.properties.
 * Se puede cambiar sin volver a generar el war; se aplica al reiniciar la aplicacion en Tomcat.
 */
public final class ConfiguracionWeb {

    public static final String PATH = System.getProperty("user.home") + File.separator + ".edEXT";
    public static final String PATH_PROPERTIES = PATH + File.separator + "web.properties";

    public static final String WS_URL = "ws.url";

    private static Properties propiedades;

    private ConfiguracionWeb() {}

    public static synchronized Properties propiedades() {
        if (propiedades == null) {
            Properties defecto = new Properties();
            defecto.setProperty(WS_URL, "http://localhost:9128/edext/publicador");
            File archivo = new File(PATH_PROPERTIES);
            if (!archivo.exists())
                crear(defecto);
            propiedades = new Properties(defecto);
            try (Reader r = new FileReader(archivo, StandardCharsets.UTF_8)) {
                propiedades.load(r);
                Logger.info("Configuracion de la web cargada de ..: {}", PATH_PROPERTIES);
            } catch (IOException e) {
                Logger.error("Error leyendo configuracion {} : {}", PATH_PROPERTIES, e.getMessage());
            }
        }
        return propiedades;
    }

    public static String get(String clave) {
        return propiedades().getProperty(clave);
    }

    private static void crear(Properties defecto) {
        new File(PATH).mkdirs();
        try (Writer w = new FileWriter(PATH_PROPERTIES, StandardCharsets.UTF_8)) {
            w.write("# Configuracion de edEXT - Servidor Web\n");
            w.write("# Se puede modificar sin volver a generar el war; reiniciar la aplicacion en Tomcat para aplicar cambios.\n\n");
            w.write("# URL de los Web Services del Servidor Central (si esta en otra maquina, usar su IP)\n");
            w.write(WS_URL + "=" + defecto.getProperty(WS_URL) + "\n");
            Logger.info("Archivo de configuracion de la web creado en ..: {}", PATH_PROPERTIES);
        } catch (IOException e) {
            Logger.error("Error al crear configuracion {} : {}", PATH_PROPERTIES, e.getMessage());
        }
    }
}
