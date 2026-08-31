package com.edext.tools;
//************************* Capa Presentacion **********************************
import java.awt.Component;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 *
 * @author vdraco
 */
public class indexHelper {
    private JDesktopPane dpIndex;
    
    public indexHelper(JDesktopPane obj){
        this.dpIndex=obj;
    }
    
    public void cargarpanel(String titulo, Component panel, boolean unique, boolean rendOnDrag, int with, int height){
        cargarPanel(titulo, panel, unique, rendOnDrag, unique, unique, unique, unique, with, height);
    }
    
    public void cargarPanel(String titulo, Component panel, boolean unique, boolean rendOnDrag){
        cargarPanel(titulo, panel, unique, rendOnDrag, false, true, false, true);
    }
    
    public void cargarPanel(String titulo, Component panel, boolean unique,boolean rendOnDrag, boolean resizable, boolean closable, boolean maximizable, boolean minimizable){
        cargarPanel(titulo, panel, unique, rendOnDrag, resizable, closable, maximizable, minimizable, 0, 0);
    }
    private void cargarPanel(String titulo, Component panel, boolean unique,boolean rendOnDrag, boolean resizable, boolean closable, boolean maximizable, boolean minimizable, int with, int height){
        //JInternalFrame internalFrame;
        boolean flag=true;
        if (unique){
            for(JInternalFrame panels : this.dpIndex.getAllFrames()) {
                if(panels.getContentPane().getComponentCount()>0){
                    Component internalPanel=panels.getContentPane().getComponent(0);
                    if(internalPanel.getClass().equals(panel.getClass())){
                        flag=false;
                    try{
                        if(panels.isIcon()) panels.setIcon(false);
                        panels.toFront();
                        panels.setSelected(true);
                    }catch(PropertyVetoException e){}
                    break;                    
                    }
                }
            }
        }
        if(flag){
            JInternalFrame internalFrame;
            try{
                if(panel instanceof JPanel) {
                    internalFrame= new JInternalFrame(titulo, resizable, closable, maximizable, minimizable);
                    internalFrame.getContentPane().add(panel);
                    internalFrame.pack();
                    this.dpIndex.add(internalFrame);
                    if(!rendOnDrag)  this.dpIndex.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE); // Modo de renderizado
                    internalFrame.setVisible(true);   
                }else if(panel instanceof JInternalFrame){
                    // para implementar
                }
            }catch(Exception ex){
                System.err.println("Error al cargar el Panel <" + titulo + ">");
                System.err.println(ex);}
        }
    }
}
