package edu.edext.web;

import edu.edext.cliente.DtCurso;
import edu.edext.cliente.DtEdicion;
import edu.edext.cliente.DtEvaluacion;
import edu.edext.cliente.DtInstituto;
import edu.edext.cliente.DtPrograma;
import edu.edext.cliente.DtUsuario;
import edu.edext.cliente.TipoUsuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Armado de los datatypes que genera wsimport (solo tienen setters) y conversiones que antes
 * hacian los datatypes del Servidor Central. Las fechas LocalDate viajan como texto yyyy-MM-dd.
 */
public final class Datos {

    private Datos() {}

    public static LocalDate fecha(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDate.parse(texto.trim());
    }

    public static String texto(LocalDate fecha) {
        return fecha == null ? null : fecha.toString();
    }

    private static void copiar(Collection<String> origen, List<String> destino) {
        if (origen != null)
            destino.addAll(origen);
    }

    public static DtUsuario usuario(String nickname, String email, String nombre, String apellido, String imagen,
                                    Date fechaNacimiento, List<String> institutos, TipoUsuario tipo) {
        DtUsuario u = new DtUsuario();
        u.setNickname(nickname);
        u.setEmail(email);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setImagen(imagen);
        u.setFechaNacimiento(fechaNacimiento);
        copiar(institutos, u.getInstitutos());
        u.setTipoUsuario(tipo);
        return u;
    }

    public static DtCurso curso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos,
                                String url, Date fechaRegistro, String instituto, Set<String> previas,
                                Set<String> categorias, String imagen) {
        DtCurso c = new DtCurso();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setDuracion(duracion);
        c.setCantidadHoras(cantidadHoras);
        c.setCreditos(creditos);
        c.setUrl(url);
        c.setFechaRegistro(fechaRegistro);
        DtInstituto inst = new DtInstituto();
        inst.setNombre(instituto);
        c.setInstituto(inst);
        copiar(previas, c.getNamePrevias());
        copiar(categorias, c.getNameCategorias());
        c.setImagen(imagen);
        return c;
    }

    public static DtEdicion edicion(String nombre, Integer cupo, LocalDate inicio, LocalDate fin, LocalDate publicacion,
                                    Set<String> docentes, String imagen) {
        DtEdicion e = new DtEdicion();
        e.setNombre(nombre);
        e.setCupo(cupo);
        e.setFechaInicio(texto(inicio));
        e.setFechaFin(texto(fin));
        e.setFechaPublicacion(texto(publicacion));
        copiar(docentes, e.getNameDocentes());
        e.setImagen(imagen);
        return e;
    }

    public static DtPrograma programa(String nombre, String descripcion, LocalDate registro, LocalDate inicio, LocalDate fin) {
        DtPrograma p = new DtPrograma();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setFechaRegistro(texto(registro));
        p.setFechaInicio(texto(inicio));
        p.setFechaFin(texto(fin));
        return p;
    }

    /** Reemplaza al Map nickname -> estado de evaluarInscripciones (SOAP no envia Map). */
    public static List<DtEvaluacion> evaluaciones(Map<String, Integer> estados) {
        List<DtEvaluacion> ret = new ArrayList<>();
        for (Map.Entry<String, Integer> e : estados.entrySet()) {
            DtEvaluacion ev = new DtEvaluacion();
            ev.setNickname(e.getKey());
            ev.setEstado(e.getValue());
            ret.add(ev);
        }
        return ret;
    }

    /** Categorias de los cursos del programa (antes DtPrograma.getCategorias(), que no viaja por el Web Service). */
    public static Set<String> categoriasDePrograma(DtPrograma programa) {
        Set<String> ret = new LinkedHashSet<>();
        for (DtCurso curso : programa.getCursos())
            ret.addAll(curso.getNameCategorias());
        return ret;
    }
}
