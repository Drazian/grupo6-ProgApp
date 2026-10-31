package edu.edext.webservices;

import edu.edext.logica.GestorImagenes;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Imagenes por Web Service: el Servidor Web ya no lee ni escribe el disco del Servidor Central.
 */
public class PublicadorImagenesTest {

    private final Publicador publicador = new Publicador();

    @Test
    public void testGuardarYObtenerImagen() throws Exception {
        String id = "ws-" + UUID.randomUUID().toString().substring(0, 8);
        byte[] contenido = {1, 2, 3, 4};

        String nombre = publicador.guardarImagen(contenido, "foto.JPG", id);
        assertEquals(id + ".jpg", nombre);
        assertArrayEquals(contenido, publicador.obtenerImagen(nombre));
        assertArrayEquals(contenido, publicador.obtenerImagen("  " + nombre + "  "));

        // Sin bytes no se guarda nada y se usa la imagen por defecto
        assertEquals("usr.png", publicador.guardarImagen(null, "foto.png", id));
    }

    @Test
    public void testObtenerImagenInexistenteOInvalida() throws Exception {
        assertNull(publicador.obtenerImagen(null));
        assertNull(publicador.obtenerImagen("   "));
        assertNull(publicador.obtenerImagen("no-existe-" + UUID.randomUUID() + ".png"));
        assertNull(publicador.obtenerImagen(File.separator)); // sin nombre de archivo
        assertNull(publicador.obtenerImagen("."));
        assertNull(publicador.obtenerImagen(".."));
        assertNull(publicador.obtenerImagen("sub" + File.separator + ".."));

        // Solo se usa el nombre del archivo: una ruta no permite leer fuera de la carpeta de imagenes
        File fuera = new File("target", "secreto-" + UUID.randomUUID() + ".png");
        Files.write(fuera.toPath(), new byte[]{9});
        assertNull(publicador.obtenerImagen(fuera.getAbsolutePath()));
        assertNull(publicador.obtenerImagen(".." + File.separator + fuera.getName()));

        // Una carpeta con ese nombre no es una imagen
        File carpeta = new File(GestorImagenes.getRutaBase(), "carpeta-" + UUID.randomUUID());
        assertTrue(carpeta.mkdirs());
        assertNull(publicador.obtenerImagen(carpeta.getName()));
    }
}
