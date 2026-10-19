package edu.edext.tools;
//************************* Capa Presentacion **********************************
import javax.swing.JList;
import javax.swing.Timer;
import java.awt.Component;
import javax.swing.JLabel;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 *
 * @author vdraco
 */
public class cronometroHelper {
    private final Timer cronoTimer;
    private final Component obj;
    private final JLabel message;
    private final JList lista;
    private final ArrayList<String> cronoError=new ArrayList<>();
    private boolean enable=true;

    
    public cronometroHelper(int milisegundos, Component objeto, JButton boton){
        this(milisegundos, objeto, null, boton, null);
    }
    
    public cronometroHelper(int milisegundos, Component objeto, JLabel mensaje){
        this(milisegundos, objeto, mensaje, null, null);
    }
    
    public cronometroHelper(int milisegundos, Component objeto, JLabel mensaje, JButton boton){
        this(milisegundos, objeto, mensaje,  boton, null);
    }
    
    public cronometroHelper(int milisegundos, Component objeto, JLabel mensaje, JButton boton, JList lista){
        obj=objeto;
        message=mensaje;
        this.lista=lista;
        cronoTimer=new Timer(milisegundos , e -> obj.setVisible(!obj.isVisible()));
    }
    public void alarm(){
        if(cronoTimer.isRunning()) alarm(false, "");
        else alarm(true, "");
    }

    public void alarm(boolean estado, String alert){
        if(enable){
            obj.setVisible(true);
            if(estado){
                if(!cronoError.contains(alert)){
                    cronoError.add(alert);
                    if(message!=null) message.setText(alert);
                }
                cronoTimer.start();
            }else{
                if(!cronoError.isEmpty() && cronoError.contains(alert)) cronoError.remove(cronoError.indexOf(alert));
                if(!cronoError.isEmpty()) message.setText(cronoError.getLast());
                if(cronoError.isEmpty()){
                    cronoTimer.stop();
                    if(message!=null) message.setText("");
                    if(obj!=null) obj.setVisible(false);
                }
            }
        }else kill();
    }
    
    public String[] ArrayNull(){
        String[] ret=null;
        return ret;
    }
    public String[] getLista(){
        return cronoError.toArray(String[]::new);
    }
    
    public int size(){
        return cronoError.size();
    }

    public boolean contains(String mensaje){
        return cronoError.contains(mensaje);
    }
    
    public boolean isEmpty(){
        return cronoError.isEmpty();
    }
    
    public String getLast(){
        return cronoError.getLast();
    }
    
    public String getFirst(){
        return cronoError.getFirst();
    }
    public void enabled(boolean estado){
        this.enable=estado;
    }
    
    public void kill(){
        cronoError.clear();
        cronoTimer.stop();
        this.enable=false;
        obj.setVisible(false);
    }
}