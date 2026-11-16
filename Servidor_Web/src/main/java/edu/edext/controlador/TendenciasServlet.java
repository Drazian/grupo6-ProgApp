package edu.edext.controlador;

import java.util.List;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import edu.edext.controlador.ws.DtCursoVisitas;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TendenciasServlet")
public class TendenciasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<DtCursoVisitas> tendencias = Fabrica.getConexion().listarCursosMasVisitados();
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