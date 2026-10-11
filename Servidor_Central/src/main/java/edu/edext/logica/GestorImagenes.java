package edu.edext.logica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GestorImagenes {

    // Ruta absoluta en el disco del sistema (ej: C:\Users\TuUsuario\edext_imagenes\ en Windows)
    private static final String RUTA_BASE = System.getProperty("user.home") 
            + File.separator + "edext_imagenes" + File.separator;

    public static String getRutaBase() {
        File dir = new File(RUTA_BASE);
        if (!dir.exists()) {
            dir.mkdirs(); // Crea la carpeta automáticamente si no existe
        }
        return RUTA_BASE;
    }

    // Método invocado tanto por Servlets como por Swing
    public static String guardarImagen(byte[] bytesImagen, String nombreOriginal, String nickname) throws IOException {
        if (bytesImagen == null || bytesImagen.length == 0 || nombreOriginal == null) {
            return "usr.png";
        }

        String extension = "";
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto > 0) {
            extension = nombreOriginal.substring(punto).toLowerCase();
        } else {
            extension = ".png";
        }

        String nombreArchivoFinal = nickname + extension;
        File destino = new File(getRutaBase() + nombreArchivoFinal);

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            fos.write(bytesImagen);
        }

        return nombreArchivoFinal;
    }

    public static String guardarImagen(File archivoOrigen, String nickname) throws IOException {
        if (archivoOrigen == null || !archivoOrigen.exists()) {
            return "usr.png";
        }

        byte[] bytes = java.nio.file.Files.readAllBytes(archivoOrigen.toPath());
        return guardarImagen(bytes, archivoOrigen.getName(), nickname);
    }
}