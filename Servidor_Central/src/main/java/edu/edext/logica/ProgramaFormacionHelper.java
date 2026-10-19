package edu.edext.logica;
//**************************** Capa Logica *************************************
import edu.edext.datatypes.DtPrograma;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vdraco
 */
public class ProgramaFormacionHelper{
    private final EntityManagerFactory DB;
    private DtPrograma DTO;
    private EntityManager db;
    private EntityTransaction hiloTransaction;
    
    public ProgramaFormacionHelper(EntityManagerFactory obj, DtPrograma dto){
        DB=obj;
        DTO=dto;
    }
    
    public boolean validate(){
        return validateData(DTO);
    }

    private boolean beginTransaction(){
        boolean flag=false;
        db=DB.createEntityManager();
        hiloTransaction=db.getTransaction();
        try {
            hiloTransaction.begin();
            flag=true;
        } catch (Exception e) {
            flag=false;
        }
        return flag;
    }
    
    public boolean persist(){
        boolean ret=beginTransaction();
        if (ret) {
            try {
                db.persist(toEntity());
                hiloTransaction.commit();
                ret=true;
            } catch (Exception e) {
                hiloTransaction.rollback();
                ret=false;
            }
        }else ret=false;
        return ret;
    }
    
    public void kill(){
        if(db!=null && db.isOpen()) db.close();
        hiloTransaction=null;
        DTO=null;
        db=null;
    }

    private ProgramaFormacion toEntity(){
        List<Curso> cursos=new ArrayList();
        List<Categoria> categorias=new ArrayList<>();
        for(String categoria : DTO.getCategorias())
            categorias.add(findCategoria(categoria));
        for(String curso : DTO.getNameCursos())
            cursos.add(findCurso(curso));
        return new ProgramaFormacion(DTO.getNombre(), DTO.getDescripcion(), DTO.getFechaRegistro(), DTO.getFechaInicio(), DTO.getFechaFin(), cursos, categorias, DTO.getImagen());
    }
    
    private boolean validateData(DtPrograma DTO){
        boolean flag=true, flag2=true;
        if(isExist(DTO.getNombre())) flag=false;
        if(flag) if(DTO.getFechaRegistro()==null) flag=false;
        if(flag) if(DTO.getFechaInicio()==null) flag=false;
        if(flag2) if(DTO.getFechaFin()==null) flag=false;
        if(flag && flag2) if(DTO.getFechaInicio().isAfter(DTO.getFechaFin())) flag=false;
        return flag && flag2;
    }
    
    private boolean isExist(String tmpNombre){
        EntityManager tmpDB= DB.createEntityManager();
        boolean flag=false;
        try { flag=tmpDB.find(ProgramaFormacion.class, tmpNombre)!=null;
        } catch (Exception e) { flag=false;
        } finally {
            if (tmpDB!=null && tmpDB.isOpen()) {
                tmpDB.close();
                tmpDB=null;
            }
        }
        return flag;
    }
 
    private Categoria findCategoria(String categoria){
        db=DB.createEntityManager();
        return db.find(Categoria.class, categoria);
    }
    
    private Curso findCurso(String curso){
        db=DB.createEntityManager();
        return db.find(Curso.class, curso);
    }
    
}