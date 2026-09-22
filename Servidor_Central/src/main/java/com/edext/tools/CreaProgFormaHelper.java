package com.edext.tools;
//************************* Capa Presentacion **********************************
import com.edext.datatypes.DtPrograma;
import com.edext.datatypes.DtInstituto;
import com.edext.logica.Fabrica;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vdraco
 */
public class CreaProgFormaHelper{
 
    private Set<String> listData = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    //private List<String> listaData=new ArrayList<>();
     
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
        setLista();
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
    
    public void setLista(){
        listData.clear();
        try {
            for(DtPrograma dto : getListProgram()) listData.add(dto.getNombre());
            
        } catch (Exception e) {
        }
{
            
        }
    }
    
    
    
    //******************** Pipeline Presentacion -> Logica *********************
    
    public boolean setData(String nom, String desc, LocalDate fReg, LocalDate fStart, LocalDate fEnd) throws Exception{
        return sendData(new DtPrograma(nom, desc, fReg, fStart, fEnd));
    }
    
    private boolean sendData(DtPrograma DTO) throws Exception{
        Fabrica pipeline=Fabrica.getInstance();
        return pipeline.getIControlador().setCrearProgramaFormacion(DTO);
    }
    
    private List<DtPrograma> getListProgram() throws Exception{
        Fabrica pipeline=Fabrica.getInstance();
        return pipeline.getIControlador().listarProgramas();
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
