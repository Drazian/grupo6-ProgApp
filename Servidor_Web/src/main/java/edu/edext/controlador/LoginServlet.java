package edu.edext.controlador;

import edu.edext.datatypes.DtUsuario;
import edu.edext.logica.Fabrica;
import edu.edext.logica.IControlador;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
 
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        
        String accion = request.getParameter("accion");
        IControlador ic = Fabrica.getInstance().getIControlador();

        if ("login".equals(accion)) {
            String identificador = request.getParameter("identificador");
            String password = request.getParameter("password");

            try {
                DtUsuario logueado = null;
                if (identificador != null && !identificador.trim().isEmpty() && password != null) {
                    DtUsuario u = ic.obtenerUsuario(identificador.trim());
                    if (u != null && edu.edext.tools.Utils.Crypt.verifyPassword(password, u.getPassword())) {
                        logueado = new DtUsuario(
                            u.getNickname(),
                            null,
                            u.getEmail(),
                            u.getNombre(),
                            u.getApellido(),
                            u.getImagen(),
                            u.getfNacimiento(),
                            u.getInstitutos(),
                            u.getTipoUsuario()
                        );
                    }
                }

                if (logueado != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("usuarioLogueado", logueado);
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("OK");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Usuario o contraseña incorrectos.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error en el servidor: " + e.getMessage());
            }
        } 
        else if ("logout".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}