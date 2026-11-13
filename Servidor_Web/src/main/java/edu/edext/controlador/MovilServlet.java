package edu.edext.controlador;

//import edu.edext.datatypes.DtCategoria;
//import edu.edext.datatypes.DtConsultaCurso;
//import edu.edext.datatypes.DtCurso;
//import edu.edext.datatypes.DtEdicion;
//import edu.edext.datatypes.DtInstituto;
//import edu.edext.datatypes.DtUsuario;
//import edu.edext.datatypes.TipoUsuario;
//import edu.edext.logica.Fabrica;
//import edu.edext.logica.IBridgeSOAP;
//import edu.edext.logica.IControlador;
 
import edu.edext.controlador.ws.DtCategoria;
import edu.edext.controlador.ws.DtConsultaCurso;
import edu.edext.controlador.ws.DtCurso;
import edu.edext.controlador.ws.DtEdicion;
import edu.edext.controlador.ws.DtInstituto;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.IBridgeSOAP;
import edu.edext.controlador.ws.TipoUsuario;

import edu.edext.modelo.RecordarmeMovil;
import edu.edext.tools.Utils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.tinylog.Logger;

/**
 * Dispositivo Móvil (6.2 y 7.1): aplicación web con Bootstrap (Responsive Web Design) dentro del mismo Servidor Web.
 * La letra permite que esté en el mismo servidor; se accede en /web/movil.
 *
 *   /movil, /movil/login  -> Inicio de Sesión (nick o mail + contraseña, con Recordarme). Página inicial.
 *   /movil/inicio         -> Bienvenida con la barra de navegación.
 *   /movil/cursos         -> Consulta de Curso: Instituto o Categoría -> grilla de cursos.
 *   /movil/curso          -> Datos del curso, sus ediciones y programas, y las valoraciones.
 *   /movil/ediciones      -> Consulta de Edición: Instituto o Categoría -> cursos -> grilla de ediciones del curso.
 *   /movil/edicion        -> Datos de la edición y sus docentes.
 *   /movil/salir          -> Cierre de Sesión.
 *
 * Los casos de uso son del Estudiante: solo pueden iniciar sesión estudiantes.
 */
@WebServlet(urlPatterns = {"/movil", "/movil/", "/movil/login", "/movil/inicio", "/movil/salir",
                           "/movil/cursos", "/movil/curso", "/movil/ediciones", "/movil/edicion"})
public class MovilServlet extends HttpServlet {
    private final IBridgeSOAP ic = Fabrica.getConexion();
//    private final IControlador ic = Fabrica.getInstance().getIControlador();
    private final LoginHelper login = new LoginHelper();

    private static final String VISTAS = "/WEB-INF/movil/";
    // Las fechas llegan del Servidor Central como texto dd/MM/yyyy; la fecha de registro del curso se muestra corta (Figura 3)
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yy");

    // Versión de los recursos estáticos (?v=): cambia en cada despliegue. El war se arma con fecha fija (1980)
    // y sin esto el navegador sigue usando el CSS/JS viejo después de redesplegar.
    @Override
    public void init() throws ServletException {
        getServletContext().setAttribute("versionRecursosMovil", System.currentTimeMillis());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String ruta = request.getServletPath();
        DtUsuario estudiante = estudianteLogueado(request);

        // La página inicial es la de login
        if ("/movil".equals(ruta) || "/movil/".equals(ruta) || "/movil/login".equals(ruta)) {
            if (estudiante != null) redirigir(request, response, "/movil/inicio");
            else vista(request, response, "login.jsp");
            return;
        }
        if ("/movil/salir".equals(ruta)) {
            salir(request, response);
            return;
        }
        if (estudiante == null) {
            redirigir(request, response, "/movil/login");
            return;
        }

        request.setAttribute("saludo", saludo(estudiante));
        try {
            switch (ruta) {
                case "/movil/inicio":
                    request.setAttribute("seccion", "inicio");
                    vista(request, response, "inicio.jsp");
                    break;
                case "/movil/cursos":
                    request.setAttribute("seccion", "cursos");
                    listarCursos(request);
                    vista(request, response, "cursos.jsp");
                    break;
                case "/movil/curso":
                    request.setAttribute("seccion", "cursos");
                    detalleCurso(request, response, estudiante);
                    break;
                case "/movil/ediciones":
                    request.setAttribute("seccion", "ediciones");
                    if (request.getParameter("curso") != null) {
                        edicionesDelCurso(request, response);
                    } else {
                        listarCursos(request);
                        contarEdiciones(request);
                        vista(request, response, "cursos.jsp");
                    }
                    break;
                case "/movil/edicion":
                    request.setAttribute("seccion", "ediciones");
                    detalleEdicion(request, response);
                    break;
                default:
                    redirigir(request, response, "/movil/inicio");
            }
        } catch (Exception e) {
            Logger.error(e, "Error en el Dispositivo Móvil ({}): ", ruta);
            mensaje(request, response, "Ocurrió un error al consultar los datos. Intente nuevamente.");
        }
    }

