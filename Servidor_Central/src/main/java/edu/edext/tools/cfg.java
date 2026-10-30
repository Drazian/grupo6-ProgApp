package edu.edext.tools;

import edu.edext.tools.Utils.CfgDB;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.tinylog.Logger;

/**
 * Configuracion externa de la aplicacion, guardada en ~/.edEXT/servidor.properties
 * para poder cambiarla sin volver a generar el jar/war.
 *
 * @author vdraco
 */
public class cfg {

    public static final String PATH=getHomePath()+getSeparador()+".edEXT";
    // Se puede usar otro archivo con -Dedext.config=<ruta> (lo usan los tests para correr contra H2)
    public static final String PATH_PROPERTIES=System.getProperty("edext.config", PATH+getSeparador()+"servidor.properties");
    // Archivo de configuracion de la iteracion anterior, solo se lee para migrar la ruta de imagenes
    public static final String PATH_FILE=PATH+getSeparador()+"edEXT.cfg";
    public static final String PATH_INTERNAL="/Images/";
    //public static final String IMAGE_USER = "%#@.png";
    //%#@.jpeg
    public static final String IMAGE_USER = "usr.png";
    private static final String SYM_HOME="~";

    public static final String DB_URL="db.url";
    public static final String DB_DRIVER="db.driver";
    public static final String DB_USUARIO="db.usuario";
    public static final String DB_PASSWORD="db.password";
    public static final String DB_ESQUEMA="db.esquema";
    public static final String IMAGEN_RUTA="imagen.ruta";

    private static Properties propiedades;

    public static void init(){
        propiedades();
    }

    /// @return Las propiedades de ~/.edEXT/servidor.properties (lo crea con valores por defecto si no existe)
    public static synchronized Properties propiedades(){
        if(propiedades==null){
            Properties defecto=valoresPorDefecto();
            File archivo=new File(PATH_PROPERTIES);
            if(!archivo.exists())
                crearProperties(defecto);
            propiedades=new Properties(defecto);
            try (Reader r = new FileReader(archivo, StandardCharsets.UTF_8)){
                propiedades.load(r);
                Logger.info("Configuracion cargada de ..: {}", PATH_PROPERTIES);
            } catch (IOException e){  Logger.error("Error leyendo configuracion {} : {}", PATH_PROPERTIES, e.getMessage());  }
        }
        return propiedades;
    }

    public static String get(String clave){
        return propiedades().getProperty(clave);
    }

    /// @return Propiedades de conexion para Persistence.createEntityManagerFactory (sobrescriben persistence.xml)
    public static Map<String, Object> propiedadesJPA(){
        Map<String, Object> ret=new HashMap<>();
        ret.put("jakarta.persistence.jdbc.url", get(DB_URL));
        ret.put("jakarta.persistence.jdbc.driver", get(DB_DRIVER));
        ret.put("jakarta.persistence.jdbc.user", get(DB_USUARIO));
        ret.put("jakarta.persistence.jdbc.password", get(DB_PASSWORD));
        ret.put("jakarta.persistence.schema-generation.database.action", get(DB_ESQUEMA));
        return ret;
    }

    public static String pathImagen(){
        return get(IMAGEN_RUTA).replace(SYM_HOME, getHomePath());
    }

    public static String fullpathImagen(){
        return pathImagen()+getSeparador();
    }

    private static Properties valoresPorDefecto(){
        Properties p=new Properties();
        p.setProperty(DB_URL, "jdbc:mariadb://localhost:3306/edext?createDatabaseIfNotExist=true");
        p.setProperty(DB_DRIVER, "org.mariadb.jdbc.Driver");
        p.setProperty(DB_USUARIO, "pipo");
        p.setProperty(DB_PASSWORD, "pipo");
        p.setProperty(DB_ESQUEMA, "update");
        p.setProperty(IMAGEN_RUTA, rutaImagenAnterior());
        return p;
    }

    // Si existe el edEXT.cfg de la iteracion anterior se respeta su carpeta de imagenes
    private static String rutaImagenAnterior(){
        String ret=SYM_HOME+"/.edEXT/imagen";
        if(new File(PATH_FILE).exists())
            for (CfgDB cfgDB : Utils.Disk.loadCFG(PATH_FILE))
                if(cfgDB.clave().equals("imagen"))
                    ret=cfgDB.valor();
        return ret.replace('\\', '/');
    }

    private static void crearProperties(Properties p){
        Utils.Disk.createFolder(PATH);
        try (Writer w = new FileWriter(PATH_PROPERTIES, StandardCharsets.UTF_8)){
            w.write("# Configuracion de edEXT - Servidor Central\n");
            w.write("# Se puede modificar sin recompilar; reiniciar la aplicacion para aplicar cambios.\n");
            w.write("# Usar / como separador de carpetas. ~ representa el directorio home del usuario.\n\n");
            w.write("# Base de datos\n");
            escribir(w, p, DB_URL);
            escribir(w, p, DB_DRIVER);
            escribir(w, p, DB_USUARIO);
            escribir(w, p, DB_PASSWORD);
            w.write("# create | update | none\n");
            escribir(w, p, DB_ESQUEMA);
            w.write("\n# Carpeta de imagenes\n");
            escribir(w, p, IMAGEN_RUTA);
            Logger.info("Archivo de configuracion creado en ..: {}", PATH_PROPERTIES);
        } catch (IOException e) {
            Logger.error("Error al crear configuracion {} : {}", PATH_PROPERTIES, e.getMessage());
        }
    }

    private static void escribir(Writer w, Properties p, String clave) throws IOException{
        w.write(clave+"="+p.getProperty(clave)+"\n");
    }

    private static String getSeparador(){
        return java.io.File.separator;
    }

    private static String getHomePath(){
        return System.getProperty("user.home");
    }
}
