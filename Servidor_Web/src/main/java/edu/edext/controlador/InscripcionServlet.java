package edu.edext.controlador;

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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

@WebServlet("/InscripcionServlet")
public class InscripcionServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "error";

        HttpSession session = request.getSession();

        switch (accion) {
            case "formulario": {
                // 1. Barrera de Seguridad: Solo estudiantes
                DtUsuario usuario = (DtUsuario) session.getAttribute("usuarioLogueado");
                
                if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los Estudiantes pueden realizar inscripciones.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.getRequestDispatcher("fragmentos/inscripcionEdicion.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;
            }

            case "listarEdiciones": {
                // Devuelve un JSON con las ediciones de un curso seleccionado
                try {
                    String curso = request.getParameter("curso");
                    List<String> ediciones = ic.listaEdicionPorCurso(curso);
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < ediciones.size(); i++) {
                        json.append("\"").append(ediciones.get(i)).append("\"");
                        if (i < ediciones.size() - 1) json.append(",");
                    }
                    json.append("]");

                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo ediciones.");
                }
                break;
            }

            // =====================================================================
            // NUEVOS ENDPOINTS: CASO DE USO "SELECCIONAR ESTUDIANTES" (DOCENTE)
            // =====================================================================

            case "formularioSeleccion": {
                // Seguridad: Solo Docentes
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo Docentes pueden evaluar inscripciones.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    // Carga la estructura visual base
                    request.getRequestDispatcher("fragmentos/seleccionEstudiantes.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;
            }

            case "listarInscripciones": {
                // Devuelve la tabla HTML procesada con los estudiantes inscriptos a la edición
                try {
                    String edicion = request.getParameter("edicion");
                    // Traemos las inscripciones ya con la prioridad calculada (ipr * 0.5)
                    List<edu.edext.datatypes.DtInscripcionEdicion> inscripciones = ic.getInscripcionesCompletas(edicion);
                    
                    request.setAttribute("inscripciones", inscripciones);
                    request.getRequestDispatcher("fragmentos/tablaInscripciones.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo las inscripciones: " + e.getMessage());
                }
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();

        if ("inscribir".equals(accion)) {
            DtUsuario estudiante = (DtUsuario) session.getAttribute("usuarioLogueado");
            
            if (estudiante == null || estudiante.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("La sesión expiró o no tienes permisos de Estudiante.");
                return;
            }

            try {
                String nombreEdicion = request.getParameter("edicion");
                String nicknameEstudiante = estudiante.getNickname();
                
                // Llamamos a la lógica. 0=Exito, 1=Falta Estudiante, 2=Falta Edicion, -1=Error/Ya inscripto
                int resultado = ic.setInscripcion(nicknameEstudiante, nombreEdicion);
                
                if (resultado == 0) {
                     response.setStatus(HttpServletResponse.SC_OK);
                     response.getWriter().write("¡Inscripción realizada con éxito!");
                } else if (resultado == 1) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: El estudiante no existe en la base de datos.");
                } else if (resultado == 2) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: La edición de curso ya no está disponible.");
                } else {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("No se pudo procesar. Es posible que ya estés inscripto a esta edición.");
                }
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error del sistema: " + e.getMessage());
            }
        }
        else if ("guardarSeleccion".equals(accion)) {
            DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
            
            if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("La sesión expiró o no tienes permisos de Docente.");
                return;
            }

            try {
                String nombreEdicion = request.getParameter("edicion");
                Map<String, Integer> evaluaciones = new HashMap<>();
                
                // Buscamos dinámicamente todos los inputs que enviará nuestro frontend con el prefijo "estado_"
                Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (paramName.startsWith("estado_")) {
                        String nickname = paramName.substring(7); // Extraemos el nickname (quitando "estado_")
                        int estado = Integer.parseInt(request.getParameter(paramName));
                        evaluaciones.put(nickname, estado);
                    }
                }
                
                ic.evaluarInscripciones(nombreEdicion, evaluaciones);
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Selección de estudiantes guardada con éxito.");
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error al guardar: " + e.getMessage());
            }
        }
    }
}