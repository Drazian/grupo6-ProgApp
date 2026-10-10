package edu.edext.controlador;

import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/EdicionServlet")
public class EdicionServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();

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
                        json.append("\"").append(cursos.get(i)).append("\"");
                        if (i < cursos.size() - 1) json.append(",");
                    }
                    json.append("]");

                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo cursos.");
                }
                break;
        }
    }

@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String accion = request.getParameter("accion");
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

                // 3. Parsear fechas al formato LocalDate (El fix del error)
                LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
                LocalDate fechaFin = LocalDate.parse(fechaFinStr);
                LocalDate fechaPublicacion = LocalDate.now(); // Fecha actual del sistema

                // Validación lógica rápida de fechas usando isAfter de LocalDate
                if(fechaInicio.isAfter(fechaFin)) {
                     throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin.");
                }

                // 4. Procesar Cupo opcional
                Integer cupo = null;
                if (cupoStr != null && !cupoStr.trim().isEmpty()) {
                    cupo = Integer.parseInt(cupoStr);
                }

                // 5. Instanciar el DtEdicion (Ahora los tipos coinciden perfectamente)
                DtEdicion dt = new DtEdicion(
                    nombreEdicion, cupo, fechaInicio, fechaFin, fechaPublicacion, null, docentes
                );

                // 6. Impactar en base de datos
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
        }
    }
}