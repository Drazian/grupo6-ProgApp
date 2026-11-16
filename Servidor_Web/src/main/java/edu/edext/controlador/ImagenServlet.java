package edu.edext.controlador;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

@WebServlet(name = "ImagenServlet", urlPatterns = {"/ImagenServlet"})
public class ImagenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreImagen = request.getParameter("nombre");
        if (nombreImagen == null || nombreImagen.trim().isEmpty() || "null".equalsIgnoreCase(nombreImagen.trim())) {
            nombreImagen = "usr.png"; }
        else {
            nombreImagen = new File(nombreImagen.trim()).getName();
            if (nombreImagen.isEmpty() || "..".equals(nombreImagen) || ".".equals(nombreImagen)) {
                nombreImagen = "usr.png"; } }
        byte[] imagenBytes = null;

        if (!"usr.png".equals(nombreImagen)) {
            try {
                imagenBytes = Fabrica.getConexion().getImageBytes(nombreImagen);
            } catch (Exception e) {
                Logger.error(e, "Error al obtener la imagen desde el servidor central: " + nombreImagen);
                imagenBytes = null;
            }
        }
        if (imagenBytes != null && imagenBytes.length > 0) {
            String contentType = getServletContext().getMimeType(nombreImagen);
            if (contentType == null) {
                contentType = "image/png";
            }
            response.setContentType(contentType);
            response.setContentLength(imagenBytes.length);
            try (OutputStream out = response.getOutputStream()) {
                out.write(imagenBytes);
            }
            return; }
        File archivoLocal = null;
        String pathReal = getServletContext().getRealPath("/imagenes/" + nombreImagen);
        if (pathReal != null) {
            archivoLocal = new File(pathReal);
        }
        if (archivoLocal == null || !archivoLocal.exists() || !archivoLocal.isFile()) {
            String pathDefault = getServletContext().getRealPath("/imagenes/usr.png");
            if (pathDefault != null) {
                archivoLocal = new File(pathDefault); } }
        if (archivoLocal == null || !archivoLocal.exists() || !archivoLocal.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return; }
        String contentType = getServletContext().getMimeType(archivoLocal.getName());
        if (contentType == null) {
            contentType = "image/png"; }
        response.setContentType(contentType);
        response.setContentLength((int) archivoLocal.length());
        try (FileInputStream in = new FileInputStream(archivoLocal);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead); } }
    }
}