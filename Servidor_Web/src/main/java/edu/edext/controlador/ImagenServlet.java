package edu.edext.controlador;

import edu.edext.logica.GestorImagenes;
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
        }


        File archivo = new File(GestorImagenes.getRutaBase(), nombreImagen);

        if (!archivo.exists()) {
            String pathReal = getServletContext().getRealPath("/imagenes/" + nombreImagen);
            if (pathReal != null) {
                archivo = new File(pathReal);
            }
        }

        if (!archivo.exists()) {
            String pathDefault = getServletContext().getRealPath("/imagenes/usr.png");
            if (pathDefault != null) {
                archivo = new File(pathDefault);
            }
        }

        if (!archivo.exists()) {
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