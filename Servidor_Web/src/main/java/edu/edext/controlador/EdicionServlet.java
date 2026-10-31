package edu.edext.controlador;

import edu.edext.cliente.DtEdicion;
import edu.edext.cliente.DtUsuario;
import edu.edext.cliente.TipoUsuario;
import edu.edext.cliente.Publicador;
import edu.edext.web.ClienteCentral;
import edu.edext.web.Datos;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; 
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; 
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import edu.edext.cliente.DtInscripcionEdicion;
import java.util.ArrayList;
import java.util.HashSet; 
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/EdicionServlet")
@MultipartConfig // IMPORTANTE: Sin esto, el Servlet no puede leer el FormData con imágenes
public class EdicionServlet extends HttpServlet {
    private Publicador ic = ClienteCentral.get();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "error";

        switch (accion) {
            case "altaEdicion":
                // 1. Control de Seguridad: Solo docentes
                HttpSession session = request.getSession();
                DtUsuario usuarioLogueado = (DtUsuario) session.getAttribute("usuarioLogueado");
                
                if (usuarioLogueado == null || usuarioLogueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los docentes pueden realizar esta acción.");
                    return;
                }

                try {
                    // 2. Cargar listas iniciales (Institutos y Docentes)
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.setAttribute("docentes", ic.listarDocentes());
                    request.getRequestDispatcher("fragmentos/altaEdicion.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;

            case "listarCursosPorInstituto":
                // Devuelve un JSON con los cursos asociados a un instituto seleccionado
                try {
                    String instituto = request.getParameter("instituto");
                    List<String> cursos = ic.listarCursosPorInstituto(instituto);
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < cursos.size(); i++) {
                        // FIX: Escapar caracteres problemáticos para construir un JSON válido y seguro.
                        // Reemplazamos comillas dobles, barras invertidas y saltos de línea.
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
                 DtEdicion edicion = ic.getEdicion(nombreEdicion);

                 if (edicion == null) {
                     response.sendError(HttpServletResponse.SC_NOT_FOUND, "La edición no existe.");
                     return;
                 }

                 List<DtInscripcionEdicion> todasLasInscripciones = ic.getInscripcionesCompletas(nombreEdicion);
                 List<DtInscripcionEdicion> aceptadas = new ArrayList<>();

                 if (todasLasInscripciones != null) {
                     for (DtInscripcionEdicion ins : todasLasInscripciones) {
                         if (ins.getEstado() == 2) {
                             aceptadas.add(ins);
                         }
                     }
                 }

                 request.setAttribute("edicion", edicion);
                 request.setAttribute("inscripcionesAceptadas", aceptadas); // Pasamos la lista al JSP
                 request.getRequestDispatcher("fragmentos/consultaEdicion.jsp").forward(request, response);
             } catch (Exception e) {
                 response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error cargando la edición: " + e.getMessage());
             }
             break; 
             
             case "listarDocentesJSON":
             try {
                 // Reutilizamos la misma lista de tu Alta Edición
                 List<String> docentesList = ic.listarDocentes(); 
                 StringBuilder jsonDoc = new StringBuilder("[");
                 for (int i = 0; i < docentesList.size(); i++) {
                     // FIX: También aplicamos la sanitización aquí por seguridad.
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
                // 1. Extraer datos simples
                String nombreCurso = request.getParameter("curso");
                String nombreEdicion = request.getParameter("nombre");
                String fechaInicioStr = request.getParameter("fechaInicio");
                String fechaFinStr = request.getParameter("fechaFin");
                String cupoStr = request.getParameter("cupo");
                
                // 2. Extraer docentes seleccionados
                String[] docentesArray = request.getParameterValues("docentes");
                Set<String> docentes = docentesArray != null ? new HashSet<>(Arrays.asList(docentesArray)) : new HashSet<>();

                if (docentes.isEmpty()) {
                    throw new Exception("Debe seleccionar al menos un docente para dictar la edición.");
                }

                // 3. Parsear fechas al formato LocalDate
                LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
                LocalDate fechaFin = LocalDate.parse(fechaFinStr);
                LocalDate fechaPublicacion = LocalDate.now();

                if(fechaInicio.isAfter(fechaFin)) {
                     throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin.");
                }

                // 4. Procesar Cupo opcional
                Integer cupo = null;
                if (cupoStr != null && !cupoStr.trim().isEmpty()) {
                    cupo = Integer.parseInt(cupoStr.trim());
                    if (cupo < 0) {
                        throw new Exception("El cupo no puede ser negativo.");
                    }
                }

                // --- 5. Capturar y guardar la imagen de la edición ---
                String nombreImagen = "usr.png"; // Imagen genérica por defecto
                Part filePart = request.getPart("imagen"); // Asegúrate de que el input en tu JSP se llame name="imagen"
                
                if (filePart != null && filePart.getSize() > 0) {
                    byte[] imgBytes = filePart.getInputStream().readAllBytes();
                    String nombreOriginal = filePart.getSubmittedFileName();
                    
                    // Limpiamos los espacios del nombre de la edición para usarlo como identificador del archivo
                    String identificadorUnico = "Edicion_" + nombreEdicion.replaceAll("\\s+", "_");
                    
                    // Guardamos físicamente la imagen y obtenemos el nombre del archivo final
                    nombreImagen = ic.guardarImagen(imgBytes, nombreOriginal, identificadorUnico);
                }

                // 6. Instanciar el DtEdicion
                DtEdicion dt = Datos.edicion(nombreEdicion, cupo, fechaInicio, fechaFin, fechaPublicacion, docentes, nombreImagen);

                // 7. Impactar en base de datos
                ic.altaEdicionCurso(nombreCurso, dt);

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
            // Recibe la petición del modal para guardar un docente en una edición
            try {
                String edicion = request.getParameter("edicion");
                String docente = request.getParameter("docente");
                
                ic.agregarDocenteAEdicion(edicion, docente);
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Docente agregado exitosamente.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } else if ("quitarDocente".equals(accion)) {
            // Recibe la petición para quitar un docente
            try {
                String edicion = request.getParameter("edicion");
                String docente = request.getParameter("docente");
                
                ic.quitarDocenteDeEdicion(edicion, docente);
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Docente removido exitosamente.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        }
    }
}