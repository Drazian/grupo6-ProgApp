package edu.edext.controlador;

//import edu.edext.datatypes.DtEdicion;
//import edu.edext.datatypes.DtInscripcionEdicion;
//import edu.edext.datatypes.DtUsuario;
//import edu.edext.datatypes.TipoUsuario;
//import edu.edext.logica.Fabrica;
//import edu.edext.logica.IBridgeSOAP;
//import edu.edext.logica.IControlador;

import edu.edext.controlador.ws.DtEdicion;
import edu.edext.controlador.ws.DtInscripcionEdicion;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.IBridgeSOAP;
import edu.edext.controlador.ws.TipoUsuario;

import edu.edext.controlador.ws.MapEntry;
import edu.edext.controlador.ws.MapWrapper;
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
    private final IBridgeSOAP ic = Fabrica.getConexion();
//    private final IControlador ic = Fabrica.getInstance().getIControlador();

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
                    request.getRequestDispatcher("/WEB-INF/fragmentos/inscripcionEdicion.jsp").forward(request, response);
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
                    request.getRequestDispatcher("/WEB-INF/fragmentos/seleccionEstudiantes.jsp").forward(request, response);
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
                    request.getRequestDispatcher("/WEB-INF/fragmentos/tablaInscripciones.jsp").forward(request, response);
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
                    request.getRequestDispatcher("/WEB-INF/fragmentos/listarAceptados.jsp").forward(request, response);
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
                    request.getRequestDispatcher("/WEB-INF/fragmentos/tablaAceptados.jsp").forward(request, response);
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
                    request.getRequestDispatcher("/WEB-INF/fragmentos/resultadosInscripciones.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener resultados de inscripciones: " + e.getMessage());
                }
                break;
            }

            // =====================================================================
            // CASO DE USO: "LISTAR RESULTADOS DE EVALUACIONES EN EDICIONES DE CURSOS" (ESTUDIANTE)
            // =====================================================================

            case "misEvaluaciones": {
                DtUsuario estudiante = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (estudiante == null || estudiante.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los Estudiantes pueden consultar sus resultados de evaluaciones.");
                    return;
                }

                try {
                    // Inscripciones a ediciones cerradas, con nota y resultado (Aprobado / No aprobado)
                    List<DtInscripcionEdicion> resultados = ic.listarResultadosEvaluaciones(estudiante.getNickname());

                    int evaluadas = 0, aprobadas = 0, suma = 0;
                    for (DtInscripcionEdicion r : resultados) {
                        if (r.getNota() != null) {
                            evaluadas++;
                            suma += r.getNota();
                            if ("Aprobado".equals(r.getResultado())) aprobadas++;
                        }
                    }
                    request.setAttribute("resultadosEvaluaciones", resultados);
                    request.setAttribute("cantidadEvaluadas", evaluadas);
                    request.setAttribute("cantidadAprobadas", aprobadas);
                    if (evaluadas > 0)
                        request.setAttribute("promedioNotas", String.format(java.util.Locale.ROOT, "%.2f", (double) suma / evaluadas));
                    request.getRequestDispatcher("/WEB-INF/fragmentos/resultadosEvaluaciones.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener los resultados de evaluaciones: " + e.getMessage());
                }
                break;
            }

            // =====================================================================
            // CASO DE USO: "VER RESULTADOS DE EVALUACIONES EN EDICIONES DE CURSOS" (DOCENTE)
            // =====================================================================

            case "formularioResultados": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo Docentes pueden ver los resultados de las evaluaciones.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.getRequestDispatcher("/WEB-INF/fragmentos/verResultadosEdicion.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;
            }

            case "listarEdicionesCerradas": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
                    return;
                }

                try {
                    // Solo las ediciones cerradas del curso en las que el docente logueado participa
                    escribirJson(response, ic.listarEdicionesCerradasDeDocente(docente.getNickname(), request.getParameter("curso")));
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo las ediciones cerradas.");
                }
                break;
            }

            case "verActa": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
                    return;
                }

                String edicion = request.getParameter("edicion");
                try {
                    // El Central valida que la edicion exista, este cerrada y que el docente participe en ella
                    List<DtInscripcionEdicion> acta = ic.getActaEdicion(edicion, docente.getNickname());

                    int conNota = 0, suma = 0;
                    for (DtInscripcionEdicion ins : acta) {
                        if (ins.getNota() != null) {
                            conNota++;
                            suma += ins.getNota();
                        }
                    }

                    request.setAttribute("edicion", ic.getEdicion(edicion));
                    request.setAttribute("acta", acta);
                    if (conNota > 0) request.setAttribute("promedioActa", String.format(java.util.Locale.ROOT, "%.2f", (double) suma / conNota));
                } catch (Exception e) {
                    request.setAttribute("errorActa", e.getMessage());
                }
                request.getRequestDispatcher("/WEB-INF/fragmentos/tablaActaEdicion.jsp").forward(request, response);
                break;
            }

            // =====================================================================
            // CASO DE USO: "INGRESAR RESULTADOS DE EVALUACIONES DE ESTUDIANTES EN EDICIONES DE CURSOS" (DOCENTE)
            // =====================================================================

            case "formularioIngresoResultados": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo Docentes pueden ingresar resultados de evaluaciones.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.getRequestDispatcher("/WEB-INF/fragmentos/ingresarResultados.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;
            }

            case "listarEdicionesAbiertas": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
                    return;
                }

                try {
                    // Solo las ediciones todavia abiertas del curso en las que el docente logueado participa
                    escribirJson(response, ic.listarEdicionesAbiertasDeDocente(docente.getNickname(), request.getParameter("curso")));
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo las ediciones del docente.");
                }
                break;
            }

            case "tablaIngresoResultados": {
                DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado.");
                    return;
                }

                String edicion = request.getParameter("edicion");
                try {
                    // El Central valida que la edicion exista, siga abierta y que el docente participe en ella
                    List<DtInscripcionEdicion> participantes = ic.getParticipantesParaEvaluar(edicion, docente.getNickname());
                    request.setAttribute("edicion", ic.getEdicion(edicion));
                    request.setAttribute("participantes", participantes);
                } catch (Exception e) {
                    request.setAttribute("errorIngreso", e.getMessage());
                }
                request.getRequestDispatcher("/WEB-INF/fragmentos/tablaIngresoResultados.jsp").forward(request, response);
                break;
            }
        }
    }

    private static String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    // Responde una lista de textos como arreglo JSON
    private static void escribirJson(HttpServletResponse response, List<String> valores) throws IOException {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escaparJson(valores.get(i))).append("\"");
        }
        json.append("]");

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json.toString());
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
                String videoURL = request.getParameter("videoURL");   
             	String nicknameEstudiante = estudiante.getNickname();

                // Invocación corregida pasando los 3 parámetros
                int resultado = ic.setInscripcion(nicknameEstudiante, nombreEdicion, videoURL);

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
//                     response.getWriter().write("No se pudo procesar. Es posible que ya estés inscripto a esta edición.");
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
                        String valorParam = request.getParameter(paramName);
                        //Verificamos que se haya seleccionado un estado
                        if (valorParam != null && !valorParam.trim().isEmpty()) {
	                   String nickname = paramName.substring(7);
			   int estado = Integer.parseInt(request.getParameter(paramName));
			   evaluaciones.put(nickname, estado);
                        }
                    }
                }
                
                
