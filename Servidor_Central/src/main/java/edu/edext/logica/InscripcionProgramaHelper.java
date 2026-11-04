package edu.edext.logica;
//**************************** Capa Logica *************************************
import edu.edext.entitys.Usuario;
import edu.edext.entitys.InscripcionPrograma;
import edu.edext.entitys.Curso;
import edu.edext.entitys.ProgramaFormacion;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInscripcionPrograma;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vdraco
 */
public class InscripcionProgramaHelper {
    private final EntityManagerFactory DB;
    private String nombre;
    private DtEdicion DTO;
    private EntityManager db;
    private EntityTransaction hiloTransaction;
    
    public InscripcionProgramaHelper(EntityManagerFactory obj, String nombre){
        DB=obj;
        this.nombre=nombre;
    }
    
    public List<String> getListInscriptos(String nombre){
        return getListinscriptos(nombre);
    }

//    public DtInscripcionPrograma getInscriptos(String nombre){
//        //return toDTO()
//    }

    public List<DtUsuario> getInscriptos(String nombre){
        return getEstudiantes(nombre);
    }
    
   public void kill(){
        if(db!=null && db.isOpen()) db.close();
        hiloTransaction=null;
        DTO=null;
        db=null;
    }
    
   private List<DtUsuario> getEstudiantes(String nombre){
       List<DtUsuario> ret=new ArrayList<>();
       List<Usuario> tmp=getinscriptos(nombre);
       for (Usuario usuario : tmp)
           ret.add(toUserDTO(usuario));
       return ret;
   }
   
   
    private DtInscripcionPrograma toDTO(InscripcionPrograma entity){
        ProgramaFormacion pf=entity.getProgramaFormacion();
        Set<String> cursos=new HashSet();
        for (Curso curso : entity.getProgramaFormacion().getCursos())
            cursos.add(curso.getNombre());
        return new DtInscripcionPrograma(toUserDTO(entity.getEstudiante()),
                new DtPrograma(pf.getNombre(), pf.getDescripcion(), pf.getFechaRegistro(), pf.getFechaInicio(), pf.getFechaFin(), cursos, pf.getImagen()),
                entity.getFechaInscripcion(), entity.getEstado());
    }
        
   private DtUsuario toUserDTO(Usuario e){
        return new DtUsuario(e.getNickname(), null,  e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, null);
   }
    
   //***************************** pipeline *********************************** 
   
   
    private List<Usuario> getinscriptos(String nombre){
        db=DB.createEntityManager();
        List<Usuario> ret;
        try {
            ret=db.createQuery("SELECT i.estudiante FROM InscripcionPrograma i WHERE i.programa.nombre = :nombrePrograma", Usuario.class)
                    .setParameter("nombrePrograma", nombre).getResultList();
        } catch (Exception e) { ret=new ArrayList<>(); }
        db.close();
        db=null;
        return ret;
    }
    
    private List<String> getListinscriptos(String nombre){
        db=DB.createEntityManager();
        List<String> ret;
        try {
            ret=db.createQuery("SELECT i.estudiante.nickname FROM InscripcionPrograma i WHERE i.programa.nombre = :nombrePrograma", String.class)
                    .setParameter("nombrePrograma", nombre).getResultList();
        } catch (Exception e) { ret=new ArrayList<>(); }
        db.close();
        db=null;
        return ret;
    }
    
    
   
   
}
