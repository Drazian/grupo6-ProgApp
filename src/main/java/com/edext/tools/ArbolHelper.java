package com.edext.tools;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.edext.datatypes.DtInstituto;
import com.edext.logica.Fabrica;
import org.tinylog.Logger;
import javax.swing.JTree;
import java.util.List;

/**
 *
 * @author vdraco
 */
public class ArbolHelper {
    
    // ********************** Modelo totalmente desacoplado ********************
    public DefaultTreeModel getModel(){
        return make();
    }

    private DefaultTreeModel make(){
        Pipe arbol=new Pipe();
        DefaultTreeModel ret=new DefaultTreeModel(arbol.create());
        arbol=null;
        return ret;
    }

    private class Pipe{
        private DefaultMutableTreeNode create(){
            DefaultMutableTreeNode raiz;
            try {
                raiz = new DefaultMutableTreeNode("Plataforma Educativa edEXT");
                List<DtInstituto> institutos = Fabrica.getInstance().getIControlador().listarInstitutos();
                if(!institutos.isEmpty())
                    for(DtInstituto inst : institutos){
                        DefaultMutableTreeNode nodoInstituto = new DefaultMutableTreeNode(inst.getNombre());
                        raiz.add(nodoInstituto);
                        List<String> cursos = Fabrica.getInstance().getIControlador().listarCursosPorInstituto(inst.getNombre());
                        if(!cursos.isEmpty())
                            for(String curso : cursos){
                                DefaultMutableTreeNode nodoCurso = new DefaultMutableTreeNode(curso);
                                nodoInstituto.add(nodoCurso);
                                List<String> ediciones = Fabrica.getInstance().getIControlador().listaEdicionPorCurso(curso); 
                                if(!ediciones.isEmpty())
                                    for(String edicion : ediciones){
                                        DefaultMutableTreeNode nodoEdicion = new DefaultMutableTreeNode(edicion);
                                        nodoCurso.add(nodoEdicion);
                                        List<String> inscripciones = Fabrica.getInstance().getIControlador().getEstudiantesInscriptosEdicion(edicion);
                                        if(!inscripciones.isEmpty())
                                            for (String inscripcion : inscripciones){
                                                DefaultMutableTreeNode nodoInscripcion = new DefaultMutableTreeNode(inscripcion);
                                                nodoEdicion.add(nodoInscripcion);
                                            }else
                                            nodoEdicion.add(new DefaultMutableTreeNode("vacio"));
                                    }else
                                    nodoCurso.add(new DefaultMutableTreeNode("vacio"));                                    
                            }else
                            nodoInstituto.add(new DefaultMutableTreeNode("vacio"));
                    }else
                    raiz.add(new DefaultMutableTreeNode("vacio"));
            } catch (Exception e) { 
                raiz=new DefaultMutableTreeNode("Error al crear el Arbol");
                Logger.error(e, "Error al crear el Arbol"); }
            return raiz;
        }
    }
    
    //************************* Modelo Original ********************************
    public boolean createArbol(JTree objTree){
        return loadArbol(objTree);
    }
    
    private boolean loadArbol(JTree obj){
        boolean ret=false;
        try {
            DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Plataforma Educativa edEXT");  // modique de titulo para agregar el nombre de la aplicacion
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
                        List<String> inscripciones = Fabrica.getInstance().getIControlador().getEstudiantesInscriptosEdicion(edicion); // nueva 1
                        for (String inscripcion : inscripciones) {                                                                  // nueva 2
                            DefaultMutableTreeNode nodoInscripcion = new DefaultMutableTreeNode(inscripcion);                    // nueva 3
                            nodoEdicion.add(nodoInscripcion);                                                                 // nueva 4
                        }
                    }
                }
            }
            obj.setModel(new DefaultTreeModel(raiz));
            for(int i = 0; i < obj.getRowCount(); i++) obj.expandRow(i);
            ret=true;
        } catch (Exception e) { Logger.error(e, "Error al rellenar el Arbol"); }
        return ret;
    }    
    //**************************************************************************
}
