package edu.edext.controlador;

import org.tinylog.Logger;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.io.ByteArrayOutputStream;
import edu.edext.modelo.ActaEstudioPdf;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import java.nio.charset.StandardCharsets;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.TipoUsuario;
import jakarta.servlet.annotation.WebServlet;
import edu.edext.controlador.ws.DtActaEstudio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ActaEstudioServlet")
public class ActaEstudioServlet extends HttpServlet {

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
            acta = Fabrica.getConexion().getActaEstudio(estudiante.getNickname(), programa);
        } catch (Exception e) {
            if (sinConexion(e)) {
                Logger.error(e, "Acta de estudio: no se pudo conectar con el Servidor Central: ");
                error(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "No se pudo conectar con el Servidor Central. Intente nuevamente más tarde.");
            } else {
                error(response, HttpServletResponse.SC_BAD_REQUEST, mensajeDelCentral(e));
            }
            return;
        }
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        try {
            ActaEstudioPdf.escribir(acta, pdf);
        } catch (Exception e) {
            Logger.error(e, "Error al generar el acta de estudio en PDF: ");
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocurrió un error al generar el PDF del acta.");
            return;
        }
        String nombreArchivo = ActaEstudioPdf.nombreArchivo(acta);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + soloAscii(nombreArchivo) + "\"; filename*=UTF-8''"
                + URLEncoder.encode(nombreArchivo, StandardCharsets.UTF_8).replace("+", "%20"));
        response.setHeader("Cache-Control", "no-store");
        response.setContentLength(pdf.size());
        pdf.writeTo(response.getOutputStream());
    }

    private static String mensajeDelCentral(Exception e) {
        String mensaje = e.getMessage() == null ? "" : e.getMessage();
        mensaje = mensaje.replaceFirst("(?s)^Client received SOAP Fault from server:\\s*", "")
                         .replaceFirst("(?s)\\s*Please see the server log.*$", "")
                         .trim();
        return mensaje.isEmpty() ? "No se pudo generar el acta de estudio." : mensaje;
    }

    private static boolean sinConexion(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause())
            if (t instanceof java.net.ConnectException) return true;
        return false;
    }

    private static void error(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        response.setStatus(codigo);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(mensaje);
    }

    private static String soloAscii(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinAcentos.replaceAll("[^A-Za-z0-9 ._-]", "_");
    }
}