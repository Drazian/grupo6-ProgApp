package edu.edext.logica;

import edu.edext.entitys.Usuario;
import edu.edext.entitys.Docente;
import edu.edext.entitys.Estudiante;
import edu.edext.entitys.Instituto;
import jakarta.persistence.EntityManagerFactory;
import edu.edext.datatypes.DtUsuario;
import jakarta.persistence.EntityManager;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.tools.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vdraco
 */
public class UsuarioHelper {
    private final EntityManagerFactory DB;
    private EntityManager db;
    
    public UsuarioHelper(EntityManagerFactory obj){ DB=obj; }

    public DtUsuario validaNick(String nickname, String pass){
        return toDTO(validateNick(nickname, pass));
    }

    public DtUsuario validarEmail(String email, String pass){
        return toDTO(validateEmail(email, pass));
    }

    private DtUsuario toDTO(Usuario data){
        DtUsuario ret=null;
        List<String> institutos=new ArrayList<>(), seguidores=new  ArrayList<>(), seguidos=new ArrayList<>();
        TipoUsuario type;
        if(data != null){
            for(Usuario seguido : data.getSeguidos())
                seguidos.add(seguido.getNickname());
            for(Usuario seguidor : data.getSeguidores())
                seguidores.add(seguidor.getNickname());
            if(data instanceof Estudiante){
                type = TipoUsuario.ESTUDIANTE;
                institutos=null; }
            else{
                type = TipoUsuario.DOCENTE;
                for(Instituto instituto : ((Docente) data).getInstitutos())
                    institutos.add(instituto.getNombre());
            }
            ret = new DtUsuario(data.getNickname(), null, data.getEmail(), data.getNombre(), data.getApellido(), data.getImagen(), data.getfNacimiento(), institutos, type, seguidos, seguidores);
        }
        return ret;
    }

//    public DtSessionUsuario validaNick(String nickname, String pass) {
//        return toDTO(validateNick(nickname, pass));
//    }
//
//    public DtSessionUsuario validarEmail(String email, String pass) {
//        return toDTO(validateEmail(email, pass));
//    }
//
//    private DtSessionUsuario toDTO(Usuario data) {
//        DtSessionUsuario ret = null;
//        TipoUsuario type;
//        if (data != null) {
//            if (data instanceof Estudiante) {
//                type = TipoUsuario.ESTUDIANTE;
//            } else {
//                type = TipoUsuario.DOCENTE;
//            }
//            ret = new DtSessionUsuario(data.getNickname(), data.getEmail(), data.getImagen(), type);
//        }
//        return ret;
//    }
    
    public void kill(){
        finaly();
    }
    
    private void finaly(){
        if(db!=null && db.isOpen()) db.close();
        db=null;
    }

    private boolean validate(String password, String passDB){
        boolean ret;
        if(passDB.startsWith("$2a$") || passDB.startsWith("$2b$") || passDB.startsWith("$2y$"))
            ret=Utils.Crypt.compareBCrypt(password, passDB);
        else    // el alta guarda la contraseña cifrada con AES: se descifra la guardada y se compara con la ingresada
            ret=password.equals(Utils.Crypt.descifrarAES(passDB));
        return ret;
    }
    //***************************** pipeline ***********************************

    private Usuario validateNick(String nickname, String password){
        db=DB.createEntityManager();
        Usuario ret=null;
        try {
            ret=db.find(Usuario.class, nickname);
            ret=validate(password, ret.getPassword())?ret:null; }
        catch(Exception e) { ret=null; }
        //finally { finaly(); }
        return ret;
    }
    
    private Usuario validateEmail(String email, String password){
        db=DB.createEntityManager();
        Usuario ret=null;
        try {
            ret=db.createQuery("SELECT u FROM Usuario u WHERE u.email = :email",Usuario.class)
                  .setParameter("email", email).getResultStream().findFirst().orElse(null);
            ret=validate(password, ret.getPassword())?ret:null; }
        catch(Exception e) { ret=null; }
        //finally { finaly(); }
        return ret;
    }

//    private Usuario validateNick(String nickname, String password){
//        db=DB.createEntityManager();
//        Usuario ret=null;
//        try {
//            ret=db.find(Usuario.class, nickname);
//            ret=Utils.Crypt.compareBCrypt(password, ret.getPassword())?ret:null; }
//        catch(Exception e) { ret=null; }
//        finally { finaly(); }
//        return ret;
//    }
//    
//    private Usuario validateEmail(String email, String password){
//        db=DB.createEntityManager();
//        Usuario ret=null;
//        try {
//            ret=db.createQuery("SELECT u FROM Usuario u WHERE u.email = :email",Usuario.class)
//                  .setParameter("email", email).getResultStream().findFirst().orElse(null);
//            ret=Utils.Crypt.compareBCrypt(password, ret.getPassword())?ret:null; }
//        catch(Exception e) { ret=null; }
//        finally { finaly(); }
//        return ret;
//    }

}
