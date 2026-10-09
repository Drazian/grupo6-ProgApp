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
                // Buscamos coincidencia de Nickname/Email y Contraseña
                List<DtUsuario> usuarios = ic.listarUsuarios();
                DtUsuario logueado = null;
                
                for (DtUsuario u : usuarios) {
                    if ((u.getNickname().equals(identificador) || u.getEmail().equals(identificador)) 
                            && u.getPassword().equals(password)) {
                        logueado = u;
                        break;
                    }
                }

                if (logueado != null) {
                    // ¡Éxito! Guardamos el usuario en la sesión del servidor
                    HttpSession session = request.getSession();
                    session.setAttribute("usuarioLogueado", logueado);
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("OK");
                } else {
                    // Falla: Credenciales incorrectas
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Error 401
                    response.getWriter().write("Usuario o contraseña incorrectos.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error en el servidor: " + e.getMessage());
            }
        } 
        else if ("logout".equals(accion)) {
            // Destruimos la sesión
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}