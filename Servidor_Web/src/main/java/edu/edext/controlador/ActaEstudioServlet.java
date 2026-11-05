package edu.edext.controlador;

//import edu.edext.datatypes.DtActaEstudio;
//import edu.edext.datatypes.DtUsuario;
//import edu.edext.datatypes.TipoUsuario;
//import edu.edext.logica.Fabrica;
//import edu.edext.logica.IBridgeSOAP;
//import edu.edext.logica.IControlador;

import edu.edext.controlador.ws.DtActaEstudio;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.IBridgeSOAP;
import edu.edext.controlador.ws.TipoUsuario;

import edu.edext.modelo.ActaEstudioPdf;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

/**
 * CU "Tramitar Acta de Estudio" (Estudiante), requerimiento 7.4.
 * Desde su perfil, el estudiante elige un programa de formacion en el que esta inscripto y descarga el acta en PDF.
 * El PDF se genera en el momento con iText, con los datos que devuelve el Servidor Central.
 */
@WebServlet("/ActaEstudioServlet")
public class ActaEstudioServlet extends HttpServlet {
    private final IBridgeSOAP ic = Fabrica.getConexion();
//    private final IControlador ic = Fabrica.getInstance().getIControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Object usuario = request.getSession().getAttribute("usuarioLogueado");
        if (!(usuario instanceof DtUsuario) || ((DtUsuario) usuario).getTipoUsuario() != TipoUsuario.ESTUDIANTE) {
            error(response, HttpServletResponse.SC_FORBIDDEN, "Solo los estudiantes pueden tramitar el acta de estudio. Inicie sesión como estudiante.");
            return;
        }
        DtUsuario estudiante = (DtUsuario) usuario;

        String programa = request.getParameter("programa");
        if (programa == null || programa.isBlank()) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, "Debe indicar el programa de formación.");
            return;
        }

        DtActaEstudio acta;
        try {
            // El Central valida que el estudiante este inscripto en el programa y arma los resultados de cada curso
            acta = ic.getActaEstudio(estudiante.getNickname(), programa);
        } catch (Exception e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        // Se arma en memoria para poder responder un error con su codigo si algo falla al generarlo
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        try {
            ActaEstudioPdf.escribir(acta, pdf);
        } catch (Exception e) {
            Logger.error(e, "Error al generar el acta de estudio en PDF: ");
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error al generar el PDF del acta.");
            return;
        }

        // "Certificado de Aprobación - ..." si aprobó todo el programa, "Acta de Estudio - ..." si es una constancia de avance
        String nombreArchivo = ActaEstudioPdf.nombreArchivo(acta);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + soloAscii(nombreArchivo) + "\"; filename*=UTF-8''"
                + URLEncoder.encode(nombreArchivo, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setHeader("Cache-Control", "no-store");
        response.setContentLength(pdf.size());
        pdf.writeTo(response.getOutputStream());
    }

    private static void error(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        response.setStatus(codigo);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(mensaje);
    }

    // Nombre de archivo de respaldo para navegadores que no entienden filename* (sin acentos ni caracteres raros)
    private static String soloAscii(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinAcentos.replaceAll("[^A-Za-z0-9 ._-]", "_");
    }
}
