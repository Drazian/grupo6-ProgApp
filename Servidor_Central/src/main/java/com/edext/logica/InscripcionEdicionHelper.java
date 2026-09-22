package com.edext.logica;
//**************************** Capa Logica *************************************
import com.edext.datatypes.DtEdicion;
import com.edext.persistencia.Edicion;
import com.edext.persistencia.Estudiante;
import com.edext.persistencia.InscripEditMolde;
import com.edext.persistencia.InscripcionEdicion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class InscripcionEdicionHelper {
    private final EntityManagerFactory DB;
    private String nombre;
    private DtEdicion DTO;
    private EntityManager db;
    private EntityTransaction hiloTransaction;
    
    public InscripcionEdicionHelper(EntityManagerFactory obj, String nombre){
        DB=obj;
        this.nombre=nombre;
    }
    
    
    public List<String> getEstudiantesInscriptos(){
        return getInscriptos();
    }

    public List<String> getEstudiantesCandidatos(){
        return getCandidatos();
    }





//    public boolean persist(){
//        boolean ret=beginTransaction();
//        if (ret) {
//            try {
//      //          db.persist(toEntity());
//                hiloTransaction.commit();
//                ret=true;
//            } catch (Exception e) {
//                hiloTransaction.rollback();
//                ret=false;
//            }
//        }else ret=false;
//        return ret;
//    }

//    public boolean persist(){
//        boolean ret=beginTransaction();
//        if (ret) {
//            try {
//                db.persist(toEntity());
//                hiloTransaction.commit();
//                ret=true;
//            } catch (Exception e) {
//                hiloTransaction.rollback();
//                ret=false;
//            }
//        }else ret=false;
//        return ret;
//    }




    public void kill(){
        if(db!=null && db.isOpen()) db.close();
        hiloTransaction=null;
        DTO=null;
        db=null;
    }
    
//    private boolean beginTransaction(){
//        boolean flag=false;
//        db=DB.createEntityManager();
//        hiloTransaction=db.getTransaction();
//        try {
//            hiloTransaction.begin();
//            flag=true;
//        } catch (Exception e) {
//            flag=false;
//        }
//        return flag;
//    }
    
    
    //***************************** pipeline ***********************************
    
    private List<String> getInscriptos(){
        db=DB.createEntityManager();
        List<String> ret;
        try { ret=db.createQuery("SELECT i.estudiante.nickname FROM InscripcionEdicion i WHERE i.edicion.nombre = :nombreEdicion", 
                    String.class)
                    .setParameter("nombreEdicion", nombre)
                    .getResultList(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        db.close();
        db=null;
        return ret;
    }
    
    private List<String> getCandidatos(){
        db=DB.createEntityManager();
        List<String> ret;
        try { ret=db.createQuery("SELECT e.nickname FROM Estudiante e WHERE e.nickname NOT IN (" +
                                 "SELECT i.estudiante.nickname FROM InscripcionEdicion i WHERE i.edicion.nombre = :nombreEdicion" +")", 
                    String.class)
                    .setParameter("nombreEdicion", nombre)
                    .getResultList();
        } catch (Exception e) { ret=new ArrayList<>(); }
        db.close();
        db=null;
        return ret;
    }
    
    public int inscribirEstudiante(String nomEstudiante, String nomEdicion){ // incompleta falta aislacion y modularizacion
        int ret=-1;
        db=DB.createEntityManager();
        try {
            db.getTransaction().begin();
            Estudiante estudiante = db.find(Estudiante.class, nomEstudiante);
            Edicion edicion = db.find(Edicion.class, nomEdicion);
            if(estudiante==null) return 1;
            if (edicion==null) return 2;
            InscripEditMolde idDual = new InscripEditMolde(nomEstudiante, nomEdicion);
            InscripcionEdicion isExist = db.find(InscripcionEdicion.class, idDual);
            if(isExist==null){
                InscripcionEdicion newInscripcion=new InscripcionEdicion(estudiante, edicion, LocalDate.now());
                db.persist(newInscripcion);
                db.getTransaction().commit();
                Logger.info("Se Inscribio el Estudiante {} en la edicion {}", nomEstudiante, nomEdicion);
                ret=0;
            }
        } catch (Exception e) {
            if(db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error("Error al Inscribir al estudiante {} en la edicion {}", nomEstudiante, nomEdicion);
        }
        finally{
            db.close();
        }
        return ret;
    }
 
    // 0 exito
    // 1 estudiante no existe
    // 2 edicion no existe
    // -1 error
    
    public int cancelarInscripcion(String nomEstudiante, String nomEdicion){ // incompleta falta aislacion y modularizacion
        int ret=-1;
        db=DB.createEntityManager();
        try {
            db.getTransaction().begin();
            InscripEditMolde idDual = new InscripEditMolde(nomEstudiante, nomEdicion);
            InscripcionEdicion isExist = db.find(InscripcionEdicion.class, idDual);
            if(isExist!=null){
                db.remove(isExist);
                db.getTransaction().commit();
                Logger.info("Se Cancelo la Incripcion del Estudiante {} a la Edicion {}", nomEstudiante, nomEdicion);
                ret=0;
            }
        } catch (Exception e) {
            if(db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error("Error al Cancelar la Inscripcion del estudiante {} a la edicion {}", nomEstudiante, nomEdicion);
            ret=-1;
        }
        finally{
            db.close();
        }
        
        return ret;
    }
}
