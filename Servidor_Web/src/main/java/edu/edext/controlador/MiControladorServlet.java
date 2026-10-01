package edu.edext.controlador;

import edu.edext.modelo.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

// Nota: Si usas una versión muy antigua de Java EE, cambia "jakarta" por "javax"
@WebServlet("/MiControladorServlet")
public class MiControladorServlet extends HttpServlet {

    // Método principal que recibe peticiones GET (clics del menú y búsquedas)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String vistaDestino = "/fragmentos/error.jsp"; // Por si falla algo

        // CASO 1: Listar productos desde el Menú Lateral
        if ("productos".equals(accion)) {
            // Simulamos datos de una Base de Datos usando la clase Modelo
            List<Producto> lista = new ArrayList<>();
            lista.add(new Producto(101, "Laptop Gamer ASUS", 1200.50));
            lista.add(new Producto(102, "Mouse Óptico Inalámbrico", 25.00));
            lista.add(new Producto(103, "Monitor 4K 27 pulgadas", 350.99));

            // Guardamos la lista en el request para que el JSP pueda leerla
            request.setAttribute("listaProductos", lista);
            vistaDestino = "/fragmentos/productos.jsp";
        }
        
        // CASO 2: Procesar la Barra de Búsqueda del Sector 1
        else if ("buscar".equals(accion)) {
            String query = request.getParameter("query");
            
            // Simulación básica de filtro
            String resultadoBusqueda = "No se encontraron resultados para: " + query;
            if (query != null && !query.trim().isEmpty()) {
                resultadoBusqueda = "Mostrando resultados en tiempo real para el término: '" + query + "'";
            }
            
            request.setAttribute("mensajeBusqueda", resultadoBusqueda);
            vistaDestino = "/fragmentos/resultados.jsp";
        }
        
        // CASO 3: Cargar un formulario interactivo
        else if ("abrirFormulario".equals(accion)) {
            vistaDestino = "/fragmentos/formulario.jsp";
        }
        
        else if("reportes".equals(accion)) vistaDestino="/fragmentos/Central-01-10-2026.log";
        
        // Despachador: Toma los datos procesados y renderiza el JSP correspondiente
        request.getRequestDispatcher(vistaDestino).forward(request, response);
    }

    // Método que procesa los envíos de datos (POST) como guardar un formulario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Ejemplo de cómo recibir datos de un formulario asíncrono
        String nombreNuevo = request.getParameter("nombreProd");
        String precioNuevo = request.getParameter("precioProd");

        // Aquí iría tu código para guardar en Base de Datos (DAO)
        System.out.println("Guardando producto: " + nombreNuevo + " - $" + precioNuevo);

        // Respondemos un texto simple confirmando el éxito para que AJAX lo muestre
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<div class='alerta-exito'>¡Producto '" + nombreNuevo + "' guardado exitosamente!</div>");
    }
}

