package edu.edext.logica;

import edu.edext.datatypes.DtCurso;
import edu.edext.datatypes.DtEdicion;
import edu.edext.datatypes.DtInstituto;
import edu.edext.datatypes.DtPrograma;
import edu.edext.datatypes.DtUsuario;
import edu.edext.datatypes.TipoUsuario;
import jakarta.persistence.EntityManagerFactory;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Datos de apoyo para los tests de la capa logica. Todo lo que se crea lleva un sufijo UUID,
 * asi los tests funcionan igual contra la base H2 vacia de los tests y contra una base que ya
 * tiene datos (nunca chocan con nombres existentes ni modifican datos ajenos).
 */
final class DatosUnicos {

    static final String PASSWORD = "clave123";

    private DatosUnicos() {}

    static String uid() {
        return UUID.randomUUID().toString();
    }

    /** prefijo-UUID */
    static String nombre(String prefijo) {
        return prefijo + "-" + uid();
    }

    static IControlador ic() {
        return Fabrica.getInstance().getIControlador();
    }

    /** El EntityManagerFactory que usa Controlador (el mismo que usan los helpers). */
    static EntityManagerFactory emf() throws Exception {
        new Controlador(); // fuerza la inicializacion estatica
        Field f = Controlador.class.getDeclaredField("emf");
        f.setAccessible(true);
        return (EntityManagerFactory) f.get(null);
    }

    static Set<String> set(String... valores) {
        return new HashSet<>(Arrays.asList(valores));
    }

    static Date fechaNacimiento() {
        return java.sql.Date.valueOf(LocalDate.of(2000, 1, 1));
    }

    static String instituto(IControlador ic) throws Exception {
        String nombre = nombre("Inst");
        ic.crearInstituto(nombre);
        return nombre;
    }

    static String categoria(IControlador ic) throws Exception {
        String nombre = nombre("Cat");
        ic.crearCategoria(nombre);
        return nombre;
    }

    static DtUsuario dtEstudiante(String nick) {
        return new DtUsuario(nick, PASSWORD, nick + "@test.uy", "nombre", "apellido", "img.png",
                fechaNacimiento(), null, TipoUsuario.ESTUDIANTE);
    }

    static String estudiante(IControlador ic) throws Exception {
        String nick = nombre("est");
        ic.crearUsuario(dtEstudiante(nick));
        return nick;
    }

    /** Docente con nombre "Doc<uid>" y apellido "Ape<uid>" (unicos, para buscarlo por "Nombre Apellido"). */
    static String docente(IControlador ic, String... institutos) throws Exception {
        String u = uid().substring(0, 8);
        String nick = "doc-" + uid();
        ic.crearUsuario(new DtUsuario(nick, PASSWORD, nick + "@test.uy", "Doc" + u, "Ape" + u, "img.png",
                fechaNacimiento(), Arrays.asList(institutos), TipoUsuario.DOCENTE));
        return nick;
    }

    static String curso(IControlador ic, String instituto, Set<String> previas, Set<String> categorias, String imagen) throws Exception {
        String nombre = nombre("Curso");
        ic.altaCurso(new DtCurso(nombre, "descripcion", "1 mes", 10, 2, "http://curso", new Date(),
                new DtInstituto(instituto), previas, categorias, imagen), instituto);
        return nombre;
    }

    static String curso(IControlador ic, String instituto) throws Exception {
        return curso(ic, instituto, new HashSet<>(), new HashSet<>(), "curso.png");
    }

    static String edicion(IControlador ic, String curso, Integer cupo, LocalDate inicio, LocalDate fin, String imagen, String... docentes) throws Exception {
        String nombre = nombre("Ed");
        ic.altaEdicionCurso(curso, new DtEdicion(nombre, cupo, inicio, fin, LocalDate.now(),
                new DtCurso(curso, null), null, set(docentes), imagen));
        return nombre;
    }

    /** Edicion vigente (termina dentro de un año) para poder inscribir estudiantes. */
    static String edicionVigente(IControlador ic, String curso, Integer cupo, String... docentes) throws Exception {
        return edicion(ic, curso, cupo, LocalDate.now(), LocalDate.now().plusYears(1), "ed.png", docentes);
    }

    static String programa(IControlador ic, Set<String> cursos, String docente) throws Exception {
        String nombre = nombre("Prog");
        boolean ok = ic.setCrearProgramaFormacion(new DtPrograma(nombre, "descripcion", LocalDate.now(),
                LocalDate.now(), LocalDate.now().plusMonths(6), null, cursos, "prog.png", docente));
        if (!ok) throw new IllegalStateException("No se pudo crear el programa " + nombre);
        return nombre;
    }

    static List<String> lista(String... valores) {
        return Arrays.asList(valores);
    }
}