    // ----------------------------- Inicio de Sesión -----------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!"/movil/login".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String identificador = request.getParameter("identificador");
        String password = request.getParameter("password");
        boolean recordarme = request.getParameter("recordarme") != null;

        DtUsuario usuario = autenticar(identificador, password);
        if (usuario == null) {
            loginConError(request, response, identificador, "Usuario o contraseña incorrectos.");
            return;
        }
        if (usuario.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
            loginConError(request, response, identificador, "La aplicación móvil es para estudiantes. Los docentes pueden entrar al sitio web con \"Ver versión de escritorio\".");
            return;
        }

        HttpSession session = request.getSession();
        request.changeSessionId();   // evita fijación de sesión
        session.setAttribute("usuarioLogueado", usuario);
        if (recordarme)
            response.addCookie(cookieRecordarme(request, RecordarmeMovil.crearToken(usuario.getNickname()), RecordarmeMovil.DURACION_SEGUNDOS));
        redirigir(request, response, "/movil/inicio");
    }

    // Misma validación que el login del sitio web: por email o por nickname
    private DtUsuario autenticar(String identificador, String password) {
        if (Utils.Validate.isEmpty(identificador) || Utils.Validate.isEmpty(password)) return null;
        identificador = identificador.trim();
        if (Utils.Validate.isEmail(identificador))
            return login.isExistEmail(identificador) ? login.validarEmail(identificador, password) : null;
        return login.isExistNick(identificador) ? login.validarNick(identificador, password) : null;
    }

    private void loginConError(HttpServletRequest request, HttpServletResponse response, String identificador, String error)
            throws ServletException, IOException {
        request.setAttribute("errorLogin", error);
        request.setAttribute("identificador", identificador);
        vista(request, response, "login.jsp");
    }

    // ----------------------------- Cierre de Sesión -----------------------------
    private void salir(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.addCookie(cookieRecordarme(request, "", 0));   // se borra el Recordarme
        redirigir(request, response, "/movil/login");
    }

    // Estudiante de la sesión; si no hay sesión pero sí cookie de Recordarme válida, la recupera
    private DtUsuario estudianteLogueado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object usuario = session != null ? session.getAttribute("usuarioLogueado") : null;
        if (usuario instanceof DtUsuario)
            return ((DtUsuario) usuario).getTipoUsuario() == TipoUsuario.ESTUDIANTE ? (DtUsuario) usuario : null;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (!RecordarmeMovil.COOKIE.equals(c.getName())) continue;
                String nickname = RecordarmeMovil.nicknameDelToken(c.getValue());
                if (nickname == null) return null;
                try {
                    DtUsuario recordado = ic.obtenerUsuario(nickname);
                    if (recordado != null && recordado.getTipoUsuario() == TipoUsuario.ESTUDIANTE) {
                        request.getSession().setAttribute("usuarioLogueado", recordado);
                        return recordado;
                    }
                } catch (Exception e) {
                    Logger.warn(e, "No se pudo recuperar la sesión recordada de {}", nickname);
                }
                return null;
            }
        }
        return null;
    }

    private Cookie cookieRecordarme(HttpServletRequest request, String valor, int maxAge) {
        Cookie cookie = new Cookie(RecordarmeMovil.COOKIE, valor);
        cookie.setPath(request.getContextPath() + "/movil");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

    // ----------------------------- Consulta de Curso -----------------------------
    // Instituto o Categoría -> cursos asociados (sin filtro se muestran todos)
    private void listarCursos(HttpServletRequest request) throws Exception {
        String instituto = vacioANull(request.getParameter("instituto"));
        String categoria = instituto == null ? vacioANull(request.getParameter("categoria")) : null;

        Set<String> filtro = null;
        if (instituto != null) filtro = new HashSet<>(ic.listarCursosPorInstituto(instituto));
        else if (categoria != null) filtro = new HashSet<>(ic.listarCursosPorCategoria(categoria));

        List<DtCurso> cursos = new ArrayList<>();
        for (DtCurso c : ic.listarCursos())
            if (filtro == null || filtro.contains(c.getNombre())) cursos.add(c);
        cursos.sort(Comparator.comparing(DtCurso::getNombre, String.CASE_INSENSITIVE_ORDER));

        List<String> institutos = new ArrayList<>();
        for (DtInstituto i : ic.listarInstitutos()) institutos.add(i.getNombre());
        institutos.sort(String.CASE_INSENSITIVE_ORDER);
        List<String> categorias = new ArrayList<>();
        for (DtCategoria c : ic.listarCategorias()) categorias.add(c.getNombre());
        categorias.sort(String.CASE_INSENSITIVE_ORDER);

        request.setAttribute("cursos", cursos);
        request.setAttribute("institutos", institutos);
        request.setAttribute("categorias", categorias);
        request.setAttribute("filtroInstituto", instituto);
        request.setAttribute("filtroCategoria", categoria);
    }

    // "Ver Ediciones": cantidad de ediciones de cada curso listado, para mostrarla debajo del nombre
    @SuppressWarnings("unchecked")
    private void contarEdiciones(HttpServletRequest request) throws Exception {
        Map<String, Integer> cantidad = new HashMap<>();
        for (DtCurso c : (List<DtCurso>) request.getAttribute("cursos")) {
            List<String> ediciones = ic.listaEdicionPorCurso(c.getNombre());
            cantidad.put(c.getNombre(), ediciones == null ? 0 : ediciones.size());
        }
        request.setAttribute("cantidadEdiciones", cantidad);
    }

    private void detalleCurso(HttpServletRequest request, HttpServletResponse response, DtUsuario estudiante) throws Exception {
        String nombre = request.getParameter("nombre");
        DtConsultaCurso curso = nombre == null ? null : ic.obtenerDatosCurso(nombre);
        if (curso == null) {
            mensaje(request, response, "El curso no existe.");
            return;
        }
        request.setAttribute("curso", curso);
        request.setAttribute("fechaRegistro", fechaCorta(curso.getFechaRegistro()));
        request.setAttribute("ediciones", edicionesOrdenadas(curso));
        try {
            request.setAttribute("instituto", ic.obtenerInstitutoPorCurso(nombre));
        } catch (Exception ignorada) { /* el detalle se muestra igual */ }
        try {
            // Opcional en la letra: valoraciones del curso (mismo resumen que el sitio web)
            request.setAttribute("valoraciones", ic.getValoracionesCurso(nombre, estudiante.getNickname()));
        } catch (Exception ignorada) { /* el detalle se muestra igual */ }
        vista(request, response, "curso.jsp");
    }

    // ------------------------- Consulta de Edición de curso -------------------------
    private void edicionesDelCurso(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre = request.getParameter("curso");
        DtConsultaCurso curso = ic.obtenerDatosCurso(nombre);
        if (curso == null) {
            mensaje(request, response, "El curso no existe.");
            return;
        }
        request.setAttribute("curso", curso);
        request.setAttribute("ediciones", edicionesOrdenadas(curso));
        vista(request, response, "ediciones.jsp");
    }

    private void detalleEdicion(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre = request.getParameter("nombre");
        DtEdicion edicion = nombre == null ? null : ic.getEdicion(nombre);
        if (edicion == null) {
            mensaje(request, response, "La edición no existe.");
            return;
        }
        request.setAttribute("edicion", edicion);
        vista(request, response, "edicion.jsp");
    }

    // Ediciones del curso, de la más nueva a la más vieja
    private static List<DtEdicion> edicionesOrdenadas(DtConsultaCurso curso) {
        List<DtEdicion> ediciones = new ArrayList<>(curso.getEdiciones() != null ? curso.getEdiciones() : List.of());
        // La fecha llega como texto dd/MM/yyyy: se ordena por la fecha, no por el texto ("02/08/2026" va antes que "15/03/2025")
        ediciones.sort(Comparator.comparing((DtEdicion e) -> Utils.Cast.valueOf(e.getFechaInicio()),
                Comparator.nullsLast(Comparator.reverseOrder())));
        return ediciones;
    }

    // "15/03/2025" -> "15/03/25"; si no se puede interpretar se muestra tal cual
    private static String fechaCorta(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        LocalDate f = Utils.Cast.valueOf(fecha);
        return f == null ? fecha : f.format(FECHA_CORTA);
    }

    // ----------------------------- auxiliares -----------------------------
    // "Bienvenido Roro": el nickname con la primera letra en mayúscula, como en la Figura 3
    private static String saludo(DtUsuario usuario) {
        String nick = usuario.getNickname();
        return nick == null || nick.isEmpty() ? "" : Character.toUpperCase(nick.charAt(0)) + nick.substring(1);
    }

    private static String vacioANull(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }

    private void mensaje(HttpServletRequest request, HttpServletResponse response, String texto) throws ServletException, IOException {
        request.setAttribute("mensaje", texto);
        vista(request, response, "mensaje.jsp");
    }

    private void vista(HttpServletRequest request, HttpServletResponse response, String jsp) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        request.getRequestDispatcher(VISTAS + jsp).forward(request, response);
    }

    private static void redirigir(HttpServletRequest request, HttpServletResponse response, String ruta) throws IOException {
        response.sendRedirect(request.getContextPath() + ruta);
    }
}
