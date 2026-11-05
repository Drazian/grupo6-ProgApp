package edu.edext.logica;
//**************************** Capa Logica *************************************

import edu.edext.datatypes.DtCursoVisitas;
import edu.edext.entitys.Curso;
import edu.edext.entitys.VisitasCurso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

/**
 * Requerimiento 7.3 "Listado de tendencias": cursos más visitados.
 * El Servidor Web registra una visita (con un filtro) cada vez que alguien consulta el detalle de un curso.
 */
public class TendenciasHelper {
    private final EntityManagerFactory DB;

    public TendenciasHelper(EntityManagerFactory obj){
        DB = obj;
    }

    /**
     * Suma una visita al curso. Devuelve false si el curso no existe (no se cuenta).
     * El incremento es un UPDATE atómico; la primera visita crea la fila (si dos llegan a la vez, se reintenta).
     */
    public boolean registrarVisita(String nombreCurso) {
        if (nombreCurso == null || nombreCurso.isBlank()) return false;
        for (int intento = 1; intento <= 2; intento++) {
            EntityManager em = DB.createEntityManager();
            try {
                em.getTransaction().begin();
                if (em.find(Curso.class, nombreCurso) == null) {
                    em.getTransaction().rollback();
                    return false;
                }
                int filas = em.createQuery(
                    "UPDATE VisitasCurso v SET v.cantidad = v.cantidad + 1, v.ultimaVisita = :ahora WHERE v.curso = :curso")
                    .setParameter("ahora", LocalDateTime.now())
                    .setParameter("curso", nombreCurso)
                    .executeUpdate();
                if (filas == 0)
                    em.persist(new VisitasCurso(nombreCurso, 1, LocalDateTime.now()));
                em.getTransaction().commit();
                return true;
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                if (intento == 2) {
                    Logger.warn(e, "No se pudo registrar la visita al curso {}", nombreCurso);
                    return false;
                }
                // Otra consulta creó la fila al mismo tiempo: el segundo intento hace el UPDATE
            } finally {
                if (em.isOpen()) em.close();
            }
        }
        return false;
    }

    /** Cursos consultados al menos una vez, del más visitado al menos visitado (empates por nombre). */
    public List<DtCursoVisitas> listarMasVisitados() {
        try (EntityManager em = DB.createEntityManager()) {
            List<Object[]> filas = em.createQuery(
                "SELECT c, v.cantidad FROM VisitasCurso v, Curso c " +
                "WHERE c.nombre = v.curso AND v.cantidad > 0 " +
                "ORDER BY v.cantidad DESC, c.nombre", Object[].class)
                .getResultList();

            List<DtCursoVisitas> ret = new ArrayList<>();
            int posicion = 0;
            for (Object[] fila : filas) {
                Curso c = (Curso) fila[0];
                String instituto = c.getInstituto() != null ? c.getInstituto().getNombre() : null;
                ret.add(new DtCursoVisitas(++posicion, c.getNombre(), instituto, c.getImagen(), ((Number) fila[1]).longValue()));
            }
            return ret;
        }
    }
}
