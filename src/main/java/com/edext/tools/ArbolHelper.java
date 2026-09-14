/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.edext.tools;

import com.edext.datatypes.DtInstituto;
import com.edext.logica.Fabrica;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class ArbolHelper {
    
    public boolean createArbol(JTree objTree){
        return makeArbol(objTree) && loadArbol(objTree);
    }
    
    private boolean makeArbol(JTree obj){
        boolean ret=false;
        try {
            DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Institutos de Educación");
            List<DtInstituto> institutos = Fabrica.getInstance().getIControlador().listarInstitutos();
            for (DtInstituto inst : institutos) {
                DefaultMutableTreeNode nodoInstituto = new DefaultMutableTreeNode(inst.getNombre());
                raiz.add(nodoInstituto);
                List<String> cursos = Fabrica.getInstance().getIControlador().listarCursosPorInstituto(inst.getNombre());
                for (String curso : cursos) {
                    DefaultMutableTreeNode nodoCurso = new DefaultMutableTreeNode(curso);
                    nodoInstituto.add(nodoCurso);
                    List<String> ediciones = Fabrica.getInstance().getIControlador().listaEdicionPorCurso(curso); 
                    for (String edicion : ediciones) {
                        DefaultMutableTreeNode nodoEdicion = new DefaultMutableTreeNode(edicion);
                        nodoCurso.add(nodoEdicion);
                    }
                }
            }
            obj.setModel(new DefaultTreeModel(raiz));
            
//        // 5. Inyectamos el modelo cargado al JTree de Swing
//        treeExplorador.setModel(new DefaultTreeModel(raiz));
//        
        // 🚀 EL TOQUE FINAL: Expandir todos los nodos automáticamente

            
            ret=true;
        } catch (Exception e) { 
            ret=false;
            Logger.error(e, "Error al construir el Arbol");
        //JOptionPane.showMessageDialog(this, "No se pudo cargar el explorador visual: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return  ret;
    }   
    
    public boolean loadArbol(JTree obj){
        boolean ret=false;
        try {
            DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Plataforma Educativa");
            List<DtInstituto> institutos = Fabrica.getInstance().getIControlador().listarInstitutos();
            for (DtInstituto inst : institutos) {
                DefaultMutableTreeNode nodoInstituto = new DefaultMutableTreeNode(inst.getNombre());
                raiz.add(nodoInstituto);
                List<String> cursos = Fabrica.getInstance().getIControlador().listarCursosPorInstituto(inst.getNombre());
                for (String curso : cursos) {
                    DefaultMutableTreeNode nodoCurso = new DefaultMutableTreeNode(curso);
                    nodoInstituto.add(nodoCurso);
                    List<String> ediciones = Fabrica.getInstance().getIControlador().listaEdicionPorCurso(curso); 
                    for (String edicion : ediciones) {
                        DefaultMutableTreeNode nodoEdicion = new DefaultMutableTreeNode(edicion);
                        nodoCurso.add(nodoEdicion);
                    }
                }
            }
            obj.setModel(new DefaultTreeModel(raiz));
            for(int i = 0; i < obj.getRowCount(); i++) obj.expandRow(i);
            ret=true;
        } catch (Exception e) {
            org.tinylog.Logger.error(e, "Error al rellenar el Arbol");
            //JOptionPane.showMessageDialog(this, "Error al cargar el árbol: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return ret;
    }    
}
