/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.edext.controlador;

import edu.edext.datatypes.DtInstituto;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import edu.edext.logica.GestorImagenes;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author usuario
 */
@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
@MultipartConfig // Permite recibir archivos 
public class UsuarioServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UsuarioServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UsuarioServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
        private void cargarInstitutos(HttpServletResponse response)
        throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            IControlador ic = Fabrica.getInstance().getIControlador();

            List<DtInstituto> lista = ic.listarInstitutos();

            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < lista.size(); i++) {
                DtInstituto instituto = lista.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{");
                json.append("\"nombre\":\"")
                    .append(instituto.getNombre())
                    .append("\"");
                json.append("}");
            }

            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                "{\"error\":\"" + e.getMessage() + "\"}"
            );
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if ("listarInstitutos".equals(accion)) {
            cargarInstitutos(response);
        } else {
            processRequest(request, response);
        }
    } 
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {

            String nickname = request.getParameter("nickname");
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String fechaNacimiento = request.getParameter("fechaNacimiento");
            String tipoUsuario = request.getParameter("tipoUsuario");
            
            String[] institutosArray = request.getParameterValues("institutos");

            List<String> institutos;

            if (institutosArray != null) {
                institutos = Arrays.asList(institutosArray);
            } else {
                institutos = Collections.emptyList();
            }
            
            try {
                IControlador ic = Fabrica.getInstance().getIControlador();

                if (ic.existeUsuario(nickname)) {
                    throw new ServletException(
                        "Ya existe un usuario con el nickname '" + nickname + "'."
                    );
                }

                if (ic.existeEmail(email)) {
                    throw new ServletException(
                        "Ya existe un usuario con el correo '" + email + "'."
                    );
                }
                
            } catch (ServletException e) {
                throw e;
            } catch (Exception e) {
                throw new ServletException(
                   "No se pudieron validar los datos del usuario.", e
                );
            }
            Part imagen = request.getPart("imagen");
            
           
            File archivoTemporal = null;

            if (imagen != null && imagen.getSize() > 0) {
                String nombreOriginal = imagen.getSubmittedFileName();

                String extension = "";
                int punto = nombreOriginal.lastIndexOf('.');

                if (punto > 0) {
                    extension = nombreOriginal.substring(punto).toLowerCase();
                }

                if (!extension.equals(".jpg") && !extension.equals(".png")) {
                    throw new ServletException("La imagen debe ser JPG o PNG.");
                }

                Path rutaTemporal = Files.createTempFile("imagen-", extension);

                try (InputStream entrada = imagen.getInputStream()) {
                    Files.copy(
                        entrada,
                        rutaTemporal,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                }

                archivoTemporal = rutaTemporal.toFile();
                
            }

           
            
            try {
                
                Date fecha = Date.valueOf(fechaNacimiento);

                TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);

                
                String nombreImagen = null;

            
                DtUsuario usuario = new DtUsuario(
                    nickname,
                    password,
                    email,
                    nombre,
                    apellido,
                    nombreImagen,
                    fecha,
                    institutos,
                    tipo
                );

                // Enviar los datos y el archivo temporal al Servidor Central.
                IControlador ic = Fabrica.getInstance().getIControlador();

                ic.crearUsuario(usuario, archivoTemporal);

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Usuario creado correctamente.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
