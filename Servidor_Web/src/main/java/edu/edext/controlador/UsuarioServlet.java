package edu.edext.controlador;
 
import edu.edext.tools.Utilsw;
import java.util.Collections;
import edu.edext.modelo.Remux;
import edu.edext.controlador.ws.DtEdicion;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.DtPrograma;
import edu.edext.controlador.ws.TipoUsuario;
import edu.edext.controlador.ws.DtInstituto;
import edu.edext.controlador.ws.DtInscripcionEdicion;
import edu.edext.controlador.ws.Exception_Exception;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
@MultipartConfig
public class UsuarioServlet extends HttpServlet {
    
    private void cargarInstitutos(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            List<DtInstituto> lista = Fabrica.getConexion().listarInstitutos();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("listarInstitutos".equals(accion)) {
            cargarInstitutos(response);

        } else if ("formAlta".equals(accion)) {
            request.getRequestDispatcher("/WEB-INF/fragmentos/altaUsuario.jsp").forward(request, response);

        } else if ("cargarSocial".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Debe iniciar sesión para acceder a la red social.</h3>");
                return;
            }
            DtUsuario logueado = (DtUsuario) session.getAttribute("usuarioLogueado");
            try {
                List<DtUsuario> todosLosUsuarios = Fabrica.getConexion().listarUsuarios();
                List<String> misSeguidos = Fabrica.getConexion().listarSeguidos(logueado.getNickname());
                List<String> misSeguidores = Fabrica.getConexion().listarSeguidores(logueado.getNickname());
                request.setAttribute("todosLosUsuarios", todosLosUsuarios);
                request.setAttribute("misSeguidos", misSeguidos);
                request.setAttribute("misSeguidores", misSeguidores);
                request.getRequestDispatcher("/WEB-INF/fragmentos/seguidos.jsp").forward(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al cargar la red social: " + e.getMessage() + "</h3>");
            }

        } else if ("existeNickname".equals(accion)) {
            String nickname = request.getParameter("nickname");
            try{
                boolean existe = Fabrica.getConexion().existeUsuario(nickname);
                response.setContentType("text/plain");
                response.getWriter().write(String.valueOf(existe));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al comprobar nickname: " + e.getMessage() + "</h3>");
            }

        } else if ("existeCorreo".equals(accion)) {
            String correo = request.getParameter("correo");
            try{
                boolean existe = Fabrica.getConexion().existeEmail(correo);
                response.setContentType("text/plain");
                response.getWriter().write(String.valueOf(existe));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al comprobar email: " + e.getMessage() + "</h3>");
            }

        } else if ("verPerfil".equals(accion)) {
            String nickname = request.getParameter("nickname");
            if (nickname != null && !nickname.trim().isEmpty()) {
                try {
                    DtUsuario dtUsuario = Fabrica.getConexion().obtenerUsuario(nickname.trim());
                    if (dtUsuario != null) {
                        HttpSession session = request.getSession(false);
                        DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
                        List<String> seguidoresPerfil = Fabrica.getConexion().listarSeguidores(nickname.trim());
                        List<String> seguidosPerfil = Fabrica.getConexion().listarSeguidos(nickname.trim());
                        boolean loSigo = false;
                        if (logueado != null) {
                            List<String> misSeguidos = Fabrica.getConexion().listarSeguidos(logueado.getNickname());
                            loSigo = misSeguidos.contains(nickname.trim());
                        }
                        request.setAttribute("usuarioPerfil", dtUsuario);
                        request.setAttribute("seguidoresPerfil", seguidoresPerfil);
                        request.setAttribute("seguidosPerfil", seguidosPerfil);
                        request.setAttribute("loSigo", loSigo);
                        request.getRequestDispatcher("/WEB-INF/fragmentos/detallePerfil.jsp").forward(request, response);
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
                List<DtUsuario> todosLosUsuarios = Fabrica.getConexion().listarUsuarios();

                request.setAttribute("todosLosUsuarios", todosLosUsuarios);
                request.getRequestDispatcher("/WEB-INF/fragmentos/consultaUsuario.jsp").forward(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
            }

        } else if ("detalleConsultaUsuario".equals(accion)) {
            String nickname = request.getParameter("nickname");
            if (nickname != null && !nickname.trim().isEmpty()) {
                try {
                    DtUsuario dtUsuario = Fabrica.getConexion().obtenerUsuario(nickname.trim());
                    if (dtUsuario != null) {
                        HttpSession session = request.getSession(false);
                        DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
                        boolean esPropioPerfil = (logueado != null && logueado.getNickname().equalsIgnoreCase(dtUsuario.getNickname()));
                        boolean loSigo = false;
                        if (logueado != null) {
                            List<String> misSeguidos = Fabrica.getConexion().listarSeguidos(logueado.getNickname());
                            loSigo = misSeguidos.contains(nickname.trim());
                        }
                        if (dtUsuario.getTipoUsuario() == TipoUsuario.ESTUDIANTE) {
                            List<DtInscripcionEdicion> inscripcionesEstudiante = Fabrica.getConexion().getInscripcionesDeEstudiante(dtUsuario.getNickname());
                            request.setAttribute("listaInscripcionesCompletas", inscripcionesEstudiante);
                        } else if (dtUsuario.getTipoUsuario() == TipoUsuario.DOCENTE) {
                            List<DtEdicion> edicionesDocente = Fabrica.getConexion().listarEdicionesPorUsuario(dtUsuario.getNickname());
                            request.setAttribute("listaEdicionesDocente", edicionesDocente);
                        }
                        List<DtPrograma> programasUsuario = Fabrica.getConexion().listarProgramasPorUsuario(dtUsuario.getNickname());
                        request.setAttribute("listaProgramas", programasUsuario);
                        List<String> listaSeguidores = Fabrica.getConexion().listarSeguidores(dtUsuario.getNickname());
                        List<String> listaSeguidos = Fabrica.getConexion().listarSeguidos(dtUsuario.getNickname());
                        request.setAttribute("listaSeguidores", listaSeguidores);
                        request.setAttribute("cantidadSeguidores", listaSeguidores.size());
                        request.setAttribute("listaSeguidos", listaSeguidos);
                        request.setAttribute("cantidadSeguidos", listaSeguidos.size());
                        request.setAttribute("usuarioPerfil", dtUsuario);
                        request.setAttribute("esPropioPerfil", esPropioPerfil);
                        request.setAttribute("loSigo", loSigo);
                        request.getRequestDispatcher("/WEB-INF/fragmentos/detalleConsultaUsuario.jsp").forward(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                LocalDate fecha = leerFecha(fechaNacimiento);
                if (fecha == null)
                    throw new IllegalArgumentException("Ingrese una fecha de nacimiento válida.");
                TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
                DtUsuario usuario = Remux.DtUsuario(
                    nickname, password, email, nombre, apellido,
                    null, fecha, institutos, tipo );
                Fabrica.getConexion().crearUsuario(usuario, bytesImagen, nombreOriginal);
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
                LocalDate fecha = leerFecha(fechaNacimiento);
                if (fecha == null)   // si no llega una fecha válida se conserva la que tenía (no se borra)
                    fecha = Utilsw.Cast.valueOf(usuarioActual.getFNacimiento());
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
                DtUsuario usuarioModificado = Remux.DtUsuario(
                    usuarioActual.getNickname(),
                    passwordParaControlador,
                    usuarioActual.getEmail(),
                    nombre,
                    apellido,
                    nombreImagenFinal, 
                    fecha,
                    institutos,
                    usuarioActual.getTipoUsuario());
                Fabrica.getConexion().modificarUsuario(usuarioModificado, bytesImagen, nombreOriginal);
                actualizarSesionUsuario(request, usuarioActual.getNickname());
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
                if (!Fabrica.getConexion().existeUsuario(nicknameASeguir.trim())) {
                    throw new Exception("El usuario '" + nicknameASeguir + "' no existe.");
                }
                List<String> seguidos = Fabrica.getConexion().listarSeguidos(logueado.getNickname());
                if (seguidos.contains(nicknameASeguir.trim())) {
                    throw new Exception("Ya sigue al usuario '" + nicknameASeguir + "'.");
                }
                Fabrica.getConexion().seguirUsuario(logueado.getNickname(), nicknameASeguir.trim());
                actualizarSesionUsuario(request, logueado.getNickname());
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
                Fabrica.getConexion().dejarDeSeguirUsuario(logueado.getNickname(), nicknameADejar.trim());
                actualizarSesionUsuario(request, logueado.getNickname());
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
                Fabrica.getConexion().dejarDeSeguirUsuario(seguidorAEliminar.trim(), logueado.getNickname());
                actualizarSesionUsuario(request, logueado.getNickname());
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Se ha eliminado a '" + seguidorAEliminar.trim() + "' de tus seguidores.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
            }
        } else if ("desistirInscripcion".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Debe iniciar sesión para desistir de una inscripción.");
                return;
            }
            DtUsuario usuarioActual = (DtUsuario) session.getAttribute("usuarioLogueado");
            if (usuarioActual.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Solo los estudiantes pueden desistir de sus inscripciones.");
                return;
            }
            String nombreEdicion = request.getParameter("edicion");
            if (nombreEdicion == null || nombreEdicion.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("No se recibió una edición válida.");
                return;
            }
            try {
                int resultado = Fabrica.getConexion().delInscripcion(usuarioActual.getNickname(), nombreEdicion.trim());
                response.setContentType("text/plain;charset=UTF-8");
                if (resultado == 0) {
                    response.getWriter().write("Inscripción cancelada correctamente.");
                } else if (resultado == 1) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("No existe esa inscripción.");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("No se pudo cancelar la inscripción.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Error al desistir de la inscripción: " + e.getMessage());
            }
        }
    }
    
    private void actualizarListasSociales(HttpServletRequest request) {
        DtUsuario logueado = (DtUsuario) request.getSession().getAttribute("usuarioLogueado");
        if (logueado != null) {
            try {
                request.setAttribute("listaSeguidos", Fabrica.getConexion().listarSeguidos(logueado.getNickname()));
                request.setAttribute("listaSeguidores", Fabrica.getConexion().listarSeguidores(logueado.getNickname()));
            } catch (Exception e) {
                request.setAttribute("listaSeguidos", new ArrayList<String>());
                request.setAttribute("listaSeguidores", new ArrayList<String>());
            }
        }
    }

    private void actualizarSesionUsuario(HttpServletRequest request, String nickname) throws Exception {
        DtUsuario u = Fabrica.getConexion().obtenerUsuario(nickname);
        if (u != null) {
            DtUsuario sinPassword = Remux.DtUsuario(u.getNickname(),
                null,
                u.getEmail(),
                u.getNombre(),
                u.getApellido(),
                u.getImagen(),
                Utilsw.Cast.valueOf(u.getFNacimiento()),
                u.getInstitutos(),
                u.getTipoUsuario()
            );
            request.getSession().setAttribute("usuarioLogueado", sinPassword);
        }
    }

    // El <input type="date"> del navegador manda yyyy-MM-dd; también se acepta dd/MM/yyyy. Si no es válida: null
    private static LocalDate leerFecha(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return LocalDate.parse(valor.trim());
        } catch (Exception e) {
            return Utilsw.Cast.valueOf(valor.trim());
        }
    }
}