package com.edext.tools;
//************************ Capa presentacion ***********************************
import com.edext.datatypes.DtEdicion;
import com.edext.datatypes.DtInstituto;
import com.edext.logica.Fabrica;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.mapping.Array;
import org.tinylog.Logger;

/**
 *
 * @author vdraco
 */
public class ConsultaEdicionHelper {
    private List<String> listIntitutos=new ArrayList<>();
    private List<String> listCursos=new ArrayList<>();
    private List<String> listEdiciones=new ArrayList<>();

    private String nombre;
    private Integer cupo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaPublicacion;
    private String curso;
    private List<String> docentes;
    private boolean isEmpty;
    
    
    public ConsultaEdicionHelper(){ listIntitutos=getInstitutos(); }
    
    public void refreshInstituto(){ listIntitutos=getInstitutos(); }

//    public class DTO{
//        private final DtEdicion data;
//        private boolean flag;
//        public DTO(DtEdicion dto){
//            if(dto==null) flag=true;
//            data=dto;
//        }
//        public String getNombre(){ return data.getNombre(); }
//        public Integer getCupo(){ return data.getCupo(); }
//        public LocalDate getFechaInicio(){ return data.getFechaInicio(); }
//        public LocalDate getFechaFin(){ return data.getFechaFin(); }
//        public LocalDate getFechaPublicacion(){ return data.getFechaPublicacion(); }
//        public String getCurso(){ return data.getCurso().getNombre(); }
//        public List<String> getDocentes(){ return new ArrayList<>(data.getNameDocentes()); }
//        public boolean isEmpty(){ return flag; }
//    }


   
    public String getNombre(){ return nombre; }
    public Integer getCupo(){ return cupo; }
    public LocalDate getFechaInicio(){ return fechaInicio; }
    public LocalDate getFechaFin(){ return fechaFin; }
    public LocalDate getFechaPublicacion(){ return fechaPublicacion; }
    public String getCurso(){ return curso; }
    public List<String> getDocentes(){ return docentes; }
    public boolean isEmpty(){ return isEmpty; }

    
    public boolean getData(String nombre){
        return getEdicion(nombre);
    }
    
    
    public String[] getListCursos(String intituto){
        listCursos=getCursos(intituto);
        return listCursos.toArray(String[]::new);
    }
    
    public String[] getListInstitutos(){
        return listIntitutos.toArray(String[]::new);
    }
    
    public String [] getListaEdiciones(String curso){
        listCursos=getEdiciones(curso);
        return listCursos.toArray(String[]::new);
    }
    
    private boolean getEdicion(String nombre){
        boolean ret=true;
        DtEdicion tmpEdicion;
        try { 
            tmpEdicion=new Pipe().getedicion(nombre);
            Logger.debug(" nombre : {}",tmpEdicion.getNombre());
            this.isEmpty=false;
            this.cupo=tmpEdicion.getCupo();
            this.nombre=tmpEdicion.getNombre();
            this.fechaFin=tmpEdicion.getFechaFin();
            this.fechaInicio=tmpEdicion.getFechaInicio();
            this.curso=tmpEdicion.getCurso().getNombre();
            this.fechaPublicacion=tmpEdicion.getFechaPublicacion();
            this.docentes=new ArrayList<>(tmpEdicion.getNameDocentes());
            tmpEdicion=null;
        } catch (Exception e) { 
            ret=false; 
            cereo();
            Logger.debug("No entre");
        }
        return ret;
    }
    
    private void cereo(){
        this.cupo=0;
        this.curso="";
        this.nombre="";
        this.isEmpty=true;
        this.fechaFin=null;
        this.fechaInicio=null;
        this.fechaPublicacion=null;
        this.docentes=new ArrayList<>();
    }
    
    private List<String> getInstitutos(){
        Pipe tmp=new Pipe();
        List<String> ret;
        try { ret=tmp.getPipeInstitutos(); }
        catch (Exception e) { ret=new ArrayList<>(); }
        tmp=null;
        return ret;
    }
    
    private List<String> getCursos(String intituto){
        Pipe tmp=new Pipe();
        List<String> ret;
        try { ret=tmp.getPipeCursos(intituto); }
        catch (Exception e) { ret=new ArrayList<>(); }
        tmp=null;
        return ret;
    }
    
    private List<String> getEdiciones(String curso){
        Pipe tmpPipe=new Pipe();
        List<String> ret=new ArrayList<>();
        List<DtEdicion> tmp;
        try { tmp=tmpPipe.getediciones(curso);
                  for(DtEdicion tmpDTO : tmp)
                      ret.add(tmpDTO.getNombre());
        } catch (Exception e) { ret=new ArrayList<>(); }
        tmpPipe=null;
        tmp=null;
        return  ret;
    }




    
    //****************************************************************
    
    
    
    
    
    
    //******************** Pipeline Presentacion -> Logica *********************
    
    private class Pipe{
        
        private List<String> getPipeInstitutos() throws Exception{
            List<String> retList=new ArrayList<>();
            for(DtInstituto tmpInstituto : Fabrica.getInstance().getIControlador().listarInstitutos())
                retList.add(tmpInstituto.getNombre());
            return retList;
        }
        
        private List<String> getPipeCursos(String instituto) throws Exception{
            return Fabrica.getInstance().getIControlador().listarCursosPorInstituto(instituto);
        }

        private List<DtEdicion> getediciones(String curso) throws Exception{
            return Fabrica.getInstance().getIControlador().listarEdicionPorCurso(curso);
        }

        private DtEdicion getedicion(String nombre) throws Exception{
            return Fabrica.getInstance().getIControlador().getEdicion(nombre);
        }
        
    }//*************************************************************************
    
}
