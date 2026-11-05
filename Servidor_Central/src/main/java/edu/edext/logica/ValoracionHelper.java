package edu.edext.logica;
//**************************** Capa Logica *************************************

import edu.edext.datatypes.DtValoracionesCurso;
import edu.edext.entitys.Curso;
import edu.edext.entitys.Estudiante;
import edu.edext.entitys.ValoracionCurso;
import edu.edext.entitys.ValoracionCursoID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;

/**
 * CU "Valorar un Curso" (Estudiante) y requerimiento 7.6 (visualizacion grafica de puntajes).
 * Un estudiante participa de un curso si tiene una inscripcion "Aceptada" en alguna de sus ediciones.
 */
public class ValoracionHelper {
    private static final int ACEPTADO = 2;
    private static final int PUNTAJE_MIN = 1, PUNTAJE_MAX = 5;

    private final EntityManagerFactory DB;

    public ValoracionHelper(EntityManagerFactory obj){
        DB = obj;
    }

    /** Cursos en los que participa el estudiante (en cualquier edicion), en orden alfabetico. */
    public List<String> listarCursosQueParticipa(String nicknameEstudiante) {
        try (EntityManager em = DB.createEntityManager()) {
            return em.createQuery(
                "SELECT DISTINCT i.edicion.curso.nombre FROM InscripcionEdicion i " +
                "WHERE i.estudiante.nickname = :nick AND i.estado = :aceptado " +
                "ORDER BY i.edicion.curso.nombre", String.class)
                .setParameter("nick", nicknameEstudiante)
                .setParameter("aceptado", ACEPTADO)
                .getResultList();
        }
    }

    /**
     * Resumen de las valoraciones del curso. Si se indica un estudiante, incluye su puntaje (si ya valoro)
     * y si participa del curso (es decir, si puede valorarlo).
     */
    public DtValoracionesCurso getValoraciones(String nombreCurso, String nicknameEstudiante) {
        try (EntityManager em = DB.createEntityManager()) {
            if (em.find(Curso.class, nombreCurso) == null)
                throw new IllegalArgumentException("El curso '" + nombreCurso + "' no existe.");

            int[] conteo = new int[PUNTAJE_MAX];
            List<Object[]> filas = em.createQuery(
                "SELECT v.puntaje, COUNT(v) FROM ValoracionCurso v WHERE v.curso.nombre = :curso GROUP BY v.puntaje", Object[].class)
                .setParameter("curso", nombreCurso)
                .getResultList();
            for (Object[] fila : filas)
                conteo[((Number) fila[0]).intValue() - 1] = ((Number) fila[1]).intValue();

            Integer propio = null;
            boolean participa = false;
            if (nicknameEstudiante != null) {
                ValoracionCurso v = em.find(ValoracionCurso.class, new ValoracionCursoID(nicknameEstudiante, nombreCurso));
                if (v != null) propio = v.getPuntaje();
                participa = participa(em, nicknameEstudiante, nombreCurso);
            }
            return new DtValoracionesCurso(nombreCurso, aLista(conteo), propio, participa);
        }
    }

    /** Resumen de valoraciones de todos los cursos que tienen al menos una (para los resultados de busqueda). */
    public List<DtValoracionesCurso> listarResumenes() {
        try (EntityManager em = DB.createEntityManager()) {
            Map<String, int[]> porCurso = new LinkedHashMap<>();
            List<Object[]> filas = em.createQuery(
                "SELECT v.curso.nombre, v.puntaje, COUNT(v) FROM ValoracionCurso v GROUP BY v.curso.nombre, v.puntaje", Object[].class)
                .getResultList();
            for (Object[] fila : filas)
                porCurso.computeIfAbsent((String) fila[0], k -> new int[PUNTAJE_MAX])
                        [((Number) fila[1]).intValue() - 1] = ((Number) fila[2]).intValue();

            List<DtValoracionesCurso> ret = new ArrayList<>();
            for (Map.Entry<String, int[]> e : porCurso.entrySet())
                ret.add(new DtValoracionesCurso(e.getKey(), aLista(e.getValue()), null, false));
            return ret;
        }
    }

    /** Registra la valoracion (1 a 5) del estudiante. Cada estudiante puede valorar un curso una unica vez. */
    public void valorar(String nicknameEstudiante, String nombreCurso, int puntaje) throws Exception {
        EntityManager em = DB.createEntityManager();
        try {
            em.getTransaction().begin();

            if (puntaje < PUNTAJE_MIN || puntaje > PUNTAJE_MAX)
                throw new IllegalArgumentException("El puntaje debe estar entre " + PUNTAJE_MIN + " y " + PUNTAJE_MAX + ".");

            Estudiante estudiante = em.find(Estudiante.class, nicknameEstudiante);
            if (estudiante == null)
                throw new IllegalArgumentException("Solo los estudiantes pueden valorar cursos.");

            Curso curso = em.find(Curso.class, nombreCurso);
            if (curso == null)
                throw new IllegalArgumentException("El curso '" + nombreCurso + "' no existe.");

            if (!participa(em, nicknameEstudiante, nombreCurso))
                throw new IllegalArgumentException("Solo puede valorar cursos en los que participa (inscripción aceptada en alguna edición).");

            if (em.find(ValoracionCurso.class, new ValoracionCursoID(nicknameEstudiante, nombreCurso)) != null)
                throw new IllegalArgumentException("Ya valoró el curso '" + nombreCurso + "': cada estudiante puede valorar un curso una única vez.");

            em.persist(new ValoracionCurso(estudiante, curso, puntaje, LocalDate.now()));
            em.getTransaction().commit();
            Logger.info("Valoracion registrada: " + nicknameEstudiante + " -> " + nombreCurso + " (" + puntaje + ")");
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            Logger.error(e, "Error al registrar la valoracion: ");
            throw new Exception("Ocurrió un error al guardar la valoración.");
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    //***************************** pipeline ***********************************
    private boolean participa(EntityManager em, String nicknameEstudiante, String nombreCurso) {
        Long n = em.createQuery(
            "SELECT COUNT(i) FROM InscripcionEdicion i " +
            "WHERE i.estudiante.nickname = :nick AND i.edicion.curso.nombre = :curso AND i.estado = :aceptado", Long.class)
            .setParameter("nick", nicknameEstudiante)
            .setParameter("curso", nombreCurso)
            .setParameter("aceptado", ACEPTADO)
            .getSingleResult();
        return n > 0;
    }

    private static List<Integer> aLista(int[] conteo) {
        List<Integer> ret = new ArrayList<>();
        for (int c : conteo) ret.add(c);
        return ret;
    }
}
