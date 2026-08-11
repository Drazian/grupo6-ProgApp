package com.edext.logica;
import com.edext.datatypes.DtInstituto;
import com.edext.persistencia.Instituto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class Controlador implements IControlador {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("edext");
    
    @Override
    public void crearInstituto(String nombre) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            //Verifico primero si existe un Instituto con ese nombre (aux), de no ser asi lo creo. De existir, envio exception.
            Instituto aux = em.find(Instituto.class,nombre); 
            if (aux == null){
                        Instituto aux2 = new Instituto(nombre);
                        em.persist(aux2);
                        em.getTransaction().commit();
            } else {
                throw new Exception("El instituto con el nombre '"+nombre+"' ya existe.");
            }
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
    @Override
    public void eliminarInstituto(String nombre) throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Instituto aux = em.find(Instituto.class, nombre);
            
            if (aux == null){
                throw new Exception ("No se encontró el Instituto con el nombre '"+nombre+"'.");
            }
            
            em.remove(aux);
            
            em.getTransaction().commit();        
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
    @Override
    public List<DtInstituto> listarInstitutos() throws Exception{
        EntityManager em = emf.createEntityManager();
        try {
            List<DtInstituto> resultado = new ArrayList<>();
            
            List<Instituto> listaAux = em.createQuery("SELECT i FROM Instituto i",Instituto.class).getResultList();
            for (Instituto aux: listaAux){
                resultado.add(new DtInstituto(aux.getNombre()));
            }
            return resultado;     
        } catch (Exception e) {if (em.getTransaction().isActive()) {em.getTransaction().rollback();}throw e;} finally {em.close();}
    }
    
    
    
    
    
    
}
