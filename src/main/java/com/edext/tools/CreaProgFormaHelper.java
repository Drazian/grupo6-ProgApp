package com.edext.tools;
//************************* Capa Presentacion **********************************
import com.edext.datatypes.DTPrograma;
import com.edext.logica.Fabrica;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vdraco
 */
public class CreaProgFormaHelper{
 
    private DTPrograma dtPrograma;
    private final Set<String> listData = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
     
    public CreaProgFormaHelper(){
        
        
        listData.add("Programa 1 ");
        listData.add("Edicion 2 ");
        listData.add("Inscripcion 3 ");
        listData.add("Cursos 6 ");
    }
    
    public String[] getArray(ArrayList<String> obj){
        return obj.toArray(String[]::new);
    }
    
    public String[] getList(){
        return listData.toArray(String[]::new);
    }
    public Set<String> getLista(){
        return listData;
    }
    
    public String[] ArrayNull(){
        String[] ret=null;
        return ret;
    }
    
    public boolean isExist(String nombre){
        return listData.contains(nombre);
    }
    
    //******************** Pipeline Presentacion -> Logica *********************
    
    public boolean setData(String nom, String desc, LocalDate fReg, LocalDate fStart, LocalDate fEnd) throws Exception{
        return sendData(new DTPrograma(nom, desc, fReg, fStart, fEnd));
    }
    
    private boolean sendData(DTPrograma DTO) throws Exception{
        Fabrica pipeline=Fabrica.getInstance() ;
        return pipeline.getIControlador().setCrearProgramaFormacion(DTO);
    }
    //**************************************************************************
//   private boolean validateData(DTPrograma DTO){
//        boolean flag=true;
//        if(isExist(DTO.getNombre())) flag=false;
//        if(DTO.getFechaRegistro()==null) flag=false;
//        if(DTO.getFechaInicio()==null) flag=false;
//        if(DTO.getFechaFin()==null) flag=false;
//        if(DTO.getFechaInicio().isAfter(DTO.getFechaFin())) flag=false;
//        return flag;
//    }

   
//    private boolean validateData(String nom, String desc, LocalDate fReg, LocalDate fStart, LocalDate fEnd){
//        boolean flag=true;
//        if(isExist(nom)){
//            flag=false;
//            showMessageDialog(null, "Nombre Existente");
//        }
//        if(fReg==null) flag=false;
//        if(fStart==null) flag=false;
//        if(fEnd==null) flag=false;
//        if(fStart.isAfter(fEnd)){
//            flag=false;
//            showMessageDialog(null, "Fecha de Inicio porterior a Fecha de Finalizacion");
//        }
//        return flag;
//    }

//    private void probar(DTPrograma DTO){
//        testPrograma tmpFrm=new testPrograma();
//        tmpFrm.setVisible(true);
//        tmpFrm.setSize(300, 400);
//        tmpFrm.setData(DTO);
//        tmpFrm.setVisible(true);
//    }
    
}
