package edu.edext.datatypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Datos del Acta de Estudio de un estudiante para un programa de formacion
 * (CU "Tramitar Acta de Estudio", requerimiento 7.4). El PDF lo arma el Servidor Web a partir de esto.
 * Todo es campo (sin getters calculados) para que viaje completo tambien por SOAP.
 */
@XmlRootElement(name = "DtActaEstudio")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtActaEstudio {
    private String nicknameEstudiante;
    private String nombreEstudiante;
    private String apellidoEstudiante;
    private String emailEstudiante;

    private String programa;
    private String descripcionPrograma;
    @XmlElement(name = "fechaInicioPrograma")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaInicioPrograma;
    @XmlElement(name = "fechaFinPrograma")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaFinPrograma;

    @XmlElement(name = "fechaEmision")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaEmision;
    private int notaMinimaAprobacion;

    private List<DtResultadoCurso> cursos;
    private int cursosAprobados;
    private int totalCursos;
    private int creditosAprobados;
    private int totalCreditos;
    private boolean programaAprobado;   // aprobo todos los cursos del programa

    public DtActaEstudio(){}

    public DtActaEstudio(DtUsuario estudiante, String programa, String descripcionPrograma,
                         LocalDate fechaInicioPrograma, LocalDate fechaFinPrograma,
                         LocalDate fechaEmision, int notaMinimaAprobacion, List<DtResultadoCurso> cursos){
        this.nicknameEstudiante = estudiante.getNickname();
        this.nombreEstudiante = estudiante.getNombre();
        this.apellidoEstudiante = estudiante.getApellido();
        this.emailEstudiante = estudiante.getEmail();
        this.programa = programa;
        this.descripcionPrograma = descripcionPrograma;
        this.fechaInicioPrograma = fechaInicioPrograma;
        this.fechaFinPrograma = fechaFinPrograma;
        this.fechaEmision = fechaEmision;
        this.notaMinimaAprobacion = notaMinimaAprobacion;
        this.cursos = new ArrayList<>(cursos);

        for (DtResultadoCurso r : this.cursos) {
            this.totalCursos++;
            this.totalCreditos += r.getCreditos();
            if (r.isAprobado()) {
                this.cursosAprobados++;
                this.creditosAprobados += r.getCreditos();
            }
        }
        this.programaAprobado = this.totalCursos > 0 && this.cursosAprobados == this.totalCursos;
    }

    public String getNicknameEstudiante(){ return this.nicknameEstudiante; }
    public String getNombreEstudiante(){ return this.nombreEstudiante; }
    public String getApellidoEstudiante(){ return this.apellidoEstudiante; }
    public String getEmailEstudiante(){ return this.emailEstudiante; }
    public String getPrograma(){ return this.programa; }
    public String getDescripcionPrograma(){ return this.descripcionPrograma; }
    public LocalDate getFechaInicioPrograma(){ return this.fechaInicioPrograma; }
    public LocalDate getFechaFinPrograma(){ return this.fechaFinPrograma; }
    public LocalDate getFechaEmision(){ return this.fechaEmision; }
    public int getNotaMinimaAprobacion(){ return this.notaMinimaAprobacion; }
    public List<DtResultadoCurso> getCursos(){ return this.cursos; }
    public int getCursosAprobados(){ return this.cursosAprobados; }
    public int getTotalCursos(){ return this.totalCursos; }
    public int getCreditosAprobados(){ return this.creditosAprobados; }
    public int getTotalCreditos(){ return this.totalCreditos; }
    public boolean isProgramaAprobado(){ return this.programaAprobado; }
}
