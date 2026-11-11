package edu.edext.controlador;

//import edu.edext.datatypes.DtEdicion;
//import edu.edext.datatypes.DtInscripcionEdicion;
//import edu.edext.datatypes.DtUsuario;
//import edu.edext.datatypes.TipoUsuario;
//import edu.edext.logica.Fabrica;
//import edu.edext.logica.IBridgeSOAP;
//import edu.edext.logica.IControlador;

import edu.edext.controlador.ws.DtConsultaCurso;
import edu.edext.controlador.ws.DtEdicion;
import edu.edext.controlador.ws.DtInscripcionEdicion;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.IBridgeSOAP;
import edu.edext.controlador.ws.TipoUsuario;

import edu.edext.controlador.ws.MapEntry;
import edu.edext.controlador.ws.MapWrapper;
import edu.edext.tools.Utils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
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
                    // El estudiante puede buscar el curso por instituto o por categoría
                    request.setAttribute("categorias", ic.listarCategorias());
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

            // Inscripción a edición: cursos de una categoría (el estudiante busca por instituto o por categoría)
            case "cursosPorCategoria": {
                try {
                    List<String> cursos = new ArrayList<>();
                    List<String> delCentral = ic.listarCursosPorCategoria(request.getParameter("categoria"));
                    if (delCentral != null) cursos.addAll(delCentral);
                    cursos.sort(String.CASE_INSENSITIVE_ORDER);
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < cursos.size(); i++) {
                        if (i > 0) json.append(",");
                        json.append(textoJson(cursos.get(i)));
                    }
                    json.append("]");
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo los cursos de la categoría.");
                }
                break;
            }

            // Inscripción a edición: ediciones vigentes del curso (no cerradas y que todavía no terminaron)
            // con sus datos básicos, para mostrarlos antes de confirmar la inscripción
            case "edicionesVigentes": {
                try {
                    DtConsultaCurso curso = ic.obtenerDatosCurso(request.getParameter("curso"));
                    LocalDate hoy = LocalDate.now();
                    List<DtEdicion> vigentes = new ArrayList<>();
                    if (curso != null && curso.getEdiciones() != null)
                        for (DtEdicion ed : curso.getEdiciones()) {
                            boolean cerrada = ed.isCerrada() || (ed.getFechaCierre() != null && !ed.getFechaCierre().isBlank());
                            LocalDate fin = Utils.Cast.valueOf(ed.getFechaFin());
                            if (!cerrada && (fin == null || !fin.isBefore(hoy)))
                                vigentes.add(ed);
                        }
                    vigentes.sort(Comparator.comparing((DtEdicion ed) -> Utils.Cast.valueOf(ed.getFechaInicio()),
                            Comparator.nullsLast(Comparator.naturalOrder())));

                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < vigentes.size(); i++) {
                        DtEdicion ed = vigentes.get(i);
                        List<String> docentes = new ArrayList<>();
                        for (DtUsuario d : ed.getDocentes()) {
                            String nombre = ((d.getNombre() == null ? "" : d.getNombre()) + " " + (d.getApellido() == null ? "" : d.getApellido())).trim();
                            docentes.add(nombre.isEmpty() ? d.getNickname() : nombre);
                        }
                        if (i > 0) json.append(",");
                        json.append("{\"nombre\":").append(textoJson(ed.getNombre()))
                            .append(",\"fechaInicio\":").append(textoJson(ed.getFechaInicio()))
                            .append(",\"fechaFin\":").append(textoJson(ed.getFechaFin()))
                            .append(",\"fechaPublicacion\":").append(textoJson(ed.getFechaPublicacion()))
                            .append(",\"cupo\":").append(ed.getCupo() == null ? "null" : String.valueOf(ed.getCupo()))
                            .append(",\"docentes\":").append(textoJson(String.join(", ", docentes)))
                            .append("}");
                    }
                    json.append("]");
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo las ediciones del curso.");
                }
                break;
            }

            // Inscripción a edición (letra 6.1): si el estudiante ya está inscripto a la edición elegida, responde los datos
            // de su inscripción, para ofrecerle modificarlos o cancelar. Responde null si no está inscripto.
            case "miInscripcion": {
                DtUsuario estudiante = (DtUsuario) session.getAttribute("usuarioLogueado");
                if (estudiante == null || estudiante.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los Estudiantes pueden realizar inscripciones.");
                    return;
                }
                try {
                    String edicion = request.getParameter("edicion");
                    DtInscripcionEdicion mia = null;
                    List<DtInscripcionEdicion> inscripciones = ic.getInscripcionesDeEstudiante(estudiante.getNickname());
                    if (inscripciones != null && edicion != null)
                        for (DtInscripcionEdicion ins : inscripciones)
                            if (ins.getEdicion() != null && edicion.equals(ins.getEdicion().getNombre())) { mia = ins; break; }

                    String json = "null";
                    if (mia != null) {
                        String[] estados = {"No inscripto", "Inscripto", "Aceptado", "Rechazado"};
                        int estado = mia.getEstado();
                        json = "{\"fecha\":" + textoJson(mia.getFechaInscripcion())
                             + ",\"estado\":" + textoJson(estado >= 0 && estado < estados.length ? estados[estado] : String.valueOf(estado))
                             + ",\"modificable\":" + (estado == 1)   // solo mientras el docente no la evaluó
                             + ",\"videoURL\":" + textoJson(mia.getVideoURL()) + "}";
                    }
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo tu inscripción a la edición.");
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
                } else if (resultado == 3) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Ya estás inscripto en esta edición: podés modificar los datos de tu inscripción o cancelar.");
                } else {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("No se pudo realizar la inscripción. Intente nuevamente.");
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error del sistema: " + e.getMessage());
            }
        }
        // Inscripción a edición (letra 6.1): el estudiante que ya está inscripto modifica los datos de su inscripción (el video)
        else if ("modificarInscripcion".equals(accion)) {
            DtUsuario estudiante = (DtUsuario) session.getAttribute("usuarioLogueado");

            if (estudiante == null || estudiante.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("La sesión expiró o no tienes permisos de Estudiante.");
                return;
            }

            try {
                int resultado = ic.modificarInscripcion(estudiante.getNickname(), request.getParameter("edicion"), request.getParameter("videoURL"));
                String mensaje;
                switch (resultado) {
                    case 0:  mensaje = "Se guardaron los cambios de tu inscripción."; break;
                    case 1:  mensaje = "No estás inscripto en esta edición."; break;
                    case 2:  mensaje = "Error: La edición de curso no existe."; break;
                    case 3:  mensaje = "El docente ya evaluó tu inscripción: no se pueden modificar sus datos."; break;
                    case 4:  mensaje = "La edición ya finalizó o fue cerrada: no se pueden modificar los datos de la inscripción."; break;
                    default: mensaje = "No se pudo modificar la inscripción. Intente nuevamente.";
                }
                response.setStatus(resultado == 0 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(mensaje);
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

    // Texto como valor JSON (entre comillas y escapado); null -> null
    private static String textoJson(String valor){
        if (valor == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

}
