package edu.edext.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Datos comunes para las JSP al iniciar la aplicacion.
 */
@WebListener
public class InicioAplicacion implements ServletContextListener {

    // Por Web Service el estado de una inscripcion llega como numero (0..3): las JSP lo muestran con
    // ${nombresEstado[ins.estado]}
    public static final String[] NOMBRES_ESTADO = {"No Inscripto", "Inscripto", "Aceptado", "Rechazado"};

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        evento.getServletContext().setAttribute("nombresEstado", NOMBRES_ESTADO);
    }
}
