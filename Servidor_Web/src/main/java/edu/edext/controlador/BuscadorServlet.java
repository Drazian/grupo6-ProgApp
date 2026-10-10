package edu.edext.controlador;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtPrograma;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import edu.edext.modelo.DtResultadoBusqueda;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/BuscadorServlet")
public class BuscadorServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query"); 
        
        if (query == null){
            query = ""; //Opcion por defecto si no se indica nada.
        }
        
        String queryLower = query.trim().toLowerCase();
        
        List<DtResultadoBusqueda> resultados = new ArrayList<>();
        
        try {
            List<DtPrograma> programas = ic.listarProgramas();
            if (programas != null){
                for (DtPrograma prog : programas){
                    String nombre = prog.getNombre() != null ? prog.getNombre() : "";
                    String desc = prog.getDescripcion() != null ? prog.getDescripcion() : "";
                    
                    if (queryLower.isEmpty() || nombre.toLowerCase().contains(queryLower) || desc.toLowerCase().contains(queryLower)){
                        LocalDate fechaReg = prog.getFechaRegistro() != null ? prog.getFechaRegistro() : LocalDate.now();
                        DtResultadoBusqueda res = new DtResultadoBusqueda(nombre,desc,fechaReg,"","programa");
                        resultados.add(res);
                    }
                }
            }      
            
            List<DtCurso> cursos = ic.listarCursos();
            if (cursos != null){
                for (DtCurso curso : cursos){
                    String nombre = curso.getNombre() != null ? curso.getNombre() : "";
                    String desc = curso.getDescripcion() != null ? curso.getDescripcion() : "";
                    
                    if (queryLower.isEmpty() || nombre.toLowerCase().contains(queryLower) || desc.toLowerCase().contains(queryLower)) {
                        LocalDate fechaReg = DateToLocalDate(curso.getFechaRegistro()); //Conversion a LocalDate para unificar.
                        DtResultadoBusqueda res = new DtResultadoBusqueda(nombre,desc,fechaReg,"","curso");
                        resultados.add(res);
                    }
                }
            }
            
            request.setAttribute("resultadosBusqueda", resultados);
            request.setAttribute("queryBuscada", query);
            request.getRequestDispatcher("/fragmentos/busqueda.jsp").forward(request, response);
        
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }
        
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    //Funcion auxiliar para convertir los Date de DtCurso a LocalDate y asi unificar el formato de fecha con DtPrograma.
    private LocalDate DateToLocalDate(Date date){
        if (date == null){
            return LocalDate.now();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }
    
}
