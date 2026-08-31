package com.edext.logica;
//**************************** Capa Logica *************************************
import com.edext.datatypes.DTPrograma;
import com.edext.persistencia.ProgramaFormacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 *
 * @author vdraco
 */
public class CreaPograFormaHelper{
    private final EntityManagerFactory DB;
    private DTPrograma DTO;
    private EntityManager db;
    private EntityTransaction hiloTransaction;
    
    public CreaPograFormaHelper(EntityManagerFactory obj, DTPrograma dto){
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
        db.close();
        hiloTransaction=null;
        DTO=null;
        db=null;
    }
    
    private ProgramaFormacion toEntity(){
        return new ProgramaFormacion(DTO.getNombre(), DTO.getDescripcion(), DTO.getFechaRegistro(), DTO.getFechaInicio(), DTO.getFechaFin());
    }
    
    private boolean validateData(DTPrograma DTO){
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
}
