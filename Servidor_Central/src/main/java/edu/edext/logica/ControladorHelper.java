package edu.edext.logica;

import jakarta.persistence.EntityManagerFactory;

/**
 *
 * @author vdraco
 */
public class ControladorHelper {
    private final EntityManagerFactory DB;
    
    public ControladorHelper(EntityManagerFactory obj){ DB=obj; }
    



    public void kill(){
        // Todavia no abre EntityManager propio: no hay nada que liberar
    }
    

    
    //***************************** pipeline ***********************************
    

//    public List<Estudiante> obtenerEstudiantesSinInscripciones(){
//        db=DB.createEntityManager();
//        List<String> ret;
//        ret=db.createQuery("SELECT e FROM Estudiante e WHERE NOT EXISTS (SELECT ie FROM InscripcionEdicion ie WHERE ie.estudiante = e) AND NOT EXISTS (SELECT ip FROM InscripcionPrograma ip WHERE ip.estudiante = e", String.class).getResultList();
//                
//                
//    //TypedQuery<Estudiante> query = em.createQuery(jpql, Estudiante.class);
//    return query.getResultList();                
//}

}
