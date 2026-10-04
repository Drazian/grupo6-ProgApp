/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.edext.controlador;

import edu.edext.datatypes.DtInstituto;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import edu.edext.logica.IControlador;
import edu.edext.logica.Fabrica;
import java.util.List;

/**
 * //Servlet unicamente para practica, no va en el producto final
 * @author pipo
 */

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String mensaje = "Hola mundo";
//        //Guardar
//        request.setAttribute("mensaje", mensaje);
//        //Mostrar
//        request.getRequestDispatcher("/fragmentos/test.jsp").forward(request, response);

        //Testear listarInstitutos
        
        try {        
            List<DtInstituto> institutos = ic.listarInstitutos();
            request.setAttribute("institutos", institutos);
        } catch (Exception e){
            request.setAttribute("error", e.getMessage());
        }
        
        request.getRequestDispatcher("fragmentos/test.jsp").forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String nombre = request.getParameter("nombre");

        //Si esta vacio paramos.
        if (nombre == null || nombre.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("El nombre del instituto no puede estar vacío.");
            return;
        }
        
        try{
            ic.crearInstituto(nombre);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Insituto " + nombre + " creado con exito.");
        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al crear instituto: " + e.getMessage());
    }

        
    }



}
