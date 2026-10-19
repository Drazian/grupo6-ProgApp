package edu.edext.logica;

import jakarta.persistence.EntityManagerFactory;
import edu.edext.datatypes.DtSessionUsuario;
import jakarta.persistence.EntityManager;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.tools.Utils;

/**
 *
 * @author vdraco
 */
public class UsuarioHelper {
    private final EntityManagerFactory DB;
    private EntityManager db;
    
    public UsuarioHelper(EntityManagerFactory obj){ DB=obj; }
   
    public DtSessionUsuario validaNick(String nickname, String pass){
        return toDTO(validateNick(nickname, pass));
    }
    
    public DtSessionUsuario validarEmail(String email, String pass){
        return toDTO(validateEmail(email, pass));
    }

    private DtSessionUsuario toDTO(Usuario data){
        DtSessionUsuario ret=null;
        TipoUsuario type;
        if(data!=null){
            if(data instanceof Estudiante) type=TipoUsuario.ESTUDIANTE;
            else type=TipoUsuario.DOCENTE;
            ret=new DtSessionUsuario(data.getNickname(), data.getEmail(), data.getImagen(), type);
        }
        return ret;
    }
    
    public void kill(){
        finaly();
    }
    
    private void finaly(){
        if(db!=null && db.isOpen()) db.close();
        db=null;
    }

    //***************************** pipeline ***********************************

    private Usuario validateNick(String nickname, String password){
        db=DB.createEntityManager();
        Usuario ret=null;
        try {
            ret=db.find(Usuario.class, nickname);
            ret=Utils.Crypt.compareBCrypt(password, ret.getPassword())?ret:null; }
        catch(Exception e) { ret=null; }
        finally { finaly(); }
        return ret;
    }
    
    private Usuario validateEmail(String email, String password){
        db=DB.createEntityManager();
        Usuario ret=null;
        try {
            ret=db.createQuery("SELECT u FROM Usuario u WHERE u.email = :email",Usuario.class)
                  .setParameter("email", email).getResultStream().findFirst().orElse(null);
            ret=Utils.Crypt.compareBCrypt(password, ret.getPassword())?ret:null; }
        catch(Exception e) { ret=null; }
        finally { finaly(); }
        return ret;
    }

}