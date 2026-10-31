package edu.edext.tools;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Claves nuevas en servidor.properties (ws.url): se escriben al crear el archivo y se agregan
 * a un archivo de una version anterior. Corre en su propia JVM (reuseForks=false), asi que puede
 * apuntar cfg a un archivo propio antes de que la clase se cargue.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class cfgCompletarPropertiesTest {

    private static Path archivo;

    @BeforeAll
    public static void apuntarConfiguracion() throws Exception {
        Path carpeta = Path.of("target", "cfg-ws-test");
        Files.createDirectories(carpeta);
        archivo = carpeta.resolve("servidor.properties").toAbsolutePath();
        archivo.toFile().setWritable(true);
        Files.deleteIfExists(archivo);
        System.setProperty("edext.config", archivo.toString());
    }

    @AfterAll
    public static void restaurar() {
        archivo.toFile().setWritable(true);
    }

    // cfg cachea las propiedades: para volver a leer el archivo se limpia la cache
    private static void recargar() throws Exception {
        Field cache = cfg.class.getDeclaredField("propiedades");
        cache.setAccessible(true);
        cache.set(null, null);
    }

    private static String contenido() throws Exception {
        return Files.readString(archivo, StandardCharsets.UTF_8);
    }

    private static void escribirArchivoViejo() throws Exception {
        Files.writeString(archivo, "db.url=jdbc:h2:mem:viejo\ndb.driver=org.h2.Driver\ndb.usuario=sa\ndb.password=\n"
                + "db.esquema=create\nimagen.ruta=target/test-imagenes\n", StandardCharsets.UTF_8);
    }

    @Test
    @Order(1)
    public void testArchivoNuevoIncluyeWsUrl() throws Exception {
        assertEquals(archivo.toString(), cfg.PATH_PROPERTIES);
        assertEquals("http://localhost:9128/edext/publicador", cfg.get(cfg.WS_URL));
        assertTrue(contenido().contains("ws.url=http://localhost:9128/edext/publicador"));
        assertTrue(contenido().contains("# URL donde el Servidor Central publica los Web Services."));
    }

    @Test
    @Order(2)
    public void testArchivoViejoSeCompletaConLasClavesNuevas() throws Exception {
        escribirArchivoViejo();
        recargar();

        assertEquals("http://localhost:9128/edext/publicador", cfg.get(cfg.WS_URL));
        assertEquals("jdbc:h2:mem:viejo", cfg.get(cfg.DB_URL), "Lo que ya estaba configurado se respeta");
        String texto = contenido();
        assertTrue(texto.startsWith("db.url=jdbc:h2:mem:viejo"), "No se reescribe el archivo, solo se agrega al final");
        assertTrue(texto.contains("# Agregado automaticamente"));
        assertTrue(texto.endsWith("ws.url=http://localhost:9128/edext/publicador\n"));
    }

    @Test
    @Order(3)
    public void testArchivoCompletoNoSeModifica() throws Exception {
        String antes = contenido();
        recargar();
        cfg.get(cfg.WS_URL);
        assertEquals(antes, contenido());
    }

    @Test
    @Order(4)
    public void testArchivoDeSoloLecturaNoRompeLaCarga() throws Exception {
        escribirArchivoViejo();
        File f = archivo.toFile();
        assertTrue(f.setWritable(false));
        recargar();

        // No se puede agregar la clave, pero se usa su valor por defecto igual
        assertEquals("http://localhost:9128/edext/publicador", cfg.get(cfg.WS_URL));
        assertFalse(contenido().contains("ws.url"));
        f.setWritable(true);
    }
}
