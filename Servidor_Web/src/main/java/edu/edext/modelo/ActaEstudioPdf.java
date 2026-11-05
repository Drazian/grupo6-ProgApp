package edu.edext.modelo;

import edu.edext.datatypes.DtActaEstudio;
import edu.edext.datatypes.DtResultadoCurso;

//import edu.edext.controlador.ws.DtActaEstudio;
//import edu.edext.controlador.ws.DtResultadoCurso;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera el Acta de Estudio en PDF con iText (requerimiento 7.4).
 * El documento se arma en el momento, cada vez que el estudiante lo pide desde el Sitio Web.
 */
public final class ActaEstudioPdf {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

    private static final Color AZUL_OSCURO = new DeviceRgb(0x2c, 0x3e, 0x50);
    private static final Color GRIS_TEXTO = new DeviceRgb(0x7f, 0x8c, 0x8d);
    private static final Color GRIS_FONDO = new DeviceRgb(0xf4, 0xf6, 0xf7);
    private static final Color GRIS_BORDE = new DeviceRgb(0xdd, 0xdd, 0xdd);
    private static final Color VERDE = new DeviceRgb(0x1e, 0x84, 0x49);
    private static final Color ROJO = new DeviceRgb(0xc0, 0x39, 0x2b);
    private static final Color NARANJA = new DeviceRgb(0xd3, 0x54, 0x00);

    private ActaEstudioPdf() {}

    /**
     * La letra (seccion 4) dice que el certificado de aprobacion se obtiene "una vez hayan aprobado los cursos que lo componen".
     * Por eso, si el estudiante aprobo todos los cursos del programa el documento es un Certificado de Aprobacion;
     * si no, es un Acta de Estudio que deja constancia de su avance.
     */
    public static boolean esCertificado(DtActaEstudio acta) {
        return acta.isProgramaAprobado();
    }

    public static String nombreArchivo(DtActaEstudio acta) {
        return (esCertificado(acta) ? "Certificado de Aprobación" : "Acta de Estudio")
                + " - " + texto(acta.getPrograma()) + " - " + texto(acta.getNicknameEstudiante()) + ".pdf";
    }

