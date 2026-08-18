package com.edext.presentacion;

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vdraco
 */
public class consEditCurso {
    
    private Set<String> listCursos = new TreeSet<>();
    
    public consEditCurso(int valor, String nombre){
        listCursos.add("Matematicas "+(valor+1)+" "+nombre);
        listCursos.add("Fisica "+(valor+1)+" "+nombre);
        listCursos.add("Quimica "+(valor+1)+" "+nombre);
        listCursos.add("Biologia "+(valor+1)+" "+nombre);
        listCursos.add("Ingles "+(valor+1)+" "+nombre);
        listCursos.add("Dibujo "+(valor+1)+" "+nombre);
    }
    
    public String[] getListCursos(){
        return listCursos.toArray(new String[0]);
    }
    
}
