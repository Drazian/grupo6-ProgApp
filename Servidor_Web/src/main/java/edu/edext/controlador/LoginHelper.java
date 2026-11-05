package edu.edext.controlador;

//import edu.edext.datatypes.DtUsuario;
//import edu.edext.logica.Fabrica;
import edu.edext.controlador.ws.DtUsuario;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class LoginHelper {

    public LoginHelper(){}
    
    public boolean isExistNick(String nick){
        boolean ret;
        try {
            ret=findNick(nick);
        } catch (Exception ex) {
            ret=false;
            Logger.debug(ex, "Nick '{}' no existene ",nick);
        }
        return ret;       
    }
    
    public boolean isExistEmail(String email){
        boolean ret;
        try {
            ret=findEmail(email);
        } catch (Exception ex) {
            ret=false;
            Logger.debug(ex, "Email '{}' no existene ",email);
        }
        return ret;
    }

   public DtUsuario validarNick(String nickname, String pass){
       DtUsuario ret;
       try {
           ret=validateNick(nickname, pass);
       } catch(Exception e) { ret=null; }
       return ret;
   }
   
   public DtUsuario validarEmail(String email, String pass){
       DtUsuario ret;
       try {
           ret=validateEmail(email, pass);
       } catch(Exception e) { ret=null; }
       return ret;
   }
    
//   public DtSessionUsuario validarNick(String nickname, String pass){
//       DtSessionUsuario ret;
//       try {
//           ret=validateNick(nickname, pass);
//       } catch(Exception e) { ret=null; }
//       return ret;
//   }
//   
//   public DtSessionUsuario validarEmail(String email, String pass){
//       DtSessionUsuario ret;
//       try {
//           ret=validateEmail(email, pass);
//       } catch(Exception e) { ret=null; }
//       return ret;
//   }

    private boolean findNick(String nick) throws Exception{
//        return Fabrica.getInstance().getIControlador().existeUsuario(nick); // **** Descomentar para ejecutar ****
        return Fabrica.getConexion().existeUsuario(nick); // **** Comentar para compilar ****
    }

    private boolean findEmail(String email) throws Exception{
//        return Fabrica.getInstance().getIControlador().existeEmail(email);  // **** Descomentar para ejecutar ****
        return Fabrica.getConexion().existeEmail(email);  // **** Comentar para compilar ****
    }
    
    private DtUsuario validateNick(String nick, String pass) throws Exception{ 
//        return Fabrica.getInstance().getIControlador().validarNick(nick, pass); // **** Descomentar para ejecutar ****
        return Fabrica.getConexion().validarNick(nick, pass); // **** Comentar para compilar ****
    }
    
    private DtUsuario validateEmail(String email, String pass) throws Exception{
//        return Fabrica.getInstance().getIControlador().validarEmail(email, pass);   // **** Descomentar para ejecutar ****
        return Fabrica.getConexion().validarEmail(email, pass);   // **** Comentar para compilar ****
    }

//    private DtSessionUsuario validateNick(String nick, String pass) throws Exception{
//        return Fabrica.getConexion().validarNick(nick, pass);
//    }
//    
//    private DtSessionUsuario validateEmail(String email, String pass) throws Exception{
//        return Fabrica.getConexion().validarEmail(email, pass);
//    }
    
}