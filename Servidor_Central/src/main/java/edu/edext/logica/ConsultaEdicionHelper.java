package edu.edext.logica;
//**************************** Capa Logica *************************************
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vdraco
 */
public class ConsultaEdicionHelper {
    private final EntityManagerFactory DB;
    private String nombre;
    private DtEdicion DTO;
    private EntityManager db;
    
    public ConsultaEdicionHelper(EntityManagerFactory obj, String nombre){
        DB=obj;
        this.nombre=nombre;
    }
    
    public List<String> listarEdicionPorCurso(){
        return getEdicionxCurso();
    }
        
    public List<DtEdicion> getListEditPorCurso(){
        List<DtEdicion> ret=new ArrayList<>();
        for(String str : getEdicionxCurso())
            ret.add(new DtEdicion(str, null, null, null, null, null));
        return ret;
    }
    
    public DtEdicion getEdicion(){
        Edicion tmp=seekEdicion();
        if(tmp!=null) {
            List<DtUsuario> dtDocentes = new ArrayList<>();
            if (tmp.getDocentes() != null) {
                for (Docente d : tmp.getDocentes()) {
                    List<String> instDocente = new ArrayList<>();
                    if (d.getInstitutos() != null) {
                        for(Instituto inst : d.getInstitutos()) {
                            instDocente.add(inst.getNombre());
                        }
                    }
                    dtDocentes.add(new DtUsuario(d.getNickname(), d.getPassword(), d.getEmail(), d.getNombre(), d.getApellido(), d.getImagen(), d.getfNacimiento(), instDocente, TipoUsuario.DOCENTE));
                }
            }
            
            return new DtEdicion(
                    tmp.getNombre(), 
                    tmp.getCupo(), 
                    tmp.getFechaInicio(), 
                    tmp.getFechaFin(), 
                    tmp.getFechaPublicacion(), 
                    new DtCurso(tmp.getCurso().getNombre(), null, null, 0, 0, null, null, null, null, "usr.png"), 
                    dtDocentes,
                    tmp.getImagen()
            ); 
        }
        else return null;
    }
  
    public void kill(){
        if(db!=null && db.isOpen()) db.close();
        DTO=null;
        db=null;
    }
    
    
    //***************************** pipeline ***********************************
    
    private List<String> getEdicionxCurso(){
        db=DB.createEntityManager();
        List<String> ret;
        try { ret=db.createQuery("SELECT e.nombre FROM Edicion e WHERE e.curso.nombre = :curso", String.class).setParameter("curso", nombre).getResultList(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        finally { if(db!=null && db.isOpen()) db.close(); }
        return ret;
    }

    private String seekNameEdicion(){
        db=DB.createEntityManager();
        String ret;
        try { ret=db.find(Edicion.class, nombre).getNombre(); }
        catch (Exception e) { ret=null; }
        finally { if(db!=null && db.isOpen()) db.close(); }
        return ret;
    }
    
    private Edicion seekEdicion(){
        db=DB.createEntityManager();
        Edicion ret;
        try { ret=db.find(Edicion.class, nombre); }
        catch (Exception e) { ret=null; }
        return ret;
    }
}