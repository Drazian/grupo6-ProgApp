package edu.edext.controlador;
 
import edu.edext.cliente.DtEdicion;
import edu.edext.cliente.DtInscripcionEdicion;
import edu.edext.cliente.DtInstituto;
import edu.edext.cliente.DtPrograma;
import edu.edext.cliente.DtUsuario;
import edu.edext.cliente.TipoUsuario;
import edu.edext.cliente.Publicador;
import edu.edext.web.ClienteCentral;
import edu.edext.web.Datos;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
@MultipartConfig
public class UsuarioServlet extends HttpServlet {

    private void cargarInstitutos(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            Publicador ic = ClienteCentral.get();
            List<DtInstituto> lista = ic.listarInstitutos();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < lista.size(); i++) {
                if (i > 0) json.append(",");
                // Escapar \ y " para que nombres con comillas no rompan el JSON
                json.append("{\"nombre\":\"").append(lista.get(i).getNombre().replace("\\", "\\\\").replace("\"", "\\\"")).append("\"}");
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

        } else if ("cargarSocial".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Debe iniciar sesión para acceder a la red social.</h3>");
                return;
            }

            Publicador ic = ClienteCentral.get();
            DtUsuario logueado = (DtUsuario) session.getAttribute("usuarioLogueado");

            try {
                List<DtUsuario> todosLosUsuarios = ic.listarUsuarios();
                List<String> misSeguidos = ic.listarSeguidos(logueado.getNickname());
                // --- FIX: Solicitar la lista de seguidores a la base de datos ---
                List<String> misSeguidores = ic.listarSeguidores(logueado.getNickname());

                request.setAttribute("todosLosUsuarios", todosLosUsuarios);
                request.setAttribute("misSeguidos", misSeguidos);
                // --- FIX: Enviar la lista de seguidores al JSP ---
                request.setAttribute("misSeguidores", misSeguidores);

                request.getRequestDispatcher("/fragmentos/seguidos.jsp").forward(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al cargar la red social: " + e.getMessage() + "</h3>");
            }

        } else if ("verPerfil".equals(accion)) {
            String nickname = request.getParameter("nickname");

            if (nickname != null && !nickname.trim().isEmpty()) {
                try {
                    Publicador ic = ClienteCentral.get();

                    DtUsuario dtUsuario = ic.obtenerUsuario(nickname.trim());

                    if (dtUsuario != null) {
                        HttpSession session = request.getSession(false);
                        DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;

                        List<String> seguidoresPerfil = ic.listarSeguidores(nickname.trim());
                        List<String> seguidosPerfil = ic.listarSeguidos(nickname.trim());

                        boolean loSigo = false;
                        if (logueado != null) {
                            List<String> misSeguidos = ic.listarSeguidos(logueado.getNickname());
                            loSigo = misSeguidos.contains(nickname.trim());
                        }

                        request.setAttribute("usuarioPerfil", dtUsuario);
                        request.setAttribute("seguidoresPerfil", seguidoresPerfil);
                        request.setAttribute("seguidosPerfil", seguidosPerfil);
                        request.setAttribute("loSigo", loSigo);

                        request.getRequestDispatcher("/fragmentos/detallePerfil.jsp").forward(request, response);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("<p style='color:red;'>Usuario no encontrado.</p>");
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("<p style='color:red;'>Error al cargar perfil: " + e.getMessage() + "</p>");
                }
            }

        } else if ("consultaUsuario".equals(accion)) {
            try {
                Publicador ic = ClienteCentral.get();
                List<DtUsuario> todosLosUsuarios = ic.listarUsuarios();

                request.setAttribute("todosLosUsuarios", todosLosUsuarios);
                request.getRequestDispatcher("/fragmentos/consultaUsuario.jsp").forward(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
            }

        } else if ("detalleConsultaUsuario".equals(accion)) {
            String nickname = request.getParameter("nickname");
            if (nickname != null && !nickname.trim().isEmpty()) {
                try {
                    Publicador ic = ClienteCentral.get();
                    DtUsuario dtUsuario = ic.obtenerUsuario(nickname.trim());

                    if (dtUsuario != null) {
                        HttpSession session = request.getSession(false);
                        DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;

                        boolean esPropioPerfil = (logueado != null && logueado.getNickname().equalsIgnoreCase(dtUsuario.getNickname()));
                        boolean loSigo = false;

                        if (logueado != null) {
                            List<String> misSeguidos = ic.listarSeguidos(logueado.getNickname());
                            loSigo = misSeguidos.contains(nickname.trim());
                        }

                        if (dtUsuario.getTipoUsuario() == TipoUsuario.ESTUDIANTE) {
                            List<DtInscripcionEdicion> inscripcionesEstudiante = ic.getInscripcionesDeEstudiante(dtUsuario.getNickname());
                            request.setAttribute("listaInscripcionesCompletas", inscripcionesEstudiante);
                        } else if (dtUsuario.getTipoUsuario() == TipoUsuario.DOCENTE) {
                            List<DtEdicion> edicionesDocente = ic.listarEdicionesPorUsuario(dtUsuario.getNickname());
                            request.setAttribute("listaEdicionesDocente", edicionesDocente);
                        }

                        List<DtPrograma> programasUsuario = ic.listarProgramasPorUsuario(dtUsuario.getNickname());
                        request.setAttribute("listaProgramas", programasUsuario);

                        // --- FIX DE RED SOCIAL ---
                        // Solicitamos y enviamos las listas de seguidores y seguidos al JSP
                        List<String> listaSeguidores = ic.listarSeguidores(dtUsuario.getNickname());
                        List<String> listaSeguidos = ic.listarSeguidos(dtUsuario.getNickname());
                        
                        request.setAttribute("listaSeguidores", listaSeguidores);
                        request.setAttribute("cantidadSeguidores", listaSeguidores.size());
                        request.setAttribute("listaSeguidos", listaSeguidos);
                        request.setAttribute("cantidadSeguidos", listaSeguidos.size());
                        // -------------------------

                        request.setAttribute("usuarioPerfil", dtUsuario);
                        request.setAttribute("esPropioPerfil", esPropioPerfil);
                        request.setAttribute("loSigo", loSigo);

                        request.getRequestDispatcher("/fragmentos/detalleConsultaUsuario.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al cargar detalle: " + e.getMessage());
                }
            }
        } else if ("getLogueado".equals(accion)) {
            HttpSession session = request.getSession(false);
            DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;

            response.setContentType("text/plain;charset=UTF-8");
            if (logueado != null) {
                response.getWriter().write(logueado.getNickname());
            } else {
                response.getWriter().write("");
            }

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción de usuario no válida.");
        }
    }
     

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        Publicador ic = ClienteCentral.get();

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

                DtUsuario usuario = Datos.usuario(nickname, email, nombre, apellido, null, fecha, institutos, tipo);

                ic.crearUsuarioConImagen(usuario, password, bytesImagen, nombreOriginal);

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
            String[] institutosArray = request.getParameterValues("institutos");

            List<String> institutos = (institutosArray != null && institutosArray.length > 0) 
                ? Arrays.asList(institutosArray) 
                : usuarioActual.getInstitutos();

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

                DtUsuario usuarioModificado = Datos.usuario(usuarioActual.getNickname(), usuarioActual.getEmail(),
                        nombre, apellido, nombreImagenFinal, fecha, institutos, usuarioActual.getTipoUsuario());

                ic.modificarUsuarioConImagen(usuarioModificado, passwordParaControlador, bytesImagen, nombreOriginal);

                actualizarSesionUsuario(request, ic, usuarioActual.getNickname());

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Cambios guardados correctamente.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }

        } else if ("seguir".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Debe iniciar sesión para realizar esta acción.");
                return;
            }

