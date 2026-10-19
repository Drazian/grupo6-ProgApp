package edu.edext.logica;

import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

/**
 *
 * @author vdraco
 */
@Entity
@Table(name="ProgramaFormacion")
public class ProgramaFormacion {
 
    @Id
    private String nombre;
    @Column(length = 2000)
    private String descripcion;
    @Column(nullable=false)
    private LocalDate fechaRegistro;
    @Column(nullable=false)
    private LocalDate fechaInicio;
    @Column(nullable=false)
    private LocalDate fechaFin;
    private String imagen;

    @ManyToMany
    @JoinTable(
        name="Integra",
        joinColumns=@JoinColumn(name="programa"),
        inverseJoinColumns=@JoinColumn(name="curso"))
    private List<Curso> cursos;

    @ManyToMany
    @JoinTable(
    //    name="Programa_Categoria",
        name="Clasifica",
        joinColumns=@JoinColumn(name="programa"),
        inverseJoinColumns=@JoinColumn(name="categoria")
    )
    private List<Categoria> categorias;
//    @ManyToMany
//    private List<Curso> cursos = new ArrayList<>(); //Los programas inicializan sin cursos asociados.
//
    public ProgramaFormacion(){}
    
    public ProgramaFormacion(String nombre, String descripcion, LocalDate fechaRegistro, LocalDate fechaInicio,
                             LocalDate fechaFin, List<Curso> cursos, List<Categoria> categorias, String imagen){
        this.fechaRegistro=fechaRegistro;
        this.fechaInicio=fechaInicio;
        this.descripcion=descripcion;
        this.categorias=categorias;
        this.fechaFin=fechaFin;
        this.cursos=cursos;
        this.nombre=nombre;
        this.imagen=imagen;
    }

//    public void setCursos (List<Curso> cursos){this.cursos=cursos;}
    public void setImagen(String imagen){ this.imagen=imagen; }
    public void setNombre(String nombre){ this.nombre=nombre;   }
    public void setCursos(List<Curso> cursos) { this.cursos=cursos; }
    public void setFechaFin(LocalDate fechaFin){ this.fechaFin=fechaFin; }
    public void setDescripcion(String descripcion){ this.descripcion=descripcion; }
    public void setFechaInicio(LocalDate fechaInicio){ this.fechaInicio=fechaInicio; }
    public void setCategorias(List<Categoria> categorias){this.categorias=categorias;   }
    public void setFechaRegistro(LocalDate fechaRegistro){ this.fechaRegistro=fechaRegistro; }
        
    public LocalDate getFechaRegistro (){ return this.fechaRegistro;  }
    public List<Categoria> getCategorias(){ return this.categorias; }
    public LocalDate getFechaInicio(){ return this.fechaInicio; }
    public String getDescripcion(){ return this.descripcion; }
    public LocalDate getFechaFin(){ return this.fechaFin; }
    public List<Curso> getCursos(){ return this.cursos; }
    public String getNombre(){ return this.nombre;   }
    public String getImagen(){ return this.imagen; }

//    public Set<DtCurso> getDtCursos(){
//        Set<DtCurso> ret= new HashSet<>();
//        for (Curso c : cursos) {
//            ret.add(
//                    new DtCurso(
//                            c.getNombre(), c.getDescripcion(), c.getDuracion(), c.getCantidadHoras(), c.getCreditos(), c.getUrl(),
//                            c.getFechaRegistro(), new ArrayList<>())
//                    );
//                    //, c.getInstituto(), c.getPrevias() <- faltan
// 
//        }
//        return null;
//    }
                    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.nombre);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ProgramaFormacion other = (ProgramaFormacion) obj;
        return Objects.equals(this.nombre, other.nombre);
    }

    public void agregarCurso(Curso curso){
        if (!this.cursos.contains(curso)){
            this.cursos.add(curso);
        }
    } 
}