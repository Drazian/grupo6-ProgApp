package edu.edext.web;

import jakarta.xml.bind.DatatypeConverter;
import java.util.Calendar;
import java.util.Date;

/**
 * Conversion de xs:dateTime a java.util.Date para las clases que genera wsimport (ver publicador-bindings.xml).
 */
public final class FechasXml {

    private FechasXml() {}

    public static Date leer(String texto) {
        return texto == null || texto.isBlank() ? null : DatatypeConverter.parseDateTime(texto.trim()).getTime();
    }

    public static String escribir(Date fecha) {
        if (fecha == null)
            return null;
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fecha);
        return DatatypeConverter.printDateTime(calendario);
    }
}
