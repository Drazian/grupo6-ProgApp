package edu.edext.controlador;

import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
@MultipartConfig
public class UsuarioServlet extends HttpServlet {

    private void cargarInstitutos(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            IControlador ic = Fabrica.getInstance().getIControlador();
            List<DtInstituto> lista = ic.listarInstitutos();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < lista.size(); i++) {
                if (i > 0) json.append(",");
                json.append("{\"nombre\":\"").append(lista.get(i).getNombre()).append("\"}");
            }
            json.append("]");
            response.getWriter().write(json.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if ("listarInstitutos".equals(accion)) {
            cargarInstitutos(response);
        } else if ("formAlta".equals(accion)) {
            request.getRequestDispatcher("/fragmentos/altaUsuario.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción de usuario no válida.");
        }
    } 

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

            List<String> institutos = (institutosArray != null) ? Arrays.asList(institutosArray) : Collections.emptyList();

            Part partImagen = request.getPart("imagen");
            byte[] bytesImagen = null;
            String nombreOriginal = null;

            if (partImagen != null && partImagen.getSize() > 0) {
                nombreOriginal = partImagen.getSubmittedFileName();
                try (InputStream is = partImagen.getInputStream()) {
                    bytesImagen = is.readAllBytes();
                }
            }

            try {
                Date fecha = Date.valueOf(fechaNacimiento);
                TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);

                DtUsuario usuario = new DtUsuario(
                    nickname, password, email, nombre, apellido,
                    null, fecha, institutos, tipo
                );

                IControlador ic = Fabrica.getInstance().getIControlador();
                ic.crearUsuario(usuario, bytesImagen, nombreOriginal);

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Usuario creado correctamente.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }

        } else if ("modificarUsuario".equals(accion)) {
            
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            DtUsuario usuarioActual = (DtUsuario) session.getAttribute("usuarioLogueado");

            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String fechaNacimiento = request.getParameter("fechaNacimiento");
            String nuevaPassword = request.getParameter("password");

            Part partImagen = request.getPart("imagen");
            byte[] bytesImagen = null;
            String nombreOriginal = null;

            if (partImagen != null && partImagen.getSize() > 0) {
                nombreOriginal = partImagen.getSubmittedFileName();
                try (InputStream is = partImagen.getInputStream()) {
                    bytesImagen = is.readAllBytes();
                }
            }

            try {
                Date fecha = Date.valueOf(fechaNacimiento);

                // Si escribió algo en el campo, se envía la clave en texto plano; si no, va null
                String passwordParaControlador = (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) 
                    ? nuevaPassword.trim() 
                    : null;

                String nombreImagenFinal = usuarioActual.getImagen();
                if (bytesImagen != null && bytesImagen.length > 0 && nombreOriginal != null) {
                    String extension = "";
                    int punto = nombreOriginal.lastIndexOf('.');
                    if (punto > 0) {
                        extension = nombreOriginal.substring(punto).toLowerCase();
                    }
                    nombreImagenFinal = usuarioActual.getNickname() + extension;
                }

                DtUsuario usuarioModificado = new DtUsuario(
                    usuarioActual.getNickname(),
                    passwordParaControlador,
                    usuarioActual.getEmail(),
                    nombre,
                    apellido,
                    nombreImagenFinal, 
                    fecha,
                    usuarioActual.getInstitutos(),
                    usuarioActual.getTipoUsuario()
                );

                IControlador ic = Fabrica.getInstance().getIControlador();
                ic.modificarUsuario(usuarioModificado, bytesImagen, nombreOriginal);

                // Sincronizamos la sesión trayendo el DtUsuario actualizado con su hash real desde la BD
                List<DtUsuario> listaActualizada = ic.listarUsuarios();
                for (DtUsuario u : listaActualizada) {
                    if (u.getNickname().equalsIgnoreCase(usuarioActual.getNickname())) {
                        session.setAttribute("usuarioLogueado", u);
                        break;
                    }
                }

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Cambios guardados correctamente.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }
        }
    }
}