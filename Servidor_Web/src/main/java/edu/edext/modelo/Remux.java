package edu.edext.modelo;

import edu.edext.tools.Utils;
import edu.edext.controlador.ws.DtInscripcionEdicion;
import edu.edext.controlador.ws.TipoUsuario;
import edu.edext.controlador.ws.DtCategoria;
import edu.edext.controlador.ws.DtInstituto;
import edu.edext.controlador.ws.DtPrograma;
import edu.edext.controlador.ws.DtUsuario;
import edu.edext.controlador.ws.DtEdicion;
import edu.edext.controlador.ws.DtCurso;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vdraco
 */
public class Remux {

    public static DtUsuario DtUsuario(String nickname, String password, String email, String nombre, String apellido, String imagen, LocalDate fNacimiento, 
                  List<String> institutos, TipoUsuario tipoUsuario) {
        return DtUsuario(nickname, password, email, nombre, apellido, imagen, fNacimiento, institutos, tipoUsuario, null, null);
    }
    public static DtUsuario DtUsuario(String nickname, String password, String email, String nombre, String apellido, String imagen, LocalDate fNacimiento,
                  List<String> institutos, TipoUsuario tipoUsuario, List<String> seguidos, List<String> seguidores){
        DtUsuario ret=new DtUsuario();
        ret.setNickname(nickname);
        ret.setPassword(password);
        ret.setEmail(email);
        ret.setNombre(nombre);
        ret.setApellido(apellido);
        ret.setImagen(imagen);
        ret.setFNacimiento(Utils.Fecha.assignFormato(fNacimiento));
        if(institutos!=null)
            ret.getInstitutos().addAll(institutos);
        ret.setTipoUsuario(tipoUsuario);
        if(seguidos!=null)
            ret.getSeguidos().addAll(seguidos);
        if(seguidores!=null)
            ret.getSeguidores().addAll(seguidores);
        return ret;
    }
    
    public static DtPrograma DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin){
        return DtPrograma(nombre, descripcion, fechaRegistro, fechaInicio, fechaFin, null, null, null);
    }
    public static DtPrograma DtPrograma(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio, LocalDate fechaFin,
            List<DtCurso> cursos, Set<String> nameCursos, String imagen){
        DtPrograma ret=new DtPrograma();
        ret.setNombre(nombre);
        ret.setDescripcion(descripcion);
        ret.setFechaRegistro(Utils.Fecha.assignFormato(fechaRegistro));
        ret.setFechaInicio(Utils.Fecha.assignFormato(fechaInicio));
        ret.setFechaFin(Utils.Fecha.assignFormato(fechaFin));
        if(cursos!=null)
            ret.getCursos().addAll(cursos);
        if(nameCursos!=null)
            ret.getNameCursos().addAll(new ArrayList<>(nameCursos));
        ret.setImagen(imagen);
        return ret;
    }

    public static DtCurso DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, LocalDate fechaRegistro, 
            DtInstituto instituto, Set<String> namePrevias, Set<String> nameCategorias, String imagen){
        return DtCurso(nombre, descripcion, duracion, cantidadHoras, creditos, url, fechaRegistro, instituto, null, null, namePrevias, nameCategorias, imagen);
    }
    public static DtCurso DtCurso(String nombre, String descripcion, String duracion, int cantidadHoras, int creditos, String url, LocalDate fechaRegistro, 
            DtInstituto instituto, List<DtCurso> previas, List<DtCategoria> categorias, Set<String> namePrevias, Set<String> nameCategorias, String imagen){
        DtCurso ret=new DtCurso();
        ret.setNombre(nombre);
        ret.setDescripcion(descripcion);
        ret.setDuracion(duracion);
        ret.setCantidadHoras(cantidadHoras);
        ret.setCreditos(creditos);
        ret.setUrl(url);
        ret.setFechaRegistro(Utils.Fecha.assignFormato(fechaRegistro));
        ret.setInstituto(instituto);
        if(previas!=null)
            ret.getPrevias().addAll(previas);
        if(categorias!=null)
            ret.getCategorias().addAll(categorias);
        if(namePrevias!=null)
            ret.getNamePrevias().addAll(new ArrayList<>(namePrevias));
        if(nameCategorias!=null)
            ret.getNameCategorias().addAll(new ArrayList<>(nameCategorias));
        ret.setImagen(imagen);
        return ret;
    }
    
    public static DtEdicion DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso, Set<String> docentes, String imagen){
        return DtEdicion(nombre, cupo, fechaInicio, fechaFin, fechaPublicacion, curso, null, docentes, imagen);
    }
    public static DtEdicion DtEdicion(String nombre, Integer cupo, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaPublicacion, DtCurso curso,
            List<DtUsuario> docentes, Set<String> nameDocentes, String imagen){
        DtEdicion ret=new DtEdicion();
        ret.setNombre(nombre);
        ret.setCupo(cupo);
        ret.setFechaInicio(Utils.Fecha.assignFormato(fechaInicio));
        ret.setFechaFin(Utils.Fecha.assignFormato(fechaFin));
        ret.setFechaPublicacion(Utils.Fecha.assignFormato(fechaPublicacion));
        ret.setCurso(curso);
        if(docentes!=null)
            ret.getDocentes().addAll(docentes);
        if(nameDocentes!=null)
            ret.getNameDocentes().addAll(new ArrayList<>(nameDocentes));
        ret.setImagen(imagen);
        return ret;
    }
    
    public static DtInscripcionEdicion DtInscripcionEdicion(DtUsuario estudiante, DtEdicion edicion, LocalDate fechaInscripcion, int estado, double prioridad){
        DtInscripcionEdicion ret=new DtInscripcionEdicion();
        ret.setEstudiante(estudiante);
        ret.setEdicion(edicion);
        ret.setFechaInscripcion(Utils.Fecha.assignFormato(fechaInscripcion));
        ret.setEstado(estado);
        ret.setPrioridad(prioridad);
        return ret;
    }
    
    public static DtInstituto DtInstituto(String nombre){
        DtInstituto ret=new DtInstituto();
        ret.setNombre(nombre);
        return ret;
    }
}