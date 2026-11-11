package edu.edext.controlador;

import java.io.IOException;
import java.util.regex.Pattern;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Puerta única de entrada (Requerimiento 7.1: el Dispositivo Móvil puede ser "accesible desde la misma URL" que el sitio).
 * Siempre se entra por /web/. Si quien entra es un celular o una tableta (Android, iPhone, iPad, iPod...), se lo lleva
 * a la aplicación móvil (/web/movil/). Desde una computadora se muestra el sitio web, como siempre.
 *
 *   /web/?version=escritorio -> sitio web aunque se entre desde un celular (por ejemplo un docente: la aplicación móvil
 *                               es solo para estudiantes). La elección queda guardada en la sesión.
 *   /web/?version=movil      -> aplicación móvil, y se olvida la elección anterior.
 *
 * El iPad con iPadOS se presenta como una Mac y desde acá no se distingue: a ese caso lo detecta index.jsp en el
 * navegador, por la pantalla táctil.
 */
@WebFilter(urlPatterns = {"/index.jsp"})
public class DispositivoMovilFilter implements Filter {
    /** Atributo de sesión: el usuario eligió ver el sitio web desde un celular o tableta. */
    public static final String VERSION_ESCRITORIO = "versionEscritorio";

    // Navegadores de celulares y tabletas. Un Android sin "Mobile" es una tableta: también va a la versión móvil.
    private static final Pattern MOVIL = Pattern.compile(
            "Android|iPhone|iPad|iPod|Mobile|Windows Phone|IEMobile|BlackBerry|BB10|Opera Mini|Kindle|Silk",
            Pattern.CASE_INSENSITIVE);

    /** true si la petición viene de un celular o tableta. */
    public static boolean esDispositivoMovil(HttpServletRequest request) {
        // Client Hint de los navegadores basados en Chromium (Chrome, Edge, Samsung Internet): "?1" en celulares
        if ("?1".equals(request.getHeader("Sec-CH-UA-Mobile"))) return true;
        String agente = request.getHeader("User-Agent");
        return agente != null && MOVIL.matcher(agente).find();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!"GET".equals(request.getMethod()) && !"HEAD".equals(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }
        // La página depende del dispositivo: que el navegador o un proxy no reutilice la respuesta de otro
        response.addHeader("Vary", "User-Agent");

        String inicio = request.getContextPath() + "/";
        String version = request.getParameter("version");
        if ("escritorio".equals(version)) {
            request.getSession().setAttribute(VERSION_ESCRITORIO, Boolean.TRUE);
            response.sendRedirect(inicio);   // vuelve a /web/, sin el parámetro
            return;
        }
        if ("movil".equals(version)) {
            HttpSession sesion = request.getSession(false);
            if (sesion != null) sesion.removeAttribute(VERSION_ESCRITORIO);
            response.sendRedirect(inicio + "movil/");
            return;
        }

        HttpSession sesion = request.getSession(false);
        boolean eligioEscritorio = sesion != null && Boolean.TRUE.equals(sesion.getAttribute(VERSION_ESCRITORIO));
        if (!eligioEscritorio && esDispositivoMovil(request)) {
            response.sendRedirect(inicio + "movil/");
            return;
        }
        chain.doFilter(req, res);
    }
}
