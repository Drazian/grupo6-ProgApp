package edu.edext.controlador;

import edu.edext.datatypes.DtCursoVisitas;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IBridgeSOAP;
import edu.edext.logica.IControlador;

//import edu.edext.controlador.ws.DtCursoVisitas;
//import edu.edext.controlador.ws.IBridgeSOAP;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Requerimiento 7.3 "Listado de tendencias": lista de cursos más visitados, ordenados de mayor a menor cantidad de visitas.
 * Disponible para usuarios y visitantes. Las visitas las registra VisitasCursoFilter.
 */
@WebServlet("/TendenciasServlet")
public class TendenciasServlet extends HttpServlet {
//    private final IBridgeSOAP ic = Fabrica.getConexion();
    private final IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<DtCursoVisitas> tendencias = ic.listarCursosMasVisitados();
            long total = 0, maximo = 0;
            for (DtCursoVisitas t : tendencias) {
                total += t.getVisitas();
                maximo = Math.max(maximo, t.getVisitas());
            }
            request.setAttribute("tendencias", tendencias);
            request.setAttribute("totalVisitas", total);
            request.setAttribute("maximoVisitas", maximo);
            request.getRequestDispatcher("/WEB-INF/fragmentos/tendencias.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener el listado de tendencias: " + e.getMessage());
        }
    }
}
