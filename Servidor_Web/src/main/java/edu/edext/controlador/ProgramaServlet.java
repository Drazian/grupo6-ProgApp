package edu.edext.controlador;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import java.io.PrintWriter; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/ProgramaServlet")
@MultipartConfig
public class ProgramaServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();
    
    //Responsable de mostrar formularios
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        if (accion == null){
            accion = "error"; //Opcion por defecto si no se indica nada.
        }
        
        switch (accion){
            case "formCrear":
                request.getRequestDispatcher("fragmentos/crearPrograma.jsp").forward(request, response);
            break;
            
            case "formAgregarCurso":
                try {
                    List<DtPrograma> programas = ic.listarProgramas();
                    List<DtCurso> cursos = ic.listarCursos();
                    
                    request.setAttribute("programas", programas);
                    request.setAttribute("cursos", cursos);
                    
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                }
                request.getRequestDispatcher("fragmentos/agregarCursoPrograma.jsp").forward(request, response);
            break;
            
            case "formVerProgramas":
            try {
                    List<DtPrograma> programas = ic.listarProgramas(); 

                    request.setAttribute("listaProgramas", programas);
                    request.getRequestDispatcher("fragmentos/verProgramas.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
                }
            break;
            
            case "detallePrograma":
                try {
                    String nombrePrograma = request.getParameter("nombre");

                    DtPrograma programa = ic.buscarPrograma(nombrePrograma); 

                    request.setAttribute("programa", programa);
                    request.getRequestDispatcher("fragmentos/programaDetalles.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
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
                    List<DtPrograma> programas = ic.listarProgramas();
                    request.setAttribute("listaProgramas", programas);
                    request.getRequestDispatcher("fragmentos/inscripcionPrograma.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
                }
                break;
            }
            
            default:
                request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
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
            accion = "error"; //Opcion por defecto si no se indica nada.
        }
        
        HttpSession session = request.getSession(false);
        DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;
        
        switch (accion){
            case "crear":
                String nombre = request.getParameter("nombre");
                String desc = request.getParameter("desc");
                String fechaInicioStr = request.getParameter("fechaInicio");
                String fechaFinStr = request.getParameter("fechaFin");
                
                if (nombre == null || nombre.trim().isEmpty() || desc == null || desc.trim().isEmpty() || 
                        fechaInicioStr == null || fechaInicioStr.trim().isEmpty() || fechaFinStr == null || fechaFinStr.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Los datos no puede estar vacío.");
                    return;
                }
                
                //Conversion de fechas
                LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
                LocalDate fechaFin = LocalDate.parse(fechaFinStr);
                
                LocalDate fechaRegistro = LocalDate.now();
                
                //Gestion de imagenes
                Part partImagen = request.getPart("imagen");
                byte[] bytesImagen = null;
                String nombreOriginal = null;
                
                if (partImagen != null && partImagen.getSize() > 0) {
                    nombreOriginal = partImagen.getSubmittedFileName();
                    try (InputStream is = partImagen.getInputStream()) {
                        bytesImagen = is.readAllBytes();
                    }
                }
                
                DtPrograma programa = new DtPrograma(nombre, desc, fechaRegistro, fechaInicio, fechaFin);
                               
                try {
                    boolean resultado = ic.setCrearProgramaFormacion(programa, bytesImagen, nombreOriginal, logueado.getNickname());
                    
                    if (resultado == true){
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("Programa " + nombre + " creado con exito.");                    
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("El programa " + nombre + " ya existe.");                         
                    }
                    

                } catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al crear el programa: " + e.getMessage());                    
                }
                
            break;
            
case "agregarCurso":
                String programaElegido = request.getParameter("programaStr");
                // Ahora usamos getParameterValues para recibir el array de cursos seleccionados
                String[] cursosElegidos = request.getParameterValues("cursoStr");
                
                if (programaElegido == null || programaElegido.trim().isEmpty() || cursosElegidos == null || cursosElegidos.length == 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Debe seleccionar un programa y al menos un curso.");
                    return;
                }

                try {
                    int agregados = 0;
                    // Iteramos sobre todos los cursos que el usuario tildó
                    for (String c : cursosElegidos) {
                        try {
                            ic.agregarProgramaCurso(programaElegido, c);
                            agregados++;
                        } catch (Exception individualEx) {
                            // Si un curso específico ya existe en el programa, lo saltamos silenciosamente 
                            // o podrías agregar un log aquí si lo deseas.
                        }
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
            case "inscribirPrograma": {
                //HttpSession session = request.getSession(false);
                //DtUsuario logueado = (session != null) ? (DtUsuario) session.getAttribute("usuarioLogueado") : null;

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
                    int res = ic.setInscripcionPrograma(logueado.getNickname(), nombrePrograma.trim());

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
                request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
            break;
        }        
        
        
    }



}
