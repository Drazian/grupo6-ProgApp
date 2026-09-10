package com.edext.tools;
//************************* Capa Presentacion **********************************
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class indexHelper {
    private final JDesktopPane dpIndex;
    private boolean limitMin, activeScroll;
    private Dimension originalSize;
    private int panelX, panelY;
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
        this.originalSize=dpIndex.getPreferredSize();
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
                    if(!rendOnDrag)  this.dpIndex.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
                    internalFrame.setVisible(true);   
                }else if(panel instanceof JInternalFrame){
                    // para implementar
                }
                this.panelX=with;
                this.panelY=height;
            }catch(Exception ex){ Logger.debug("Error al cargar el Panel <{}> - Error : {}", titulo, ex.getMessage()); }
        }
    }
    
    public void assignScroll(){
        dpIndex.addContainerListener(new java.awt.event.ContainerAdapter(){
            @Override
            public void componentAdded(java.awt.event.ContainerEvent e){
                if(e.getChild() instanceof JInternalFrame iframe){
                    iframe.addComponentListener(new java.awt.event.ComponentAdapter() {
                        @Override
                        public void componentMoved(java.awt.event.ComponentEvent evt){ refreshPane(); 
                        }
                        @Override
                        public void componentResized(java.awt.event.ComponentEvent evt){ refreshPane(); }
                    });
                }
            }
        });
        dpIndex.setDesktopManager(new DefaultDesktopManager(){
            @Override
            public void dragFrame(JComponent f, int newX, int newY){
                if(limitMin){
                    if (newX < 0) newX = 0;
                    if (newY < 0) newY = 0;
                }
                super.dragFrame(f, newX, newY);
            }
        });
    } 
    
    private void refreshPane(){
        if(activeScroll){
           int maxX = 0, maxY = 0, rightX, bottomY;
            for(Component c : dpIndex.getComponents())
                if(c instanceof JInternalFrame){
                    rightX = c.getX() + c.getWidth();
                    bottomY = c.getY() + c.getHeight();
                    if(rightX > maxX) maxX = rightX;
                    if(bottomY > maxY) maxY = bottomY;
                }
            repaint(new Dimension(maxX, maxY));
        }
    }
    
    public void setScroll(boolean modo){
        if(!modo) repaint(originalSize);
        activeScroll=modo;
    }
    
    private void repaint(){
        dpIndex.revalidate();
        dpIndex.repaint();
    }
    private void repaint(Dimension d){
        dpIndex.setPreferredSize(d);
        repaint();
    }
    public void setLimiteMin(boolean modo){
        this.limitMin=modo;
    }
    public boolean isLimiteMin(){
        return limitMin;
    }
    public boolean isScrollActive(){
        return activeScroll;
    }
}
