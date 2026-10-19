package edu.edext.logica;
//**************************** Capa Logica *************************************
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInscripcionEdicion;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    
    
//    public List<String> getEstudiantesInscriptos(){
//        return getInscriptos();
//    }
    public List<String> getEstudiantesInscriptos(){
        return getInscriptos();
    }

    public List<String> getEstudiantesCandidatos(){
        return getCandidatos();
    }

    public void kill(){
        if(db!=null && db.isOpen()) db.close();
        hiloTransaction=null;
        DTO=null;
        db=null;
    }
    
    
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
            InscripcionEdicionID idDual = new InscripcionEdicionID(nomEstudiante, nomEdicion);
            InscripcionEdicion isExist = db.find(InscripcionEdicion.class, idDual);
            if(isExist==null){
                InscripcionEdicion newInscripcion=new InscripcionEdicion(estudiante, edicion, LocalDate.now(), 0);
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
            InscripcionEdicionID idDual = new InscripcionEdicionID(nomEstudiante, nomEdicion);
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
    
  // =========================================================================
    // NUEVOS MÉTODOS PARA EL CASO DE USO "SELECCIONAR ESTUDIANTES"
    // =========================================================================

    /**
     * Calcula la prioridad de un estudiante según la fórmula ipr * 0.5
     */
    private double calcularPrioridad(String nicknameEstudiante, String nombreCurso) {
        EntityManager emAux = DB.createEntityManager();
        try {
            // Cuenta inscripciones previas rechazadas (estado=3) a ediciones finalizadas del MISMO curso
            Long ipr = emAux.createQuery(
                "SELECT COUNT(i) FROM InscripcionEdicion i " +
                "WHERE i.estudiante.nickname = :estudiante " +
                "AND i.edicion.curso.nombre = :curso " +
                "AND i.estado = 3 " + 
                "AND i.edicion.fechaFin < CURRENT_DATE", Long.class)
                .setParameter("estudiante", nicknameEstudiante)
                .setParameter("curso", nombreCurso)
                .getSingleResult();
            
            return (ipr != null ? ipr : 0) * 0.5;
        } catch (Exception e) {
            return 0.0;
        } finally {
            emAux.close();
        }
    }

    /**
     * Retorna todas las inscripciones a esta edición, ya convertidas a Dt y con la prioridad calculada.
     */
    public List<DtInscripcionEdicion> getInscripcionesCompletas() {
        db = DB.createEntityManager();
        List<DtInscripcionEdicion> ret = new ArrayList<>();
        try {
            List<InscripcionEdicion> inscripciones = db.createQuery(
                "SELECT i FROM InscripcionEdicion i WHERE i.edicion.nombre = :nombreEdicion",
                InscripcionEdicion.class)
                .setParameter("nombreEdicion", this.nombre)
                .getResultList();

            for (InscripcionEdicion i : inscripciones) {
                Estudiante e = i.getEstudiante();
                DtUsuario dtEst = new DtUsuario(e.getNickname(), e.getPassword(), e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, TipoUsuario.ESTUDIANTE);

                Edicion ed = i.getEdicion();
                
                DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), "usr.png");
//                DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), null, null, 0, 0, null, null, null, null, null, "usr.png");
                
                
                DtEdicion dtEd = new DtEdicion(ed.getNombre(), ed.getCupo(), ed.getFechaInicio(), ed.getFechaFin(), ed.getFechaPublicacion(), dtCurso, "usr.png");

                // Cálculo en caliente del requerimiento 7.5
                double prioridad = calcularPrioridad(e.getNickname(), ed.getCurso().getNombre());

                ret.add(new DtInscripcionEdicion(dtEst, dtEd, i.getFechaInscripcion(), i.getEstado(), prioridad));
            }
        } catch (Exception e) {
            Logger.error("Error al obtener inscripciones completas: " + e.getMessage());
        } finally {
            db.close();
            db=null;
        }
        return ret;
    }

    /**
     * Aplica el dictamen del docente (Aceptado/Rechazado) masivamente a la lista de estudiantes.
     * @param evaluaciones Map donde Key = Nickname del Estudiante, Value = Nuevo Estado (2 o 3)
     */
    public void evaluarInscripciones(Map<String, Integer> evaluaciones) throws Exception {
        db = DB.createEntityManager();
        try {
            db.getTransaction().begin();
            
            Edicion edicionObj = db.find(Edicion.class, this.nombre);
            if (edicionObj == null) throw new Exception("La edición '" + this.nombre + "' ya no existe.");

            for (Map.Entry<String, Integer> entry : evaluaciones.entrySet()) {
                String nicknameEstudiante = entry.getKey();
                Integer nuevoEstado = entry.getValue(); 

                InscripcionEdicionID idDual = new InscripcionEdicionID(nicknameEstudiante, this.nombre);
                InscripcionEdicion inscripcion = db.find(InscripcionEdicion.class, idDual);

                if (inscripcion != null) {
                    inscripcion.setEstado(nuevoEstado);
                }
            }
            db.getTransaction().commit();
            Logger.info("Evaluación de inscripciones completada para la edición: " + this.nombre);
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            Logger.error("Error al evaluar inscripciones: " + e.getMessage());
            throw new Exception("Ocurrió un error al guardar las evaluaciones de los estudiantes.");
        } finally {
            db.close();
            db = null;
        }
    }
    
    public List<DtInscripcionEdicion> getInscripcionesDeEstudiante() {
        db = DB.createEntityManager();
        List<DtInscripcionEdicion> ret = new ArrayList<>();
        try {
            List<InscripcionEdicion> inscripciones = db.createQuery(
                "SELECT i FROM InscripcionEdicion i WHERE i.estudiante.nickname = :nickname ORDER BY i.fechaInscripcion DESC",
                InscripcionEdicion.class)
                .setParameter("nickname", this.nombre)
                .getResultList();

            for (InscripcionEdicion i : inscripciones) {
                Estudiante e = i.getEstudiante();
                DtUsuario dtEst = new DtUsuario(e.getNickname(), e.getPassword(), e.getEmail(), e.getNombre(), e.getApellido(), e.getImagen(), e.getfNacimiento(), null, TipoUsuario.ESTUDIANTE);

                Edicion ed = i.getEdicion();
                String imgCurso = (ed.getCurso() != null && ed.getCurso().getImagen() != null && !ed.getCurso().getImagen().isEmpty()) 
                        ? ed.getCurso().getImagen() : "usr.png";
                DtCurso dtCurso = new DtCurso(ed.getCurso().getNombre(), imgCurso);

                String imgEdicion = (ed.getImagen() != null && !ed.getImagen().isEmpty()) 
                        ? ed.getImagen() : "usr.png";
                DtEdicion dtEd = new DtEdicion(ed.getNombre(), ed.getCupo(), ed.getFechaInicio(), ed.getFechaFin(), ed.getFechaPublicacion(), dtCurso, imgEdicion);

                ret.add(new DtInscripcionEdicion(dtEst, dtEd, i.getFechaInscripcion(), i.getEstado(), 0.0));
            }
        } catch (Exception e) {
            Logger.error("Error al obtener inscripciones del estudiante " + this.nombre + ": " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
            db = null;
        }
        return ret;
    }  
    
    
    
    
}
