package edu.edext.controlador;

import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@WebServlet("/InscripcionServlet")
public class InscripcionServlet extends HttpServlet {
    private IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "error";

        switch (accion) {
            case "formulario":
                // 1. Barrera de Seguridad: Solo estudiantes
                HttpSession session = request.getSession();
                DtUsuario usuario = (DtUsuario) session.getAttribute("usuarioLogueado");
                
                if (usuario == null || usuario.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo los Estudiantes pueden realizar inscripciones.");
                    return;
                }

                try {
                    request.setAttribute("institutos", ic.listarInstitutos());
                    request.getRequestDispatcher("fragmentos/inscripcionEdicion.jsp").forward(request, response);
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                break;

            case "listarEdiciones":
                // Devuelve un JSON con las ediciones de un curso seleccionado
                try {
                    String curso = request.getParameter("curso");
                    List<String> ediciones = ic.listaEdicionPorCurso(curso);
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < ediciones.size(); i++) {
                        json.append("\"").append(ediciones.get(i)).append("\"");
                        if (i < ediciones.size() - 1) json.append(",");
                    }
                    json.append("]");

                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write(json.toString());
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo ediciones.");
                }
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String accion = request.getParameter("accion");
        if ("inscribir".equals(accion)) {
            HttpSession session = request.getSession();
            DtUsuario estudiante = (DtUsuario) session.getAttribute("usuarioLogueado");
            
            if (estudiante == null || estudiante.getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("La sesión expiró o no tienes permisos de Estudiante.");
                return;
            }

            try {
                String nombreEdicion = request.getParameter("edicion");
                String nicknameEstudiante = estudiante.getNickname();
                
                // Llamamos a la lógica. 0=Exito, 1=Falta Estudiante, 2=Falta Edicion, -1=Error/Ya inscripto
                int resultado = ic.setInscripcion(nicknameEstudiante, nombreEdicion);
                
                if (resultado == 0) {
                     response.setStatus(HttpServletResponse.SC_OK);
                     response.getWriter().write("¡Inscripción realizada con éxito!");
                } else if (resultado == 1) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: El estudiante no existe en la base de datos.");
                } else if (resultado == 2) {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("Error: La edición de curso ya no está disponible.");
                } else {
                     response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                     response.getWriter().write("No se pudo procesar. Es posible que ya estés inscripto a esta edición.");
                }
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Error del sistema: " + e.getMessage());
            }
        }
    }
}