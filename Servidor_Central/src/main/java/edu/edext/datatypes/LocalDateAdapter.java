package edu.edext.datatypes;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * JAXB no soporta LocalDate: en los Web Services viaja como texto ISO (yyyy-MM-dd).
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDate.parse(texto.trim());
    }

    @Override
    public String marshal(LocalDate fecha) {
        return fecha == null ? null : fecha.toString();
    }
}
