package edu.edext.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(name = "ImagenServlet", urlPatterns = {"/imagenes/*"})
public class ImagenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el nombre del archivo solicitado (ejemplo: "/usr.png" o "/diego.jpg")
        String nombreImagen = request.getPathInfo();

        if (nombreImagen == null || nombreImagen.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Quitar la barra inicial '/'
        nombreImagen = nombreImagen.substring(1);

        // Intentar buscar la imagen en la carpeta física 'imagenes' del servidor
        Path rutaImagen = Paths.get("imagenes", nombreImagen);
        File archivoImagen = rutaImagen.toFile();

        // Si la foto personalizada del usuario no existe en disco, servir la imagen por defecto
        if (!archivoImagen.exists() || archivoImagen.isDirectory()) {
            // Intentar buscar la foto por defecto en el WAR o en disco
            Path rutaDefault = Paths.get("imagenes", "usr.png");
            if (rutaDefault.toFile().exists()) {
                archivoImagen = rutaDefault.toFile();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // Detectar tipo MIME (image/png, image/jpeg, etc.)
        String contentType = getServletContext().getMimeType(archivoImagen.getName());
        if (contentType == null) {
            contentType = "image/png";
        }

        response.setContentType(contentType);
        response.setContentLength((int) archivoImagen.length());

        // Transmitir la imagen al cliente
        try (FileInputStream in = new FileInputStream(archivoImagen);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}