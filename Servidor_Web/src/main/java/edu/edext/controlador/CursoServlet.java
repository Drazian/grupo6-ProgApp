/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.edext.controlador;

import edu.edext.datatypes.DtCurso;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/CursoServlet")
public class CursoServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        if (accion == null){
            accion = "error"; //Opcion por defecto si no se indica nada.
        }
              
        switch (accion){
            case "buscarCurso":
                String opcion = request.getParameter("opcion"); // "instituto" o "categoria"
                String nombre = request.getParameter("nombre");

                try {
                    List<String> cursos = null;
                    if ("instituto".equalsIgnoreCase(opcion)) {
                        cursos = ic.listarCursosPorInstituto(nombre);
                    } else if ("categoria".equalsIgnoreCase(opcion)) {
                        cursos = ic.listarCursosPorInstituto(nombre); //TO-DO: CREAR listarCursosPorCategoria(nombre)
                    }

                    request.setAttribute("listaCursos", cursos);
                    request.setAttribute("filtroAplicado", opcion + ": " + nombre);
                    request.getRequestDispatcher("fragmentos/listaCursos.jsp").forward(request, response);
                } catch (Exception e) {
                    request.setAttribute("error", e.getMessage());
                    request.getRequestDispatcher("fragmentos/error.jsp").forward(request, response);
                }
                
            break;
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String accion = request.getParameter("accion");
        
        if (accion == null){
            accion = "error"; //Opcion por defecto si no se indica nada.
        }
        
        switch (accion){
        
        }        
        
    }

}
