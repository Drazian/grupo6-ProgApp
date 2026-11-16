package edu.edext.controlador;

import org.tinylog.Logger;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/CursoServlet", "/movil/curso"})
public class VisitasCursoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String curso = "GET".equals(request.getMethod()) ? cursoConsultado(request) : null;
        chain.doFilter(req, res);
        if (curso != null && response.getStatus() == HttpServletResponse.SC_OK) {
            try {
                Fabrica.getConexion().registrarVisitaCurso(curso);
            } catch (Exception e) {
                Logger.warn(e, "No se pudo registrar la visita al curso {}", curso);
            }
        }
    }

    private static String cursoConsultado(HttpServletRequest request) {
        String nombre = null;
        if ("/CursoServlet".equals(request.getServletPath())) {
            if ("verDetallesCurso".equals(request.getParameter("accion")))
                nombre = request.getParameter("nombreCurso");
        } else if ("/movil/curso".equals(request.getServletPath())) {
            nombre = request.getParameter("nombre");
        }
        if (nombre == null || nombre.isBlank()) return null;
        if (nombre.contains("Ã"))
            nombre = new String(nombre.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return nombre;
    }
}