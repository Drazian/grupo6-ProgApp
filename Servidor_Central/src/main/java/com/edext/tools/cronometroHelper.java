package com.edext.tools;
//************************* Capa Presentacion **********************************
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.Timer;

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
//        if(lista!=null) lista.setListData(ArrayNull());   // debuger
//        if(lista!=null) lista.setListData(getLista());    // debuger
//        System.out.println(" elemntos : "+cronoError.size()); // debuger
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
    public void kill(){
        cronoError.clear();
        cronoTimer.stop();
    }
}
