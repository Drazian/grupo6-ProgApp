package edu.edext.controlador;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.ArrayList; 
import java.io.IOException;
import edu.edext.tools.Utilsw;
import edu.edext.controlador.ws.DtCurso;
import jakarta.servlet.http.HttpServlet;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.DtPrograma;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletResponse;
import edu.edext.controlador.ws.DtValoracionesCurso;
import jakarta.servlet.http.HttpServletRequest;
import edu.edext.modelo.DtResultadoBusqueda;
import jakarta.servlet.ServletException;

@WebServlet("/BuscadorServlet")
public class BuscadorServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query"); 
        if (query == null){
            query = "";
        }
        String queryLower = query.trim().toLowerCase();
        List<DtResultadoBusqueda> resultados = new ArrayList<>();
        try {
            List<DtPrograma> programas = Fabrica.getConexion().listarProgramas();
            if (programas != null){
                for (DtPrograma prog : programas){
                    String nombre = prog.getNombre() != null ? prog.getNombre() : "";
                    String desc = prog.getDescripcion() != null ? prog.getDescripcion() : "";
                    if (queryLower.isEmpty() || nombre.toLowerCase().contains(queryLower) || desc.toLowerCase().contains(queryLower)){
                        LocalDate fechaReg = prog.getFechaRegistro() != null ? Utilsw.Cast.valueOf(prog.getFechaRegistro()) : LocalDate.now();
                        DtResultadoBusqueda res = new DtResultadoBusqueda(nombre,desc,fechaReg,prog.getImagen(),"programa");
                        resultados.add(res);
                    }
                }
            }      
            Map<String, DtValoracionesCurso> valoraciones = new HashMap<>();
            try {
                for (DtValoracionesCurso v : Fabrica.getConexion().listarResumenesValoraciones())
                    valoraciones.put(v.getCurso(), v);
            } catch (Exception ignored) {}
            List<DtCurso> cursos = Fabrica.getConexion().listarCursos();
            if (cursos != null){
                for (DtCurso curso : cursos){
                    String nombre = curso.getNombre() != null ? curso.getNombre() : "";
                    String desc = curso.getDescripcion() != null ? curso.getDescripcion() : "";
                    if (queryLower.isEmpty() || nombre.toLowerCase().contains(queryLower) || desc.toLowerCase().contains(queryLower)) {
                        LocalDate fechaReg = Utilsw.Cast.valueOf(curso.getFechaRegistro());
                        DtResultadoBusqueda res = new DtResultadoBusqueda(nombre,desc,fechaReg,curso.getImagen(),"curso");
                        DtValoracionesCurso v = valoraciones.get(nombre);
                        if (v != null) res.setValoracion(v.getPromedio(), v.getCantidad());
                        resultados.add(res);
                    }
                }
            }
            List<DtUsuario> usuarios = Fabrica.getConexion().listarUsuarios();
            if (usuarios != null){
                for (DtUsuario user : usuarios){
                    String nickname = user.getNickname() != null ? user.getNickname() : "";
                    String nom = user.getNombre() != null ? user.getNombre() : "";
                    String ape = user.getApellido() != null ? user.getApellido() : "";
                    String tip = user.getTipoUsuario() != null ? user.getTipoUsuario().toString() : "estudiante";
                    String nombre = (nom + " " + ape).trim();
                    if (queryLower.isEmpty() || nickname.toLowerCase().contains(queryLower) || nombre.toLowerCase().contains(queryLower)){
                        LocalDate fechaN = user.getFNacimiento() != null ? Utilsw.Cast.valueOf(user.getFNacimiento()) : LocalDate.now();
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

}