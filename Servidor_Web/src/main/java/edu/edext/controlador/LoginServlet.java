package edu.edext.controlador;

import edu.edext.cliente.DtUsuario;
import edu.edext.cliente.Publicador;
import edu.edext.web.ClienteCentral;
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
        Publicador ic = ClienteCentral.get();

        if ("login".equals(accion)) {
            String identificador = request.getParameter("identificador");
            String password = request.getParameter("password");

            try {
                // La contraseña la verifica el Servidor Central (por el Web Service nunca viaja el hash)
                DtUsuario logueado = null;
                if (identificador != null && !identificador.trim().isEmpty() && password != null) {
                    logueado = ic.iniciarSesion(identificador.trim(), password);
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