    public static void escribir(DtActaEstudio acta, OutputStream salida) throws IOException {
        boolean certificado = esCertificado(acta);
        PdfDocument pdf = new PdfDocument(new PdfWriter(salida));
        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle((certificado ? "Certificado de Aprobación - " : "Acta de Estudio (constancia de avance) - ") + acta.getPrograma());
        info.setSubject("Acta de Estudio de " + acta.getNombreEstudiante() + " " + acta.getApellidoEstudiante());
        info.setAuthor("edEXT");
        info.setCreator("edEXT - Servidor Web");

        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.WINANSI);
        PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, PdfEncodings.WINANSI);
        PdfFont cursiva = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE, PdfEncodings.WINANSI);

        try (Document doc = new Document(pdf, PageSize.A4)) {
            doc.setMargins(40, 40, 45, 40);
            doc.setFont(normal).setFontSize(10).setFontColor(AZUL_OSCURO);

            // ------------------------------ Encabezado ------------------------------
            doc.add(new Paragraph("edEXT").setFont(negrita).setFontSize(11).setFontColor(GRIS_TEXTO).setMarginBottom(0));
            if (certificado) {
                doc.add(new Paragraph("CERTIFICADO DE APROBACIÓN").setFont(negrita).setFontSize(20).setFontColor(VERDE).setMarginTop(0).setMarginBottom(2));
            } else {
                doc.add(new Paragraph()
                        .add(new Text("ACTA DE ESTUDIO").setFont(negrita).setFontSize(20))
                        .add(new Text("  –  Constancia de avance").setFont(negrita).setFontSize(13).setFontColor(NARANJA))
                        .setMarginTop(0).setMarginBottom(2));
            }
            doc.add(new Paragraph("Programa de Formación: " + texto(acta.getPrograma()))
                    .setFont(negrita).setFontSize(13).setMarginTop(0)
                    .setBorderBottom(new SolidBorder(AZUL_OSCURO, 1.5f)).setPaddingBottom(6));

            // ------------------------- Datos del estudiante --------------------------
            Table datos = new Table(UnitValue.createPercentArray(new float[]{22, 78})).useAllAvailableWidth().setMarginTop(6);
            filaDato(datos, "Estudiante", texto(acta.getNombreEstudiante()) + " " + texto(acta.getApellidoEstudiante())
                    + " (" + texto(acta.getNicknameEstudiante()) + ")", negrita);
            filaDato(datos, "Correo electrónico", texto(acta.getEmailEstudiante()), negrita);
            filaDato(datos, "Programa", texto(acta.getPrograma()), negrita);
            if (acta.getFechaInicioPrograma() != null || acta.getFechaFinPrograma() != null)
                filaDato(datos, "Período del programa", fecha(acta.getFechaInicioPrograma()) + " al " + fecha(acta.getFechaFinPrograma()), negrita);
            filaDato(datos, "Fecha de emisión", fecha(acta.getFechaEmision()), negrita);
            doc.add(datos);

            if (acta.getDescripcionPrograma() != null && !acta.getDescripcionPrograma().isBlank())
                doc.add(new Paragraph(acta.getDescripcionPrograma()).setFont(cursiva).setFontSize(9).setFontColor(GRIS_TEXTO).setMarginTop(6));

            // Declaracion: certifica la aprobacion del programa o deja constancia del avance
            String estudiante = texto(acta.getNombreEstudiante()) + " " + texto(acta.getApellidoEstudiante()) + " (" + texto(acta.getNicknameEstudiante()) + ")";
            Paragraph declaracion = new Paragraph().setMarginTop(10).setPadding(8).setFontSize(10)
                    .setBorderLeft(new SolidBorder(certificado ? VERDE : NARANJA, 3)).setBackgroundColor(GRIS_FONDO);
            if (certificado) {
                declaracion.add("Se certifica que ").add(new Text(estudiante).setFont(negrita))
                        .add(" aprobó todos los cursos que componen el programa de formación ")
                        .add(new Text("\"" + texto(acta.getPrograma()) + "\"").setFont(negrita)).add(".");
            } else {
                declaracion.add("Se deja constancia del avance de ").add(new Text(estudiante).setFont(negrita))
                        .add(" en el programa de formación ")
                        .add(new Text("\"" + texto(acta.getPrograma()) + "\"").setFont(negrita))
                        .add(". Este documento no certifica la aprobación del programa: el certificado de aprobación se emite "
                                + "una vez aprobados todos los cursos que lo componen.");
            }
            doc.add(declaracion);

            // ------------------------ Resultados por curso ---------------------------
            doc.add(new Paragraph("Resultados de los cursos del programa").setFont(negrita).setFontSize(12).setMarginTop(12).setMarginBottom(4));

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 25, 8, 8, 14, 15})).useAllAvailableWidth();
            for (String titulo : new String[]{"Curso", "Edición", "Créditos", "Nota", "Resultado", "Fecha de resultado"})
                tabla.addHeaderCell(new Cell().add(new Paragraph(titulo).setFont(negrita).setFontSize(9).setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(AZUL_OSCURO).setBorder(new SolidBorder(AZUL_OSCURO, 0.5f)).setPadding(5)
                        .setTextAlignment(titulo.equals("Curso") || titulo.equals("Edición") ? TextAlignment.LEFT : TextAlignment.CENTER));

            if (acta.getCursos() == null || acta.getCursos().isEmpty()) {
                tabla.addCell(new Cell(1, 6).add(new Paragraph("El programa no tiene cursos asociados.").setFont(cursiva).setFontSize(9))
                        .setTextAlignment(TextAlignment.CENTER).setFontColor(GRIS_TEXTO)
                        .setBorder(new SolidBorder(GRIS_BORDE, 0.5f)).setPadding(8));
            }
            int fila = 0;
            if (acta.getCursos() != null) {
                for (DtResultadoCurso r : acta.getCursos()) {
                    Color fondo = (fila++ % 2 == 1) ? GRIS_FONDO : null;
                    tabla.addCell(celda(texto(r.getCurso()), negrita, TextAlignment.LEFT, fondo));
                    tabla.addCell(celda(r.getEdicion() == null ? "-" : r.getEdicion(), normal, TextAlignment.LEFT, fondo));
                    tabla.addCell(celda(String.valueOf(r.getCreditos()), normal, TextAlignment.CENTER, fondo));
                    tabla.addCell(celda(r.getNota() == null ? "-" : String.valueOf(r.getNota()), negrita, TextAlignment.CENTER, fondo));
                    tabla.addCell(celda(texto(r.getResultado()), negrita, TextAlignment.CENTER, fondo).setFontColor(colorResultado(r.getResultado())));
                    tabla.addCell(celda(r.getFechaResultado() == null ? "-" : fecha(r.getFechaResultado()), normal, TextAlignment.CENTER, fondo));
                }
            }
            doc.add(tabla);

            // -------------------------------- Resumen --------------------------------
            doc.add(new Paragraph()
                    .add(new Text("Cursos aprobados: ").setFont(negrita))
                    .add(acta.getCursosAprobados() + " de " + acta.getTotalCursos())
                    .add(new Text("     Créditos aprobados: ").setFont(negrita))
                    .add(acta.getCreditosAprobados() + " de " + acta.getTotalCreditos())
                    .setMarginTop(10));

            boolean aprobado = acta.isProgramaAprobado();
            int faltan = acta.getTotalCursos() - acta.getCursosAprobados();
            String estado;
            Color colorEstado;
            if (acta.getTotalCursos() == 0) {
                estado = "SIN CURSOS: el programa de formación todavía no tiene cursos asociados.";
                colorEstado = GRIS_TEXTO;
            } else if (aprobado) {
                estado = "APROBADO: el estudiante aprobó todos los cursos del programa de formación.";
                colorEstado = VERDE;
            } else {
                estado = "EN CURSO: " + (faltan == 1 ? "falta aprobar 1 curso" : "faltan aprobar " + faltan + " cursos") + " del programa de formación.";
                colorEstado = NARANJA;
            }
            doc.add(new Paragraph()
                    .add(new Text("Estado del programa: ").setFont(negrita))
                    .add(new Text(estado).setFont(negrita).setFontColor(colorEstado))
                    .setBackgroundColor(GRIS_FONDO).setPadding(8).setBorderLeft(new SolidBorder(colorEstado, 3)));

            // --------------------------------- Pie -----------------------------------
            doc.add(new Paragraph("Nota mínima de aprobación: " + acta.getNotaMinimaAprobacion() + " (escala 1 a 12). "
                    + "La fecha de resultado corresponde al cierre de la edición del curso. "
                    + "\"En curso\": el estudiante fue aceptado en una edición que todavía no cerró. "
                    + "\"No cursado\": no tiene una inscripción aceptada en ninguna edición del curso.")
                    .setFont(cursiva).setFontSize(8).setFontColor(GRIS_TEXTO).setMarginTop(14));
            doc.add(new Paragraph("Documento generado automáticamente por edEXT el " + LocalDateTime.now().format(FECHA_HORA) + ".")
                    .setFont(cursiva).setFontSize(8).setFontColor(GRIS_TEXTO).setMarginTop(0));
        }
    }

    // ------------------------------- auxiliares -------------------------------
    private static void filaDato(Table tabla, String etiqueta, String valor, PdfFont negrita) {
        tabla.addCell(new Cell().add(new Paragraph(etiqueta + ":").setFont(negrita)).setBorder(Border.NO_BORDER).setPadding(2));
        tabla.addCell(new Cell().add(new Paragraph(valor)).setBorder(Border.NO_BORDER).setPadding(2));
    }

    private static Cell celda(String contenido, PdfFont fuente, TextAlignment alineacion, Color fondo) {
        Cell c = new Cell().add(new Paragraph(contenido).setFont(fuente).setFontSize(9))
                .setTextAlignment(alineacion).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(GRIS_BORDE, 0.5f)).setPadding(5);
        if (fondo != null) c.setBackgroundColor(fondo);
        return c;
    }

    private static Color colorResultado(String resultado) {
        if (DtResultadoCurso.APROBADO.equals(resultado)) return VERDE;
        if (DtResultadoCurso.NO_APROBADO.equals(resultado)) return ROJO;
        if (DtResultadoCurso.EN_CURSO.equals(resultado)) return NARANJA;
        return GRIS_TEXTO;
    }

    private static String fecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FECHA);
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