//                MapWrapper evaluar = new MapWrapper();
//                for(Map.Entry<String, Integer> entry : evaluaciones.entrySet()){
//                    MapEntry entryCliente = new MapEntry();
//                    entryCliente.setKey(entry.getKey());
//                    entryCliente.setValue(entry.getValue());
//                    evaluar.getEntry().add(entryCliente);
//                }
                
                ic.evaluarInscripciones(nombreEdicion, toWrapper(evaluaciones));
//                ic.evaluarInscripciones(nombreEdicion, evaluaciones);
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Selección de estudiantes guardada con éxito.");
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error al guardar: " + e.getMessage());
            }
        }
        // CASO DE USO: "INGRESAR RESULTADOS DE EVALUACIONES DE ESTUDIANTES EN EDICIONES DE CURSOS" (DOCENTE)
        else if ("guardarResultados".equals(accion)) {
            DtUsuario docente = (DtUsuario) session.getAttribute("usuarioLogueado");

            if (docente == null || docente.getTipoUsuario() != TipoUsuario.DOCENTE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("La sesión expiró o no tienes permisos de Docente.");
                return;
            }

            String nombreEdicion = request.getParameter("edicion");
            boolean cerrar = "true".equals(request.getParameter("cerrar"));

            // Cada select del formulario viaja como nota_<nickname>; vacio = sin nota
            Map<String, Integer> notas = new HashMap<>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (!paramName.startsWith("nota_")) continue;
                String nickname = paramName.substring(5);
                String valor = request.getParameter(paramName);
                if (valor == null || valor.trim().isEmpty()) {
                    notas.put(nickname, null);
                    continue;
                }
                try {
                    notas.put(nickname, Integer.valueOf(valor.trim()));
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("La nota de '" + nickname + "' no es un número válido.");
                    return;
                }
            }

            try {
                ic.ingresarResultados(nombreEdicion, docente.getNickname(), toWrapper(notas), cerrar);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(cerrar
                        ? "Notas guardadas y edición cerrada. Ya puede consultar el acta en \"Ver Resultados de Evaluaciones\"."
                        : "Notas guardadas con éxito.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        }
    }
    
    private MapWrapper toWrapper(Map<String, Integer>  map){
        MapWrapper ret = new MapWrapper();
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            MapEntry entryCliente = new MapEntry();
            entryCliente.setKey(entry.getKey());
            entryCliente.setValue(entry.getValue());
            ret.getEntry().add(entryCliente);
        }
        return ret;
    }
    
}
