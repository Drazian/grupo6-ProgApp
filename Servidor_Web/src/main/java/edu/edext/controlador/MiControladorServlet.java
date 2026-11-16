package edu.edext.controlador;

import java.util.List;
import org.tinylog.Logger;
import java.io.IOException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import edu.edext.controlador.ws.DtCategoria;
import edu.edext.controlador.ws.DtInstituto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@WebServlet("/MiControladorServlet")
public class MiControladorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
//        String vistaDestino = "/WEB-INF/fragmentos/error.jsp"; // Por si falla algo

        //  Obtener Institutos 
        if ("obtenerInstitutosJSON".equals(accion)) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                List<DtInstituto> institutos = Fabrica.getConexion().listarInstitutos();

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
                List<DtCategoria> categorias = Fabrica.getConexion().listarCategorias();

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