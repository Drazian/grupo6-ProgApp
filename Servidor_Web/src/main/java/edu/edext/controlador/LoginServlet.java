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
                List<DtUsuario> usuarios = ic.listarUsuarios();
                DtUsuario logueado = null;
                
                if (usuarios != null && identificador != null && password != null) {
                    for (DtUsuario u : usuarios) {
                        boolean coincideUsuario = (u.getNickname() != null && u.getNickname().equalsIgnoreCase(identificador)) 
                                               || (u.getEmail() != null && u.getEmail().equalsIgnoreCase(identificador));
                        
                        if (coincideUsuario && u.getPassword() != null) {
                            boolean passValida = false;
                            
                           try {
                                if (u.getPassword().startsWith("$2a$") || u.getPassword().startsWith("$2b$") || u.getPassword().startsWith("$2y$")) {
                                    // Opción A: Si usas la librería jbcrypt directamente
                                    passValida = org.mindrot.jbcrypt.BCrypt.checkpw(password, u.getPassword());

                                    // Opción B: Si tu clase Utils tiene el método checkpw
                                    // passValida = edu.edext.tools.Utils.Crypt.checkpw(password, u.getPassword());
                                } else {
                                    passValida = u.getPassword().equals(password);
                                }
                            } catch (Exception ex) {
                                passValida = u.getPassword().equals(password);
                            }
                            if (passValida) {
                                logueado = u;
                                break;
                            }
                        }
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