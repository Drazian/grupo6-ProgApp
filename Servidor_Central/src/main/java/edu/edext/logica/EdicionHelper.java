package edu.edext.logica;
//**************************** Capa Logica *************************************
import java.util.List;
import java.util.ArrayList;
import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/** 
 *
 * @author vdraco
 */
public class EdicionHelper {
    private final EntityManagerFactory DB;
    private String nombre;
    private DtEdicion DTO;
    private EntityManager db;
    
    public EdicionHelper(EntityManagerFactory obj, String nombre){
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
        if(tmp!=null){
            List<DtUsuario> docentes=new ArrayList<>();
            for(Usuario d : tmp.getDocentes())
                docentes.add(new DtUsuario(d.getNickname(), null, d.getEmail(), 
                        d.getNombre(), d.getApellido(), d.getImagen(), 
                        d.getfNacimiento(), null, null));
            return new DtEdicion(
                    tmp.getNombre(), 
                    tmp.getCupo(), 
                    tmp.getFechaInicio(), 
                    tmp.getFechaFin(), 
                    tmp.getFechaPublicacion(), 
                    new DtCurso(tmp.getCurso().getNombre(), tmp.getCurso().getDescripcion(), 
                            tmp.getCurso().getDuracion(), 0, 0, tmp.getCurso().getUrl(), 
                            tmp.getCurso().getFechaRegistro(), 
                            new DtInstituto(tmp.getCurso().getInstituto().getNombre()),
                            tmp.getCurso().getImagen()), 
                    docentes,
                    tmp.getImagen()); }
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
        db.close();
        db=null;
        return ret;
    }

    private String seekNameEdicion(){
        db=DB.createEntityManager();
        String ret;
        try { ret=db.find(Edicion.class, nombre).getNombre(); }
        catch (Exception e) { ret=null; }
        return ret;
    }
    private Edicion seekEdicion(){
        db=DB.createEntityManager();
        Edicion ret;
        try { ret=db.find(Edicion.class, nombre); }
        catch (Exception e) { ret=null; }
        db=null;
        return ret;
    }
    
}