package edu.edext.controlador;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/ProgramaServlet")
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
                
                DtPrograma programa = new DtPrograma(nombre, desc, fechaRegistro, fechaInicio, fechaFin);
                
                try {
                    ic.setCrearProgramaFormacion(programa);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Programa " + nombre + " creado con exito.");
                } catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al crear el programa: " + e.getMessage());                    
                }
                
            break;
            
            case "agregarCurso":
                String programaStr = request.getParameter("programaStr");
                String cursoStr = request.getParameter("cursoStr");
                
                if (programaStr == null || programaStr.trim().isEmpty() || cursoStr == null || cursoStr.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Los datos no puede estar vacío.");
                    return;
                }

                try {
                    ic.agregarProgramaCurso(programaStr, cursoStr);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Curso " + cursoStr + " agregado con exito al programa" + programaStr + ".");
                } catch (Exception e){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al agregar el curso al programa: " + e.getMessage());                    
                }                

            break;
            
            default:
                request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
            break;
        }        
        
        
    }



}
