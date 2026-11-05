package edu.edext.controlador;

import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.DtValoracionesCurso;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IBridgeSOAP;
import edu.edext.logica.IControlador;

//import edu.edext.controlador.ws.DtUsuario;
//import edu.edext.controlador.ws.DtValoracionesCurso;
//import edu.edext.controlador.ws.IBridgeSOAP;
//import edu.edext.controlador.ws.TipoUsuario;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CU "Valorar un Curso" (Estudiante).
 * El estudiante elige uno de los cursos en los que participa, ve el puntaje promedio y el suyo (si ya valoro)
 * y asigna un puntaje de 1 a 5. Cada estudiante puede valorar un curso una unica vez.
 */
@WebServlet("/ValoracionServlet")
public class ValoracionServlet extends HttpServlet {
//    private final IBridgeSOAP ic = Fabrica.getConexion();
    private final IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        DtUsuario estudiante = estudianteLogueado(request);
        if (estudiante == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los Estudiantes pueden valorar cursos.");
            return;
        }

        String accion = request.getParameter("accion");
        if ("formulario".equals(accion)) {
            // Lista de cursos en los que participa el estudiante (inscripcion aceptada en alguna edicion)
            try {
                request.setAttribute("cursosParticipa", ic.listarCursosParaValorar(estudiante.getNickname()));
                request.setAttribute("cursoPreseleccionado", request.getParameter("curso"));
                request.getRequestDispatcher("/WEB-INF/fragmentos/valorarCurso.jsp").forward(request, response);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else if ("detalle".equals(accion)) {
            // Puntaje promedio del curso, resumen de valoraciones y el puntaje del estudiante (si ya valoro)
            try {
                DtValoracionesCurso valoraciones = ic.getValoracionesCurso(request.getParameter("curso"), estudiante.getNickname());
                if (!valoraciones.isParticipa())
                    request.setAttribute("errorValoracion", "Solo puede valorar cursos en los que participa (inscripción aceptada en alguna edición).");
                request.setAttribute("valoraciones", valoraciones);
            } catch (Exception e) {
                request.setAttribute("errorValoracion", e.getMessage());
            }
            request.getRequestDispatcher("/WEB-INF/fragmentos/detalleValoracion.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        DtUsuario estudiante = estudianteLogueado(request);
        if (estudiante == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("La sesión expiró o no tienes permisos de Estudiante.");
            return;
        }
        if (!"valorar".equals(request.getParameter("accion"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Acción no válida.");
            return;
        }

        int puntaje;
        try {
            puntaje = Integer.parseInt(request.getParameter("puntaje").trim());
        } catch (NullPointerException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Debe elegir un puntaje de 1 a 5.");
            return;
        }

        try {
            // El Central valida el puntaje, que el estudiante participe del curso y que no lo haya valorado antes
            ic.valorarCurso(estudiante.getNickname(), request.getParameter("curso"), puntaje);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("¡Gracias! Su valoración quedó registrada.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        }
    }

    private static DtUsuario estudianteLogueado(HttpServletRequest request) {
        Object usuario = request.getSession().getAttribute("usuarioLogueado");
        if (usuario instanceof DtUsuario && ((DtUsuario) usuario).getTipoUsuario() == TipoUsuario.ESTUDIANTE)
            return (DtUsuario) usuario;
        return null;
    }
}
