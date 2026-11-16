package edu.edext.controlador;

import java.util.List;
import java.time.LocalDate;
import java.io.IOException;
import java.io.InputStream;
import edu.edext.modelo.Remux;
import edu.edext.controlador.ws.DtCurso;
import edu.edext.controlador.ws.DtPrograma;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.TipoUsuario;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;

@WebServlet("/ProgramaServlet")
@MultipartConfig
public class ProgramaServlet extends HttpServlet {
    
    //Responsable de mostrar formularios
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null){
            accion = "error";
        }
        
        switch (accion){

            case "formCrear": {
                HttpSession session = request.getSession(false);
                DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
                if (logueado == null || logueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso exclusivo para Docentes.");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/fragmentos/crearPrograma.jsp").forward(request, response);
            	break;
            }

            case "formAgregarCurso": {
                HttpSession session = request.getSession(false);
                DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
                if (logueado == null || logueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso exclusivo para Docentes.");
                    return;
                }
                try {
                    List<DtPrograma> programas = Fabrica.getConexion().listarProgramas();
                    List<DtCurso> cursos = Fabrica.getConexion().listarCursos();
                    
                    request.setAttribute("programas", programas);
                    request.setAttribute("cursos", cursos);
                    
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                }
                request.getRequestDispatcher("/WEB-INF/fragmentos/agregarCursoPrograma.jsp").forward(request, response);
		break;
	    }
            
            case "formVerProgramas":
            try {
                    List<DtPrograma> programas = Fabrica.getConexion().listarProgramas(); 

                    request.setAttribute("listaProgramas", programas);
                    request.getRequestDispatcher("/WEB-INF/fragmentos/verProgramas.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/fragmentos/error.jsp").forward(request, response);
                }
            break;

            case "existePrograma": {
                String nombre = request.getParameter("nombre");
                try{
                    boolean existe = Fabrica.getConexion().existeProgramaFormacion(nombre);
                    response.setContentType("text/plain");
                    response.getWriter().write(String.valueOf(existe));
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("<h3 style='color:red; text-align:center;'>Error al comprobar nombre: " + e.getMessage() + "</h3>");
                }
            }
            break;
            
            case "detallePrograma":
                try {
                    String nombrePrograma = request.getParameter("nombre");
                    DtPrograma programa = Fabrica.getConexion().buscarPrograma(nombrePrograma); 
                    request.setAttribute("programa", programa);
                    request.getRequestDispatcher("/WEB-INF/fragmentos/programaDetalles.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/fragmentos/error.jsp").forward(request, response);
                }
            break;
            
            case "formInscripcionPrograma": {
                HttpSession session = request.getSession(false);
                DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;

                if (logueado == null || logueado.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso exclusivo para Estudiantes.");
                    return;
                }
                try {
                    List<DtPrograma> programas = Fabrica.getConexion().listarProgramas();
                    request.setAttribute("listaProgramas", programas);
                    request.getRequestDispatcher("/WEB-INF/fragmentos/inscripcionPrograma.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/fragmentos/error.jsp").forward(request, response);
                }
                break;
            }
            
            default:
                request.getRequestDispatcher("/WEB-INF/fragmentos/error.jsp").forward(request, response);
            break;

        }
    }

    //Responsable de responder a los botones.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        String accion = request.getParameter("accion");
        if (accion == null){
            accion = "error";
        }
        HttpSession session = request.getSession(false);
        DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
        
        switch (accion){

            case "crear": {
                if (logueado == null || logueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Acceso denegado. Solo los docentes pueden crear programas.");
                    return;
                }
                String nombre = request.getParameter("nombre");
                String desc = request.getParameter("desc");
                String fechaInicioStr = request.getParameter("fechaInicio");
                String fechaFinStr = request.getParameter("fechaFin");
                
                if (nombre == null || nombre.trim().isEmpty() || desc == null || desc.trim().isEmpty() || 
                        fechaInicioStr == null || fechaInicioStr.trim().isEmpty() || fechaFinStr == null || fechaFinStr.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Los datos no pueden estar vacíos.");
                    return;
                }
                LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
                LocalDate fechaFin = LocalDate.parse(fechaFinStr);
                LocalDate fechaRegistro = LocalDate.now();
                
                Part partImagen = request.getPart("imagen");
                byte[] bytesImagen = null;
                String nombreOriginal = null;
                if (partImagen != null && partImagen.getSize() > 0) {
                    nombreOriginal = partImagen.getSubmittedFileName();
                    try (InputStream is = partImagen.getInputStream()) {
                        bytesImagen = is.readAllBytes();
                    }
                }
                DtPrograma programa = Remux.DtPrograma(nombre, desc, fechaRegistro, fechaInicio, fechaFin);
                programa.setDocente(logueado.getNickname());
                try {
                    boolean resultado = Fabrica.getConexion().setCrearProgramaFormacion(programa, bytesImagen, nombreOriginal);
                    if (resultado == true){
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("Programa " + nombre + " creado con exito.");                    
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("No se pudo crear el programa (verifique que el nombre no exista y las fechas sean coherentes).");                         
                    }
                } catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al crear el programa: " + e.getMessage());                    
                }
                break;
            }
            
	    case "agregarCurso": {
                if (logueado == null || logueado.getTipoUsuario() != TipoUsuario.DOCENTE) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Acceso denegado. Solo los docentes pueden agregar cursos a un programa.");
                    return;
                }
                String programaElegido = request.getParameter("programaStr");
                String[] cursosElegidos = request.getParameterValues("cursoStr");
                
                if (programaElegido == null || programaElegido.trim().isEmpty() || cursosElegidos == null || cursosElegidos.length == 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Debe seleccionar un programa y al menos un curso.");
                    return;
                }

                try {
                    int agregados = 0;
                    for (String c : cursosElegidos) {
                        try {
                            Fabrica.getConexion().agregarProgramaCurso(programaElegido, c);
                            agregados++;
                        } catch (Exception individualEx) {}
                    }
                    if(agregados > 0){
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("Se agregaron " + agregados + " curso(s) con éxito al programa " + programaElegido + ".");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("No se agregó ningún curso (es posible que ya pertenecieran al programa).");
                    }
                } catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error crítico al procesar: " + e.getMessage());                    
                }
	        break;
            }

            case "inscribirPrograma": {
                if (logueado == null || logueado.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("La sesión expiró o no tienes permisos de Estudiante.");
                    return;
                }
                String nombrePrograma = request.getParameter("programa");
                if (nombrePrograma == null || nombrePrograma.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Debe seleccionar un programa de formación.");
                    return;
                }
                try {
                    int res = Fabrica.getConexion().setInscripcionPrograma(logueado.getNickname(), nombrePrograma.trim());

                    if (res == 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("¡Inscripción al Programa de Formación realizada con éxito!");
                    } else if (res == 1) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("Atención: Ya te encuentras inscripto a este programa de formación.");
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al procesar la inscripción: " + e.getMessage());
                }
                break;
            }
            
            default:
                request.getRequestDispatcher("/WEB-INF/fragmentos/error.jsp").forward(request, response);
            break;
        }
    }

}