package edu.edext.logica;

import edu.edext.tools.cfg;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Guardado de imagenes. La carpeta de imagenes de los tests queda dentro de target/
 * (user.home de los tests apunta a target/home-test), nunca en el ~/.edEXT real.
 */
public class GestorImagenesTest {

    private static final byte[] BYTES = {1, 2, 3, 4};

    private static String nick() {
        return "img-" + UUID.randomUUID();
    }

    @Test
    public void testCarpetaSeCreaSiNoExiste() throws Exception {
        File carpeta = new File(cfg.fullpathImagen()).getCanonicalFile();
        assertTrue(carpeta.getPath().contains(File.separator + "target" + File.separator),
                "La carpeta de imagenes de los tests debe estar dentro de target/: " + carpeta);
        // Si ya existe se aparta temporalmente (sin borrar nada) para probar que se crea
        File apartada = new File(carpeta.getParentFile(), carpeta.getName() + ".apartada-" + UUID.randomUUID());
        boolean existia = carpeta.exists();
        if (existia) assertTrue(carpeta.renameTo(apartada));
        try {
            assertFalse(carpeta.exists());
            String ruta = GestorImagenes.getRutaBase();
            assertEquals(cfg.fullpathImagen(), ruta);
            assertTrue(carpeta.isDirectory(), "getRutaBase crea la carpeta");
            assertEquals(ruta, GestorImagenes.getRutaBase(), "Si ya existe no hace nada");
        } finally {
            if (existia) {
                assertTrue(carpeta.delete(), "La carpeta creada por el test esta vacia");
                assertTrue(apartada.renameTo(carpeta));
            }
        }
    }

    @Test
    public void testConstructorImplicito() {
        // Clase utilitaria (solo metodos estaticos) con el constructor por defecto publico
        assertNotNull(new GestorImagenes());
    }

    @Test
    public void testSinDatosDevuelveImagenPorDefecto() throws Exception {
        assertEquals("usr.png", GestorImagenes.guardarImagen(null, "a.png", "n"));
        assertEquals("usr.png", GestorImagenes.guardarImagen(new byte[0], "a.png", "n"));
        assertEquals("usr.png", GestorImagenes.guardarImagen(BYTES, null, "n"));
        assertEquals("usr.png", GestorImagenes.guardarImagen((File) null, "n"));
        assertEquals("usr.png", GestorImagenes.guardarImagen(new File("target/no-existe-" + UUID.randomUUID() + ".png"), "n"));
    }

    @Test
    public void testGuardarConExtension() throws Exception {
        String n = nick();
        assertEquals(n + ".jpg", GestorImagenes.guardarImagen(BYTES, "Foto.JPG", n));
        assertArrayEquals(BYTES, Files.readAllBytes(Path.of(GestorImagenes.getRutaBase() + n + ".jpg")));
    }

    @Test
    public void testGuardarSinExtensionUsaPng() throws Exception {
        String n1 = nick(), n2 = nick();
        assertEquals(n1 + ".png", GestorImagenes.guardarImagen(BYTES, "sinextension", n1));
        assertEquals(n2 + ".png", GestorImagenes.guardarImagen(BYTES, ".oculto", n2));
        assertTrue(new File(GestorImagenes.getRutaBase() + n1 + ".png").isFile());
    }

    @Test
    public void testGuardarDesdeArchivo() throws Exception {
        File origen = File.createTempFile("origen", ".gif", new File("target"));
        origen.deleteOnExit();
        Files.write(origen.toPath(), BYTES);
        String n = nick();
        assertEquals(n + ".gif", GestorImagenes.guardarImagen(origen, n));
        assertArrayEquals(BYTES, Files.readAllBytes(Path.of(GestorImagenes.getRutaBase() + n + ".gif")));
    }
}
