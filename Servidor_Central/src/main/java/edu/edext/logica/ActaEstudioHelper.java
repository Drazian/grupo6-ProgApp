package edu.edext.logica;
//**************************** Capa Logica *************************************

import edu.edext.datatypes.DtActaEstudio;
import edu.edext.datatypes.DtResultadoCurso;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import edu.edext.entitys.Curso;
import edu.edext.entitys.Edicion;
import edu.edext.entitys.Estudiante;
import edu.edext.entitys.InscripcionEdicion;
import edu.edext.entitys.ProgramaFormacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CU "Tramitar Acta de Estudio" (Estudiante) y requerimiento 7.4.
 * Arma, para un programa de formacion en el que el estudiante esta inscripto, el resultado de cada curso del programa:
 * nota, aprobacion y fecha de resultado (fecha de cierre de la edicion). El PDF lo genera el Servidor Web.
 */
public class ActaEstudioHelper {
    /** Nota minima de aprobacion en la escala 1 a 12. */
    public static final int NOTA_MINIMA_APROBACION = 6;
    private static final int ACEPTADO = 2;

    private final EntityManagerFactory DB;

    public ActaEstudioHelper(EntityManagerFactory obj){
        DB = obj;
    }

    public DtActaEstudio getActa(String nicknameEstudiante, String nombrePrograma) {
        try (EntityManager em = DB.createEntityManager()) {
            Estudiante estudiante = em.find(Estudiante.class, nicknameEstudiante);
            if (estudiante == null)
                throw new IllegalArgumentException("Solo los estudiantes pueden tramitar el acta de estudio.");

            ProgramaFormacion programa = em.find(ProgramaFormacion.class, nombrePrograma);
            if (programa == null)
                throw new IllegalArgumentException("El programa de formación '" + nombrePrograma + "' no existe.");

            Long inscripto = em.createQuery(
                "SELECT COUNT(ip) FROM InscripcionPrograma ip WHERE ip.estudiante.nickname = :nick AND ip.programa.nombre = :prog", Long.class)
                .setParameter("nick", nicknameEstudiante)
                .setParameter("prog", nombrePrograma)
                .getSingleResult();
            if (inscripto == 0)
                throw new IllegalArgumentException("No está inscripto en el programa de formación '" + nombrePrograma + "'.");

            List<Curso> cursos = new ArrayList<>(programa.getCursos() != null ? programa.getCursos() : List.of());
            cursos.sort(Comparator.comparing(Curso::getNombre, String.CASE_INSENSITIVE_ORDER));

            List<DtResultadoCurso> resultados = new ArrayList<>();
            for (Curso curso : cursos)
                resultados.add(resultadoDelCurso(em, nicknameEstudiante, curso));

            DtUsuario dtEstudiante = new DtUsuario(estudiante.getNickname(), null, estudiante.getEmail(), estudiante.getNombre(),
                    estudiante.getApellido(), null, null, null, TipoUsuario.ESTUDIANTE);
            return new DtActaEstudio(dtEstudiante, programa.getNombre(), programa.getDescripcion(),
                    programa.getFechaInicio(), programa.getFechaFin(), LocalDate.now(), NOTA_MINIMA_APROBACION, resultados);
        }
    }

    //***************************** pipeline ***********************************
    /**
     * Resultado del estudiante en un curso, segun sus inscripciones aceptadas a las ediciones del curso:
     * 1) si aprobo alguna edicion cerrada, la aprobacion mas reciente;
     * 2) si no, la ultima edicion cerrada con nota (No aprobado);
     * 3) si esta aceptado en una edicion todavia abierta, En curso;
     * 4) si no, No cursado.
     */
    private DtResultadoCurso resultadoDelCurso(EntityManager em, String nicknameEstudiante, Curso curso) {
        List<InscripcionEdicion> aceptadas = em.createQuery(
            "SELECT i FROM InscripcionEdicion i WHERE i.estudiante.nickname = :nick AND i.edicion.curso.nombre = :curso AND i.estado = :aceptado",
            InscripcionEdicion.class)
            .setParameter("nick", nicknameEstudiante)
            .setParameter("curso", curso.getNombre())
            .setParameter("aceptado", ACEPTADO)
            .getResultList();

        InscripcionEdicion aprobada = null, reprobada = null, enCurso = null;
        for (InscripcionEdicion i : aceptadas) {
            Edicion ed = i.getEdicion();
            if (ed.isCerrada() && i.getNota() != null) {
                if (i.getNota() >= NOTA_MINIMA_APROBACION) {
                    if (aprobada == null || ed.getFechaCierre().isAfter(aprobada.getEdicion().getFechaCierre())) aprobada = i;
                } else if (reprobada == null || ed.getFechaCierre().isAfter(reprobada.getEdicion().getFechaCierre())) {
                    reprobada = i;
                }
            } else if (!ed.isCerrada() && (enCurso == null || ed.getFechaInicio().isAfter(enCurso.getEdicion().getFechaInicio()))) {
                enCurso = i;
            }
        }

        if (aprobada != null)
            return fila(curso, aprobada, DtResultadoCurso.APROBADO);
        if (reprobada != null)
            return fila(curso, reprobada, DtResultadoCurso.NO_APROBADO);
        if (enCurso != null)
            return new DtResultadoCurso(curso.getNombre(), curso.getCreditos(), enCurso.getEdicion().getNombre(), null, DtResultadoCurso.EN_CURSO, null);
        return new DtResultadoCurso(curso.getNombre(), curso.getCreditos(), null, null, DtResultadoCurso.NO_CURSADO, null);
    }

    private static DtResultadoCurso fila(Curso curso, InscripcionEdicion i, String resultado) {
        return new DtResultadoCurso(curso.getNombre(), curso.getCreditos(), i.getEdicion().getNombre(),
                i.getNota(), resultado, i.getEdicion().getFechaCierre());
    }
}
