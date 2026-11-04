package edu.edext.controlador;

//import edu.edext.controlador.ws.DtCurso;
//import edu.edext.controlador.ws.DtPrograma;
//import edu.edext.controlador.ws.DtUsuario;
//import edu.edext.controlador.ws.IBridgeSOAP;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IBridgeSOAP;
import edu.edext.logica.IControlador;

import edu.edext.modelo.DtResultadoBusqueda;
import edu.edext.modelo.Utils;
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
      private final IControlador ic = Fabrica.getInstance().getIControlador();
//    private final IBridgeSOAP ic = Fabrica.getConexion();
    
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

                    // ************************* Descomentar para ejecutar ************************
                        LocalDate fechaReg = prog.getFechaRegistro() != null ? prog.getFechaRegistro() : LocalDate.now();
                    // ************************** Comentar para compilar **************************
//                        LocalDate fechaReg = prog.getFechaRegistro() != null ? Utils.Cast.valueOf(prog.getFechaRegistro()) : LocalDate.now();
                    // ****************************************************************************

                        DtResultadoBusqueda res = new DtResultadoBusqueda(nombre,desc,fechaReg,prog.getImagen(),"programa");
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
                        
                    // ************************* Descomentar para ejecutar ************************
                        LocalDate fechaReg = curso.getFechaRegistro();
                    // ************************** Comentar para compilar **************************
//                        LocalDate fechaReg = Utils.Cast.valueOf(curso.getFechaRegistro());
                    // ****************************************************************************
                        DtResultadoBusqueda res = new DtResultadoBusqueda(nombre,desc,fechaReg,curso.getImagen(),"curso");
                        resultados.add(res);
                    }
                }
            }
            
            List<DtUsuario> usuarios = ic.listarUsuarios();
            
            if (usuarios != null){
                for (DtUsuario user : usuarios){
                    String nickname = user.getNickname() != null ? user.getNickname() : "";
                    String nom = user.getNombre() != null ? user.getNombre() : "";
                    String ape = user.getApellido() != null ? user.getApellido() : "";
//                    String tip = user.getTipoUsuario().toString();
                    String tip = user.getTipoUsuario() != null ? user.getTipoUsuario().toString() : "estudiante";
                    String nombre = (nom + " " + ape).trim();
                    
                    if (queryLower.isEmpty() || nickname.toLowerCase().contains(queryLower) || nombre.toLowerCase().contains(queryLower)){
                    // ************************* Descomentar para ejecutar ************************
                        LocalDate fechaN = user.getfNacimiento() != null ? user.getfNacimiento() : LocalDate.now();
                    // ************************** Comentar para compilar **************************
//                        LocalDate fechaN = user.getFNacimiento() != null ? Utils.Cast.valueOf(user.getFNacimiento()) : LocalDate.now();
                    // ****************************************************************************
                        DtResultadoBusqueda res = new DtResultadoBusqueda(nickname, nombre, fechaN, user.getImagen(),tip.toLowerCase());
                        resultados.add(res);
                    }
                }
                    
            }
            
            request.setAttribute("resultadosBusqueda", resultados);
            request.setAttribute("queryBuscada", query);
            request.getRequestDispatcher("/WEB-INF/fragmentos/busqueda.jsp").forward(request, response);
        
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/fragmentos/error.jsp").forward(request, response);
        }
        
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

//    //Funcion auxiliar para convertir los Date de DtCurso a LocalDate y asi unificar el formato de fecha con DtPrograma.
//    private LocalDate DateToLocalDate(Date date){
//        if (date == null){
//            return LocalDate.now();
//        } else if (date instanceof java.sql.Date) {
//            return ((java.sql.Date) date).toLocalDate();
//        } else {
//            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        }
//    }
    
}
