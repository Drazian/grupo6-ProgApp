package edu.edext.controlador;
 
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
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

        } else if ("cargarSocial".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Debe iniciar sesión para acceder a la red social.</h3>");
                return;
            }

            IControlador ic = Fabrica.getInstance().getIControlador();
            DtUsuario logueado = (DtUsuario) session.getAttribute("usuarioLogueado");

            try {
                List<DtUsuario> todosLosUsuarios = ic.listarUsuarios();
                List<String> misSeguidos = ic.listarSeguidos(logueado.getNickname());

                request.setAttribute("todosLosUsuarios", todosLosUsuarios);
                request.setAttribute("misSeguidos", misSeguidos);

                request.getRequestDispatcher("/fragmentos/seguidos.jsp").forward(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al cargar la red social: " + e.getMessage() + "</h3>");
            }

        } else if ("verPerfil".equals(accion)) {
            String nickname = request.getParameter("nickname");

            if (nickname != null && !nickname.trim().isEmpty()) {
                try {
                    IControlador ic = Fabrica.getInstance().getIControlador();

                    DtUsuario dtUsuario = null;
                    for (DtUsuario u : ic.listarUsuarios()) {
                        if (u.getNickname().equalsIgnoreCase(nickname.trim())) {
                            dtUsuario = u;
                            break;
                        }
                    }

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
                IControlador ic = Fabrica.getInstance().getIControlador();
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
                    IControlador ic = Fabrica.getInstance().getIControlador();
                    DtUsuario dtUsuario = null;

                    for (DtUsuario u : ic.listarUsuarios()) {
                        if (u.getNickname().equalsIgnoreCase(nickname.trim())) {
                            dtUsuario = u;
                            break;
                        }
                    }

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
        IControlador ic = Fabrica.getInstance().getIControlador();

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

                DtUsuario usuarioModificado = new DtUsuario(
                    usuarioActual.getNickname(),
                    passwordParaControlador,
                    usuarioActual.getEmail(),
                    nombre,
                    apellido,
                    nombreImagenFinal, 
                    fecha,
                    institutos,
                    usuarioActual.getTipoUsuario()
                );

                ic.modificarUsuario(usuarioModificado, bytesImagen, nombreOriginal);

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
    
    private void actualizarListasSociales(HttpServletRequest request, IControlador ic) {
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

    private void actualizarSesionUsuario(HttpServletRequest request, IControlador ic, String nickname) throws Exception {
        List<DtUsuario> usuarios = ic.listarUsuarios();
        for (DtUsuario u : usuarios) {
            if (u.getNickname().equalsIgnoreCase(nickname)) {
                request.getSession().setAttribute("usuarioLogueado", u);
                break;
            }
        }
    }
}