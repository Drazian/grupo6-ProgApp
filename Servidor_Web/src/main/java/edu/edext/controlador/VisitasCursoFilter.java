package edu.edext.controlador;

//import edu.edext.logica.Fabrica;
//import edu.edext.logica.IBridgeSOAP;
//import edu.edext.logica.IControlador;

import edu.edext.controlador.ws.IBridgeSOAP;
 
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

/**
 * Requerimiento 7.3 "Listado de tendencias": filtro que cuenta las consultas de cursos.
 * Intercepta las dos formas de ver el detalle de un curso:
 *   - Sitio web:         CursoServlet?accion=verDetallesCurso&nombreCurso=...
 *   - Dispositivo móvil: /movil/curso?nombre=...
 * Deja pasar la petición y, si el detalle se mostró bien (HTTP 200), registra una visita en el Servidor Central.
 * Cuentan tanto usuarios logueados como visitantes. Si registrar la visita falla, la página se muestra igual.
 */
@WebFilter(urlPatterns = {"/CursoServlet", "/movil/curso"})
public class VisitasCursoFilter implements Filter {
    private final IBridgeSOAP ic = Fabrica.getConexion();
//    private final IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Solo GET: así no se leen parámetros de formularios POST (alta de curso multipart) antes que el servlet
        String curso = "GET".equals(request.getMethod()) ? cursoConsultado(request) : null;

        chain.doFilter(req, res);

        if (curso != null && response.getStatus() == HttpServletResponse.SC_OK) {
            try {
                ic.registrarVisitaCurso(curso);
            } catch (Exception e) {
                Logger.warn(e, "No se pudo registrar la visita al curso {}", curso);
            }
        }
    }

    // Nombre del curso si la petición es una consulta del detalle de un curso; si no, null
    private static String cursoConsultado(HttpServletRequest request) {
        String nombre = null;
        if ("/CursoServlet".equals(request.getServletPath())) {
            if ("verDetallesCurso".equals(request.getParameter("accion")))
                nombre = request.getParameter("nombreCurso");
        } else if ("/movil/curso".equals(request.getServletPath())) {
            nombre = request.getParameter("nombre");
        }
        if (nombre == null || nombre.isBlank()) return null;
        // Misma corrección que hace CursoServlet cuando el nombre llega mal codificado
        if (nombre.contains("Ã"))
            nombre = new String(nombre.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return nombre;
    }
}
