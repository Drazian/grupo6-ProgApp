package edu.edext.controlador;

//import edu.edext.datatypes.DtUsuario;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.tools.Utils;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
 
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private final LoginHelper hlp=new LoginHelper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        String accion = request.getParameter("accion");
        if ("login".equals(accion)) {
            String password = request.getParameter("password");
    // **************   para adelgazar el trafico del login   **************  //
            String value = request.getParameter("identificador");
            DtUsuario dto=null;
            if(!Utils.Validate.isEmpty(value))
                if(Utils.Validate.isEmail(value)){
                    if(hlp.isExistEmail(value))
                        dto=hlp.validarEmail(value, password); }
                else
                    if(hlp.isExistNick(value))
                        dto=hlp.validarNick(value, password);
            if(dto!=null){
                HttpSession session=request.getSession();
                session.setAttribute("usuarioLogueado", dto);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("OK"); }
            else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Usuario o contraseña incorrectos."); } } 
    // *********************************************************************  //
        else if("altaUsuario".equals(accion)){
            request.getRequestDispatcher("/WEB-INF/fragmentos/altaUsuario.jsp").forward(request, response);
        }

        else if ("logout".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if("login".equals(accion))
            request.getRequestDispatcher("/WEB-INF/fragmentos/login.jsp").forward(request, response);

        else if("altaUsuario".equals(accion))
            request.getRequestDispatcher("/WEB-INF/fragmentos/altaUsuario.jsp").forward(request, response);

        else if("ModificarUsuario".equals(accion))
            request.getRequestDispatcher("/WEB-INF/fragmentos/ModificarUsuario.jsp").forward(request, response);
    }

}