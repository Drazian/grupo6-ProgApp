
package com.edext.presentacion;

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vdraco
 */
public class crearProgForma {
    
    private Set<String> listCursos = new TreeSet<>();
    
    public crearProgForma(){
        listCursos.add("Programa 1 ");
        listCursos.add("Programa 2 ");
        listCursos.add("Programa 3 ");
        listCursos.add("Programa 4 ");
        listCursos.add("Programa 5 ");
        listCursos.add("Programa 6 ");
    }
    
    public String[] getProgramas(){
        return listCursos.toArray(new String[0]);
    }
    
}
