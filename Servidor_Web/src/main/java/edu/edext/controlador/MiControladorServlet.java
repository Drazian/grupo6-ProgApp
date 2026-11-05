package edu.edext.controlador;

//import edu.edext.datatypes.DtCategoria;
//import edu.edext.datatypes.DtInstituto;
//import edu.edext.logica.Fabrica;
//import edu.edext.logica.IBridgeSOAP;
//import edu.edext.logica.IControlador;

import edu.edext.controlador.ws.DtCategoria;
import edu.edext.controlador.ws.DtInstituto;
import edu.edext.controlador.ws.IBridgeSOAP;

//import edu.edext.modelo.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;
 
@WebServlet("/MiControladorServlet")
public class MiControladorServlet extends HttpServlet {
//    private final IControlador ic = Fabrica.getInstance().getIControlador();
    private final IBridgeSOAP ic = Fabrica.getConexion();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
//        String vistaDestino = "/WEB-INF/fragmentos/error.jsp"; // Por si falla algo

//        if ("productos".equals(accion)) {
            // Simulamos datos de una Base de Datos usando la clase Modelo
//            List<Producto> lista = new ArrayList<>();
//            lista.add(new Producto(101, "Laptop Gamer ASUS", 1200.50));
//            lista.add(new Producto(102, "Mouse Óptico Inalámbrico", 25.00));
//            lista.add(new Producto(103, "Monitor 4K 27 pulgadas", 350.99));

            // Guardamos la lista en el request para que el JSP pueda leerla
//            request.setAttribute("listaProductos", lista);
//            vistaDestino = "/WEB-INF/fragmentos/productos.jsp";
//        }
        
//        else if ("buscar".equals(accion)) {
//            String query = request.getParameter("query");
//            
//            // Simulación básica de filtro
//            String resultadoBusqueda = "No se encontraron resultados para: " + query;
//            if (query != null && !query.trim().isEmpty()) {
//                resultadoBusqueda = "Mostrando resultados en tiempo real para el término: '" + query + "'";
//            }
//            
//            request.setAttribute("mensajeBusqueda", resultadoBusqueda);
//            vistaDestino = "/WEB-INF/fragmentos/resultados.jsp";
//        }
        
//        else if ("abrirFormulario".equals(accion)) {
////            vistaDestino = "/WEB-INF/fragmentos/formulario.jsp";
//        }
        
        //  Obtener Institutos 
        if ("obtenerInstitutosJSON".equals(accion)) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                List<DtInstituto> institutos = ic.listarInstitutos();

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < institutos.size(); i++) {
                    json.append("{\"nombre\":\"")
                        .append(institutos.get(i).getNombre().replace("\"", "\\\""))
                        .append("\"}");
                    if (i < institutos.size() - 1) json.append(",");
                }
                json.append("]");

                response.getWriter().write(json.toString());
            } catch (Exception e) {
                Logger.error(e, "Error al listar institutos");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("[]");
            }
            return;
        }

        //  Obtener Categorías 
        else if ("obtenerCategoriasJSON".equals(accion)) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                List<DtCategoria> categorias = ic.listarCategorias();

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < categorias.size(); i++) {
                    json.append("{\"nombre\":\"")
                        .append(categorias.get(i).getNombre().replace("\"", "\\\""))
                        .append("\"}");
                    if (i < categorias.size() - 1) json.append(",");
                }
                json.append("]");

                response.getWriter().write(json.toString());
            } catch (Exception e) {
                Logger.error(e, "Error al listar categorías");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("[]");
            }
            return;
        }
        
//        request.getRequestDispatcher(vistaDestino).forward(request, response);
    }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
//            throws ServletException, IOException {
//        
//    }
}