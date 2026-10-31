package edu.edext.controlador;

import edu.edext.cliente.DtEdicion;
import edu.edext.cliente.DtInscripcionEdicion;
import edu.edext.cliente.DtUsuario;
import edu.edext.cliente.TipoUsuario;
import edu.edext.cliente.Publicador;
import edu.edext.web.ClienteCentral;
import edu.edext.web.Datos;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration; 

@WebServlet("/InscripcionServlet")
public class InscripcionServlet extends HttpServlet {
    private Publicador ic = ClienteCentral.get();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "error";

        HttpSession session = request.getSession();

        switch (accion) {
            case "formulario": {
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
                try {
                    String curso = request.getParameter("curso");
                    List<String> ediciones = ic.listaEdicionPorCurso(curso);
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < ediciones.size(); i++) {
                        // Escapar \ y " para que nombres con comillas no rompan el JSON
                        json.append("\"").append(ediciones.get(i).replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
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
            // CASO DE USO: "SELECCIONAR ESTUDIANTES" (DOCENTE)
            // =====================================================================

            case "formularioSeleccion": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo Docentes pueden evaluar inscripciones.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.getRequestDispatcher("fragmentos/seleccionEstudiantes.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;
            }

            case "listarInscripciones": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo Docentes pueden ver inscripciones.");
                    return;
                }
                try {
                    String edicion = request.getParameter("edicion");
                    DtEdicion dtEdicion = ic.getEdicion(edicion);
                    List<DtInscripcionEdicion> inscripciones = ic.getInscripcionesCompletas(edicion);
                    
                    request.setAttribute("edicion", dtEdicion);
                    request.setAttribute("inscripciones", inscripciones);
                    request.getRequestDispatcher("fragmentos/tablaInscripciones.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo las inscripciones: " + e.getMessage());
                }
                break;
            }

            // =====================================================================
            // CASO DE USO: "LISTAR ACEPTADOS A UNA EDICIÓN DE CURSO" (DOCENTE)
            // =====================================================================

            case "formularioAceptados": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo Docentes pueden consultar estudiantes aceptados.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.getRequestDispatcher("fragmentos/listarAceptados.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;
            }

            case "listarAceptados": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
                    return;
                }

                try {
                    String edicion = request.getParameter("edicion");
                    DtEdicion dtEdicion = ic.getEdicion(edicion);
                    List<DtInscripcionEdicion> todas = ic.getInscripcionesCompletas(edicion);
                    
                    List<DtInscripcionEdicion> aceptadas = new ArrayList<>();
                    if (todas != null) {
                        for (DtInscripcionEdicion ins : todas) {
                            if (ins.getEstado() == 2) {
                                aceptadas.add(ins);
                            }
                        }
                    }

                    request.setAttribute("edicion", dtEdicion);
                    request.setAttribute("inscripcionesAceptadas", aceptadas);
                    request.getRequestDispatcher("fragmentos/tablaAceptados.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo los estudiantes aceptados: " + e.getMessage());
                }
                break;
            }

            // =====================================================================
            // NUEVO CASO DE USO: "LISTAR RESULTADOS DE INSCRIPCIONES" (ESTUDIANTE)
            // =====================================================================

            case "misResultados": {
                DtUsuario estudiante = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (estudiante == null || estudiante.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los Estudiantes pueden consultar sus resultados de inscripción.");
                    return;
                }

                try {
                    List<DtInscripcionEdicion> misInscripciones = ic.getInscripcionesDeEstudiante(estudiante.getNickname());
                    request.setAttribute("misInscripciones", misInscripciones);
                    request.getRequestDispatcher("fragmentos/resultadosInscripciones.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener resultados de inscripciones: " + e.getMessage());
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
                
                int resultado = ic.setInscripcion(nicknameEstudiante, nombreEdicion);
                
                if (resultado == 0) {
                     response.setStatus(HttpServletResponse.SC_OK);
                     response.getWriter().write("¡Inscripción realizada con éxito!");
                } else if (resultado == 1) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: El estudiante no existe en la base de datos.");
                } else if (resultado == 2) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: La edición de curso no existe.");
                } else if (resultado == 4) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: La edición seleccionada ya finalizó.");
                } else {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Atención: Ya estás inscripto en esta edición.");
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
                
                Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (paramName.startsWith("estado_")) {
                        String nickname = paramName.substring(7);
                        int estado = Integer.parseInt(request.getParameter(paramName));
                        evaluaciones.put(nickname, estado);
                    }
                }
                
                ic.evaluarInscripciones(nombreEdicion, Datos.evaluaciones(evaluaciones));
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Selección de estudiantes guardada con éxito.");
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error al guardar: " + e.getMessage());
            }
        }
    }
}