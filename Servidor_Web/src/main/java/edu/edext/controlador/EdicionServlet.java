package edu.edext.controlador;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import edu.edext.controlador.ws.DtUsuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import edu.edext.controlador.ws.DtInscripcionEdicion;
import jakarta.servlet.annotation.MultipartConfig; 
import jakarta.servlet.http.HttpServletResponse;
import java.time.format.DateTimeParseException;
import edu.edext.controlador.ws.TipoUsuario;
import edu.edext.controlador.ws.DtEdicion;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; 
import edu.edext.modelo.Remux;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet; 
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@WebServlet("/EdicionServlet")
@MultipartConfig // IMPORTANTE: Sin esto, el Servlet no puede leer el FormData con imágenes
public class EdicionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "error";

        switch (accion) {
            case "altaEdicion":
                HttpSession session = request.getSession();
                DtUsuario usuarioLogueado = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (usuarioLogueado == null || usuarioLogueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los docentes pueden realizar esta acción.");
                    return;
                }
                try {
                    request.setAttribute("institutos", Fabrica.getConexion().listarInstitutos());
                    request.setAttribute("docentes", Fabrica.getConexion().listarDocentes());
                    request.getRequestDispatcher("/WEB-INF/fragmentos/altaEdicion.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;

            case "listarCursosPorInstituto":
                try {
                    String instituto = request.getParameter("instituto");
                    List<String> cursos = Fabrica.getConexion().listarCursosPorInstituto(instituto);
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < cursos.size(); i++) {
                        String cursoSanitizado = cursos.get(i)
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                                .replace("\r", "\\r");
                                
                        json.append("\"").append(cursoSanitizado).append("\"");
                        if (i < cursos.size() - 1) json.append(",");
                    }
                    json.append("]");

                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo cursos.");
                }
                break;
            
            case "consultaEdicion":
                try {
                 String nombreEdicion = request.getParameter("nombre");
                 DtEdicion edicion = Fabrica.getConexion().getEdicion(nombreEdicion);
                 if (edicion == null) {
                     response.sendError(HttpServletResponse.SC_NOT_FOUND, "La edición no existe.");
                     return;
                 }
                 List<DtInscripcionEdicion> todasLasInscripciones = Fabrica.getConexion().getInscripcionesCompletas(nombreEdicion);
                 List<DtInscripcionEdicion> aceptadas = new ArrayList<>();

                 if (todasLasInscripciones != null) {
                     for (DtInscripcionEdicion ins : todasLasInscripciones) {
                         if (ins.getEstado() == 2) {
                             aceptadas.add(ins);
                         }
                     }
                 }
                 request.setAttribute("edicion", edicion);
                 request.setAttribute("inscripcionesAceptadas", aceptadas);
                 request.getRequestDispatcher("/WEB-INF/fragmentos/consultaEdicion.jsp").forward(request, response);
             } catch (Exception e) {
                 response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error cargando la edición: " + e.getMessage());
             }
             break; 

            case "existeEdicion": {
                String nombreEdicion = request.getParameter("nombre");
                try{
                    boolean existe = Fabrica.getConexion().existeEdicion(nombreEdicion);
                    response.setContentType("text/plain");
                    response.getWriter().write(String.valueOf(existe));
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al comprobar nombre: " + e.getMessage() + "</h3>");
                }
            }
            break;

             case "listarDocentesJSON":
             try {
                 List<String> docentesList = Fabrica.getConexion().listarDocentes(); 
                 StringBuilder jsonDoc = new StringBuilder("[");
                 for (int i = 0; i < docentesList.size(); i++) {
                     String docenteSanitizado = docentesList.get(i)
                             .replace("\\", "\\\\")
                             .replace("\"", "\\\"")
                             .replace("\n", "\\n")
                             .replace("\r", "\\r");
                     jsonDoc.append("\"").append(docenteSanitizado).append("\"");
                     if (i < docentesList.size() - 1) jsonDoc.append(",");
                 }
                 jsonDoc.append("]");
                 response.setContentType("application/json; charset=UTF-8");
                 response.getWriter().write(jsonDoc.toString());
             } catch (Exception e) {
                 response.sendError(500, "Error cargando docentes");
             }
             break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession(false);
        DtUsuario usuarioLogueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
        if (usuarioLogueado == null || usuarioLogueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Acceso denegado. Solo los docentes pueden realizar esta acción.");
            return;
        }
        
        if ("altaEdicion".equals(accion)) {
            try {
                String nombreCurso = request.getParameter("curso");
                String nombreEdicion = request.getParameter("nombre");
                String fechaInicioStr = request.getParameter("fechaInicio");
                String fechaFinStr = request.getParameter("fechaFin");
                String cupoStr = request.getParameter("cupo");
                String[] docentesArray = request.getParameterValues("docentes");
                Set<String> docentes = docentesArray != null ? new HashSet<>(Arrays.asList(docentesArray)) : new HashSet<>();
                if (docentes.isEmpty()) {
                    throw new Exception("Debe seleccionar al menos un docente para dictar la edición.");
                }
                LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
                LocalDate fechaFin = LocalDate.parse(fechaFinStr);
                LocalDate fechaPublicacion = LocalDate.now();
                if(fechaInicio.isAfter(fechaFin)) {
                     throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin.");
                }
                Integer cupo = null;
                if (cupoStr != null && !cupoStr.trim().isEmpty()) {
                    cupo = Integer.parseInt(cupoStr.trim());
                    if (cupo < 0) {
                        throw new Exception("El cupo no puede ser negativo.");
                    }
                }
                String nombreImagen = "usr.png";
                Part filePart = request.getPart("imagen");
                if (filePart != null && filePart.getSize() > 0) {
                    byte[] imgBytes = filePart.getInputStream().readAllBytes();
                    String nombreOriginal = filePart.getSubmittedFileName();
                    String identificadorUnico = "Edicion_" + nombreEdicion.replaceAll("\\s+", "_");
                    nombreImagen = Fabrica.getConexion().guardarImagen(imgBytes, nombreOriginal, identificadorUnico);
                }
                DtEdicion dt = Remux.DtEdicion(nombreEdicion, cupo, fechaInicio, fechaFin, fechaPublicacion, null, docentes, nombreImagen);
                Fabrica.getConexion().altaEdicionCurso(nombreCurso, dt);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Edición de curso registrada exitosamente.");
            } catch (DateTimeParseException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Formato de fecha inválido.");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("El cupo debe ser un número válido.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
            
        } else if ("agregarDocente".equals(accion)) {
            try {
                String edicion = request.getParameter("edicion");
                String docente = request.getParameter("docente");
                Fabrica.getConexion().agregarDocenteAEdicion(edicion, docente);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Docente agregado exitosamente.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } else if ("quitarDocente".equals(accion)) {
            try {
                String edicion = request.getParameter("edicion");
                String docente = request.getParameter("docente");
                Fabrica.getConexion().quitarDocenteDeEdicion(edicion, docente);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Docente removido exitosamente.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        }
    }
}