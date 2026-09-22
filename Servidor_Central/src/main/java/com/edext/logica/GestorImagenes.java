
package com.edext.logica;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
/**
 *
 * @author Diego
 */
public class GestorImagenes {
    
    private static final Path CARPETA_IMAGENES = Paths.get("imagenes");
    
    //si no existe crea la carpeta imagenes
    public static void prepararCarpeta() throws Exception {
        Files.createDirectories(CARPETA_IMAGENES);
    }
    //Guardar la imagen del usuario en el sistema
    public static String guardarImagen(File imagenOriginal, String nickname) throws Exception {

        prepararCarpeta();
        //obtener nombre de la imagen guardada
        String nombreOriginal = imagenOriginal.getName();
        String extension = "";

        int punto = nombreOriginal.lastIndexOf('.');
        //extraer extensión del archivo
        if (punto > 0) {
            extension = nombreOriginal.substring(punto).toLowerCase();
        }

        if (!extension.equals(".png") && !extension.equals(".jpg")) {
            throw new Exception("El archivo seleccionado no es una imagen válida.");
        }

        String nombreArchivo = nickname + extension;
        //crear ruta de destino del archivo
        Path destino = CARPETA_IMAGENES.resolve(nombreArchivo);
        //copia la imagen el en destino si existe la remplaza
        Files.copy(
            imagenOriginal.toPath(),
            destino,
            StandardCopyOption.REPLACE_EXISTING
        );

        return nombreArchivo;
    }
   
}
