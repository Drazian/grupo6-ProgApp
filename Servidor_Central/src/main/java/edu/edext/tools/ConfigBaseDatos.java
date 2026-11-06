package edu.edext.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import org.tinylog.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Conexion del Servidor Central a la base de datos (requerimiento 7.8 "Distribucion de la Aplicacion Final").
 *
 * La conexion se configura en ~/.edEXT/servidor.properties, fuera del servidor.jar: se puede cambiar
 * sin volver a generar el paquete (reiniciando el Servidor Central).
 *   - Si el archivo no existe, se crea con los valores de META-INF/persistence.xml (los de las iteraciones anteriores).
 *   - Si existe pero le falta alguna clave, se le agrega con el valor de persistence.xml.
 *   - Las claves vacias se ignoran: queda el valor de persistence.xml.
 *
 * La carpeta de imagenes y la direccion del Web Service siguen en ~/.edEXT/edEXT.cfg (clase cfg).
 */
public final class ConfigBaseDatos {

    public static final String ARCHIVO = "servidor.properties";

    // clave en servidor.properties -> propiedad JPA de persistence.xml, con su descripcion para el archivo
    private static final String[][] CLAVES = {
        {"db.url",      "jakarta.persistence.jdbc.url",      "URL de conexion JDBC (servidor, puerto y nombre de la base)"},
        {"db.driver",   "jakarta.persistence.jdbc.driver",   "Driver JDBC"},
        {"db.usuario",  "jakarta.persistence.jdbc.user",     "Usuario de la base de datos"},
        {"db.password", "jakarta.persistence.jdbc.password", "Contrasena del usuario"},
        {"db.esquema",  "jakarta.persistence.schema-generation.database.action", "Creacion de tablas al iniciar: none | create | drop-and-create | update"},
    };

    private ConfigBaseDatos() { }

    /** Ruta del archivo: ~/.edEXT/servidor.properties */
    public static Path archivo() {
        return Path.of(System.getProperty("user.home"), ".edEXT", ARCHIVO);
    }

    /**
     * Propiedades para Persistence.createEntityManagerFactory("edext", ...): reemplazan a las de persistence.xml.
     */
    public static Map<String, String> propiedadesJPA() {
        Path ruta = archivo();
        Map<String, String> ret = new HashMap<>();
        try {
            Map<String, String> defecto = leerPersistenceXml();
            completarArchivo(ruta, defecto);
            Properties archivo = new Properties();
            try (Reader r = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
                archivo.load(r);
            }
            for (String[] c : CLAVES) {
                String valor = archivo.getProperty(c[0]);
                if (valor != null && !valor.isBlank())
                    ret.put(c[1], valor.trim());
            }
            Logger.info("Base de datos: {} (usuario {}). Configuracion: {}",
                    ret.getOrDefault(CLAVES[0][1], defecto.get(CLAVES[0][1])),
                    ret.getOrDefault(CLAVES[2][1], defecto.get(CLAVES[2][1])), ruta);
        } catch (Exception e) {
            // Sin el archivo se usa persistence.xml, como antes
            Logger.error(e, "No se pudo leer la configuracion de la base de datos en {}: se usa la de persistence.xml", ruta);
        }
        return ret;
    }

    // Crea el archivo si no existe y le agrega las claves que le falten (sin tocar las que ya estan)
    private static void completarArchivo(Path ruta, Map<String, String> defecto) throws IOException {
        Properties actual = new Properties();
        boolean existe = Files.exists(ruta);
        if (existe)
            try (Reader r = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
                actual.load(r);
            }

        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();
        if (!existe) {
            sb.append("# edEXT - Servidor Central: conexion a la base de datos").append(nl)
              .append("# Se puede modificar sin volver a generar servidor.jar; reiniciar el Servidor Central para aplicar los cambios.").append(nl)
              .append("# La carpeta de imagenes y la direccion del Web Service estan en edEXT.cfg.").append(nl);
        }
        boolean faltan = false;
        for (String[] c : CLAVES) {
            if (actual.containsKey(c[0])) continue;
            if (!faltan && existe)
                sb.append(nl).append("# Agregado automaticamente (conexion a la base de datos)").append(nl);
            faltan = true;
            sb.append(nl).append("# ").append(c[2]).append(nl)
              .append(c[0]).append('=').append(escapar(defecto.getOrDefault(c[1], ""))).append(nl);
        }
        if (!faltan) return;

        Files.createDirectories(ruta.getParent());
        if (existe && !terminaEnSalto(ruta)) sb.insert(0, nl);
        Files.writeString(ruta, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Logger.info("{} {}", existe ? "Se agregaron claves de la base de datos a" : "Se creo", ruta);
    }

    // Valores por defecto: las propiedades de META-INF/persistence.xml (unidad "edext")
    private static Map<String, String> leerPersistenceXml() {
        Map<String, String> ret = new HashMap<>();
        try (InputStream in = ConfigBaseDatos.class.getResourceAsStream("/META-INF/persistence.xml")) {
            if (in == null) return ret;
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            NodeList props = f.newDocumentBuilder().parse(in).getElementsByTagName("property");
            for (int i = 0; i < props.getLength(); i++) {
                Element p = (Element) props.item(i);
                ret.put(p.getAttribute("name"), p.getAttribute("value"));
            }
        } catch (Exception e) {
            Logger.warn(e, "No se pudo leer META-INF/persistence.xml");
        }
        return ret;
    }

    private static boolean terminaEnSalto(Path ruta) throws IOException {
        byte[] b = Files.readAllBytes(ruta);
        return b.length == 0 || b[b.length - 1] == '\n';
    }

    // En un .properties la barra invertida es caracter de escape
    private static String escapar(String valor) {
        return valor.replace("\\", "\\\\");
    }
}
