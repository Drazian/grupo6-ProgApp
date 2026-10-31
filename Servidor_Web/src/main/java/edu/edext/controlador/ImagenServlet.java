package edu.edext.controlador;

import edu.edext.web.ClienteCentral;
import org.tinylog.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream; 

@WebServlet(name = "ImagenServlet", urlPatterns = {"/ImagenServlet"})
public class ImagenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreImagen = request.getParameter("nombre");

        if (nombreImagen == null || nombreImagen.trim().isEmpty()) {
            nombreImagen = "usr.png";
        } else {
            nombreImagen = new File(nombreImagen.trim()).getName();
            if (nombreImagen.isEmpty() || "..".equals(nombreImagen) || ".".equals(nombreImagen)) {
                nombreImagen = "usr.png";
            }
        }

        // Las imagenes estan en el Servidor Central: se piden por Web Service
        byte[] desdeCentral = null;
        try {
            desdeCentral = ClienteCentral.get().obtenerImagen(nombreImagen);
        } catch (Exception e) {
            Logger.error("No se pudo obtener la imagen '{}' del Servidor Central: {}", nombreImagen, e.getMessage());
        }
        if (desdeCentral != null) {
            String tipo = getServletContext().getMimeType(nombreImagen);
            response.setContentType(tipo != null ? tipo : "image/png");
            response.setContentLength(desdeCentral.length);
            try (OutputStream out = response.getOutputStream()) {
                out.write(desdeCentral);
            }
            return;
        }

        // Si el Central no la tiene, se usa la del war (o la imagen por defecto)
        File archivo = new File("");

        if (!archivo.exists() || !archivo.isFile()) {
            String pathReal = getServletContext().getRealPath("/imagenes/" + nombreImagen);
            if (pathReal != null) {
                archivo = new File(pathReal);
            }
        }

        if (!archivo.exists() || !archivo.isFile()) {
            String pathDefault = getServletContext().getRealPath("/imagenes/usr.png");
            if (pathDefault != null) {
                archivo = new File(pathDefault);
            }
        }

        if (!archivo.exists() || !archivo.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(archivo.getName());
        if (contentType == null) {
            contentType = "image/png";
        }
        response.setContentType(contentType);
        response.setContentLength((int) archivo.length());

        try (FileInputStream in = new FileInputStream(archivo);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}