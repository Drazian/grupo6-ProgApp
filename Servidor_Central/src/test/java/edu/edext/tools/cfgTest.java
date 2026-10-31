package edu.edext.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de cfg. Corre en su propia JVM (surefire con reuseForks=false), asi que antes
 * de que se cargue la clase cfg se apuntan user.home y edext.config a carpetas dentro
 * de target/cfg-test: se prueba la creacion del servidor.properties, la migracion de la
 * ruta de imagenes desde el edEXT.cfg anterior y los errores de lectura/escritura sin
 * tocar el ~/.edEXT real.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class cfgTest {

    private static String homeOriginal, configOriginal;
    private static File home, conf, props;

    /** Carpeta target del modulo (surefire pone user.home en target/home-test). */
    static File target() {
        File h = new File(System.getProperty("user.home"));
        if ("home-test".equals(h.getName()) && h.getParentFile() != null) return h.getParentFile();
        return new File("target").getAbsoluteFile();
    }

    static void borrar(File f) throws IOException {
        if (!f.exists()) return;
        try (Stream<Path> s = Files.walk(f.toPath())) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    @BeforeAll
    public static void prepararRutas() throws IOException {
        File base = new File(target(), "cfg-test");
        borrar(base);
        home = new File(base, "home");
        conf = new File(base, "conf");
        props = new File(conf, "servidor.properties");
        homeOriginal = System.getProperty("user.home");
        configOriginal = System.getProperty("edext.config");
        System.setProperty("user.home", home.getAbsolutePath());
        System.setProperty("edext.config", props.getAbsolutePath());
    }

    @AfterAll
    public static void restaurar() {
        System.setProperty("user.home", homeOriginal);
        if (configOriginal != null) System.setProperty("edext.config", configOriginal);
        else System.clearProperty("edext.config");
    }

    /** Descarta las propiedades cacheadas para que el proximo acceso vuelva a leer el archivo. */
    @BeforeEach
    public void reiniciarCache() throws Exception {
        Field f = cfg.class.getDeclaredField("propiedades");
        f.setAccessible(true);
        f.set(null, null);
    }

    private static String rutaHome(String resto) {
        return home.getAbsolutePath() + resto;
    }

    @Test
    @Order(1)
    public void testRutas() {
        assertEquals(rutaHome(File.separator + ".edEXT"), cfg.PATH);
        assertEquals(props.getAbsolutePath(), cfg.PATH_PROPERTIES);
        assertEquals(cfg.PATH + File.separator + "edEXT.cfg", cfg.PATH_FILE);
        assertEquals("/Images/", cfg.PATH_INTERNAL);
        assertEquals("usr.png", cfg.IMAGE_USER);
        assertNotNull(new cfg());
    }

    @Test
    @Order(2)
    public void testSinPermisoParaCrearElArchivoUsaValoresPorDefecto() {
        // La carpeta del servidor.properties no existe: no se puede crear ni leer
        assertFalse(conf.exists());
        Properties p = cfg.propiedades();

        assertFalse(props.exists());
        assertTrue(new File(cfg.PATH).isDirectory(), "Igual crea la carpeta ~/.edEXT");
        assertEquals("jdbc:mariadb://localhost:3306/edext?createDatabaseIfNotExist=true", p.getProperty(cfg.DB_URL));
        assertEquals("org.mariadb.jdbc.Driver", cfg.get(cfg.DB_DRIVER));
        assertEquals("pipo", cfg.get(cfg.DB_USUARIO));
        assertEquals("pipo", cfg.get(cfg.DB_PASSWORD));
        assertEquals("update", cfg.get(cfg.DB_ESQUEMA));
        // Sin edEXT.cfg anterior la carpeta de imagenes es ~/.edEXT/imagen
        assertEquals("~/.edEXT/imagen", cfg.get(cfg.IMAGEN_RUTA));
        assertEquals(rutaHome("/.edEXT/imagen"), cfg.pathImagen());
    }

    @Test
    @Order(3)
    public void testCreaElArchivoMigrandoLaRutaDelCfgAnterior() throws IOException {
        assertTrue(conf.mkdirs());
        Files.createDirectories(new File(cfg.PATH).toPath());
        Files.writeString(Path.of(cfg.PATH_FILE),
                "# configuracion vieja\notra : valor\nimagen : C:\\fotos\\edext\n", StandardCharsets.UTF_8);

        cfg.init();

        assertTrue(props.exists());
        String contenido = Files.readString(props.toPath(), StandardCharsets.UTF_8);
        assertTrue(contenido.contains("db.url=jdbc:mariadb://localhost:3306/edext?createDatabaseIfNotExist=true"));
        assertTrue(contenido.contains("db.esquema=update"));
        assertTrue(contenido.contains("imagen.ruta=C:/fotos/edext"), "Las barras se normalizan a /");
        assertEquals("C:/fotos/edext", cfg.get(cfg.IMAGEN_RUTA));
        assertEquals("C:/fotos/edext", cfg.pathImagen());
    }

    @Test
    @Order(4)
    public void testLeeElArchivoExistente() throws IOException {
        Files.writeString(props.toPath(),
                "db.url=jdbc:h2:mem:cfgtest\ndb.usuario=sa\ndb.password=\nimagen.ruta=~/imgs\n", StandardCharsets.UTF_8);

        Properties p = cfg.propiedades();
        assertSame(p, cfg.propiedades(), "Las propiedades se leen una sola vez");
        assertEquals("jdbc:h2:mem:cfgtest", cfg.get(cfg.DB_URL));
        // Lo que no esta en el archivo sale de los valores por defecto
        assertEquals("org.mariadb.jdbc.Driver", cfg.get(cfg.DB_DRIVER));
        assertEquals(rutaHome("/imgs"), cfg.pathImagen());
        assertEquals(rutaHome("/imgs") + File.separator, cfg.fullpathImagen());

        Map<String, Object> jpa = cfg.propiedadesJPA();
        assertEquals(5, jpa.size());
        assertEquals("jdbc:h2:mem:cfgtest", jpa.get("jakarta.persistence.jdbc.url"));
        assertEquals("org.mariadb.jdbc.Driver", jpa.get("jakarta.persistence.jdbc.driver"));
        assertEquals("sa", jpa.get("jakarta.persistence.jdbc.user"));
        assertEquals("", jpa.get("jakarta.persistence.jdbc.password"));
        assertEquals("update", jpa.get("jakarta.persistence.schema-generation.database.action"));
    }
}
