package edu.edext.logica;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GestorImagenes {
    
    private static final Path CARPETA_IMAGENES = Paths.get("imagenes");
    
    public static void prepararCarpeta() throws Exception {
        Files.createDirectories(CARPETA_IMAGENES);
    }

    // Método 1: Firma principal basada en bytes (para Web y arquitectura distribuida)
    public static String guardarImagen(byte[] bytesImagen, String nombreOriginal, String nickname) throws Exception {
        if (bytesImagen == null || bytesImagen.length == 0) {
            return null;
        }

        prepararCarpeta();

        String extension = "";
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto > 0) {
            extension = nombreOriginal.substring(punto).toLowerCase();
        }

        if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
            throw new Exception("El archivo seleccionado no es una imagen válida (.png o .jpg).");
        }

        String nombreArchivo = nickname + extension;
        Path destino = CARPETA_IMAGENES.resolve(nombreArchivo);

        Files.write(destino, bytesImagen);

        return nombreArchivo;
    }

    // Método 2: Sobrecarga para compatibilidad con Swing (AltaUsuario.java en Servidor Central)
    public static String guardarImagen(File imagenOriginal, String nickname) throws Exception {
        if (imagenOriginal == null || !imagenOriginal.exists()) {
            return null;
        }

        byte[] bytes = Files.readAllBytes(imagenOriginal.toPath());
        return guardarImagen(bytes, imagenOriginal.getName(), nickname);
    }
}