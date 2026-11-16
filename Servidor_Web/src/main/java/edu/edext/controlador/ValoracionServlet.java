package edu.edext.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.TipoUsuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.edext.controlador.ws.DtValoracionesCurso;
import java.io.IOException;

@WebServlet("/ValoracionServlet")
public class ValoracionServlet extends HttpServlet {

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
            try {
                request.setAttribute("cursosParticipa", Fabrica.getConexion().listarCursosParaValorar(estudiante.getNickname()));
                request.setAttribute("cursoPreseleccionado", request.getParameter("curso"));
                request.getRequestDispatcher("/WEB-INF/fragmentos/valorarCurso.jsp").forward(request, response);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

        } else if ("detalle".equals(accion)) {
            try {
                DtValoracionesCurso valoraciones = Fabrica.getConexion().getValoracionesCurso(request.getParameter("curso"), estudiante.getNickname());
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
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Debe elegir un puntaje de 1 a 5.");
            return;
        }

        try {
            // El Central valida el puntaje, que el estudiante participe del curso y que no lo haya valorado antes
            Fabrica.getConexion().valorarCurso(estudiante.getNickname(), request.getParameter("curso"), puntaje);
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