            DtUsuario logueado = (DtUsuario) session.getAttribute("usuarioLogueado");
            String nicknameASeguir = request.getParameter("nicknameASeguir");

            try {
                if (nicknameASeguir == null || nicknameASeguir.trim().isEmpty()) {
                    throw new Exception("Ingrese un nickname válido.");
                }
                if (logueado.getNickname().equalsIgnoreCase(nicknameASeguir.trim())) {
                    throw new Exception("No puede seguirse a sí mismo.");
                }
                if (!ic.existeUsuario(nicknameASeguir.trim())) {
                    throw new Exception("El usuario '" + nicknameASeguir + "' no existe.");
                }

                List<String> seguidos = ic.listarSeguidos(logueado.getNickname());
                if (seguidos.contains(nicknameASeguir.trim())) {
                    throw new Exception("Ya sigue al usuario '" + nicknameASeguir + "'.");
                }

                ic.seguirUsuario(logueado.getNickname(), nicknameASeguir.trim());
                actualizarSesionUsuario(request, ic, logueado.getNickname());

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Ahora sigues a '" + nicknameASeguir.trim() + "'.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }

        } else if ("dejarSeguir".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Debe iniciar sesión para realizar esta acción.");
                return;
            }

            DtUsuario logueado = (DtUsuario) session.getAttribute("usuarioLogueado");
            String nicknameADejar = request.getParameter("nicknameSeleccionado");

            try {
                if (nicknameADejar == null || nicknameADejar.trim().isEmpty()) {
                    throw new Exception("Seleccione un usuario para dejar de seguir.");
                }

                ic.dejarDeSeguirUsuario(logueado.getNickname(), nicknameADejar.trim());
                actualizarSesionUsuario(request, ic, logueado.getNickname());

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Has dejado de seguir a '" + nicknameADejar.trim() + "'.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }

        } else if ("eliminarSeguidor".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Debe iniciar sesión para realizar esta acción.");
                return;
            }

            DtUsuario logueado = (DtUsuario) session.getAttribute("usuarioLogueado");
            String seguidorAEliminar = request.getParameter("seguidorSeleccionado");

            try {
                if (seguidorAEliminar == null || seguidorAEliminar.trim().isEmpty()) {
                    throw new Exception("Seleccione un seguidor para eliminar.");
                }

                ic.dejarDeSeguirUsuario(seguidorAEliminar.trim(), logueado.getNickname());
                actualizarSesionUsuario(request, ic, logueado.getNickname());

                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Se ha eliminado a '" + seguidorAEliminar.trim() + "' de tus seguidores.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }
        }
    }
    
    private void actualizarListasSociales(HttpServletRequest request, Publicador ic) {
        DtUsuario logueado = (DtUsuario) request.getSession().getAttribute("usuarioLogueado");
        if (logueado != null) {
            try {
                request.setAttribute("listaSeguidos", ic.listarSeguidos(logueado.getNickname()));
                request.setAttribute("listaSeguidores", ic.listarSeguidores(logueado.getNickname()));
            } catch (Exception e) {
                request.setAttribute("listaSeguidos", new ArrayList<String>());
                request.setAttribute("listaSeguidores", new ArrayList<String>());
            }
        }
    }

    private void actualizarSesionUsuario(HttpServletRequest request, Publicador ic, String nickname) throws Exception {
        // Por el Web Service el usuario nunca trae la contraseña
        DtUsuario u = ic.obtenerUsuario(nickname);
        if (u != null) {
            request.getSession().setAttribute("usuarioLogueado", u);
        }
    }
}