package com.edext.logica;
//**************************** Capa Logica *************************************
import jakarta.persistence.EntityManagerFactory;
import com.edext.persistencia.InscripcionEdicion;
import com.edext.persistencia.ProgramaFormacion;
import com.edext.persistencia.Estudiante;
import jakarta.persistence.EntityManager;
import com.edext.persistencia.Instituto;
import com.edext.persistencia.Usuario;
import com.edext.persistencia.Docente;
import com.edext.persistencia.Edicion;
import com.edext.persistencia.Curso;
import java.util.ArrayList;
import java.time.LocalDate;
import org.tinylog.Logger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vdraco - codigo de diego
 */
public class cargarDatosDePruebaHelper {
    private final EntityManagerFactory emf;
    private EntityManager em;
    
    public cargarDatosDePruebaHelper(EntityManagerFactory emf){
        this.emf=emf;
    }
    
    public int cargar(){
        int ret=0;
        try { cargarDatosDePrueba(); }
        catch (Exception ex) {
            if(ex.getMessage() != null && ex.getMessage().equals("Los datos de prueba ya fueron cargados.")) ret=1;
            else{
                Logger.error(ex, "Error al cargar datos de Prueba");
                ret=-1;
            } }
        return ret;
    }
    
    public void kill(){
        if(em!=null && em.isOpen()) em.close();
        em=null;
    }
    
    
    //**************************************************************************
    private void cargarDatosDePrueba() throws Exception {

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            /*
             * ============================================================
             * VERIFICAR SI LOS DATOS DE PRUEBA YA FUERON CARGADOS
             * ============================================================
             */

            if (em.find(Instituto.class, "IN") != null
                    || em.find(Usuario.class, "eleven11") != null
                    || em.find(Curso.class, "Talleres plenarios") != null
                    || em.find(Edicion.class, "Flor del Ceibo - 2010") != null
                    || em.find(ProgramaFormacion.class, "EFI Ingeniería Mecánica") != null) {

                throw new Exception("Los datos de prueba ya fueron cargados.");
            }


            /*
             * ============================================================
             * 1. INSTITUTOS
             * ============================================================
             */

            Instituto in = new Instituto("INCO");
            Instituto il = new Instituto("IMERL");
            Instituto ifisica = new Instituto("Física");
            Instituto im = new Instituto("IMPII");
            Instituto ie = new Instituto("Eléctrica");
            Instituto di = new Instituto("DISI");

            em.persist(in);
            em.persist(il);
            em.persist(ifisica);
            em.persist(im);
            em.persist(ie);
            em.persist(di);


            /*
             * ============================================================
             * 2. USUARIOS
             *
             * Todas las imágenes utilizan usr.png.
             * ============================================================
             */

            String imagen = "usr.png";

            // ---------------- ESTUDIANTES ----------------

            Estudiante eleven11 = new Estudiante(
                    "eleven11",
                    "eleven11@gmail.com",
                    "Eleven",
                    "Twelve",
                    java.sql.Date.valueOf(LocalDate.of(1971, 12, 31)),
                    imagen
            );

            Estudiante costas = new Estudiante(
                    "costas",
                    "gcostas@gmail.com",
                    "Gerardo",
                    "Costas",
                    java.sql.Date.valueOf(LocalDate.of(1983, 11, 15)),
                    imagen
            );

            Estudiante roro = new Estudiante(
                    "roro",
                    "rcotelo@yahoo.com",
                    "Rodrigo",
                    "Cotelo",
                    java.sql.Date.valueOf(LocalDate.of(1975, 8, 2)),
                    imagen
            );

            Estudiante chechi = new Estudiante(
                    "chechi",
                    "cgarrido@hotmail.com",
                    "Cecilia",
                    "Garrido",
                    java.sql.Date.valueOf(LocalDate.of(1987, 9, 12)),
                    imagen
            );

            Estudiante jeffw = new Estudiante(
                    "jeffw",
                    "jwilliams@gmail.com",
                    "Jeff",
                    "Williams",
                    java.sql.Date.valueOf(LocalDate.of(1964, 11, 27)),
                    imagen
            );

            Estudiante weiss = new Estudiante(
                    "weiss",
                    "aweiss@hotmail.com",
                    "Adrian",
                    "Weiss",
                    java.sql.Date.valueOf(LocalDate.of(1978, 12, 23)),
                    imagen
            );

            em.persist(eleven11);
            em.persist(costas);
            em.persist(roro);
            em.persist(chechi);
            em.persist(jeffw);
            em.persist(weiss);


            // ---------------- DOCENTES ----------------

            List<Instituto> institutosWalter = new ArrayList<>();
            institutosWalter.add(in);

            Docente heisenberg = new Docente(
                    "heisenberg",
                    "heisenberg@gmail.com",
                    "Walter",
                    "White",
                    java.sql.Date.valueOf(LocalDate.of(1956, 3, 7)),
                    imagen,
                    institutosWalter
            );


            List<Instituto> institutosObi = new ArrayList<>();
            institutosObi.add(in);

            Docente benkenobi = new Docente(
                    "benkenobi",
                    "benKenobi@gmail.com",
                    "Obi-Wan",
                    "Kenobi",
                    java.sql.Date.valueOf(LocalDate.of(1914, 4, 2)),
                    imagen,
                    institutosObi
            );


            List<Instituto> institutosEmma = new ArrayList<>();
            institutosEmma.add(in);

            Docente waston = new Docente(
                    "waston",
                    "e.watson@gmail.com",
                    "Emma",
                    "Watson",
                    java.sql.Date.valueOf(LocalDate.of(1990, 4, 15)),
                    imagen,
                    institutosEmma
            );


            List<Instituto> institutosHouse = new ArrayList<>();
            institutosHouse.add(ie);

            Docente house = new Docente(
                    "house",
                    "greghouse@gmail.com",
                    "Gregory",
                    "House",
                    java.sql.Date.valueOf(LocalDate.of(1959, 5, 15)),
                    imagen,
                    institutosHouse
            );


            List<Instituto> institutosTim = new ArrayList<>();
            institutosTim.add(il);

            Docente timmy = new Docente(
                    "timmy",
                    "tim.cook@apple.com",
                    "Tim",
                    "Cook",
                    java.sql.Date.valueOf(LocalDate.of(1960, 11, 1)),
                    imagen,
                    institutosTim
            );


            List<Instituto> institutosDaniel = new ArrayList<>();
            institutosDaniel.add(il);

            Docente danny = new Docente(
                    "danny",
                    "dan.riccio@gmail.com",
                    "Daniel",
                    "Riccio",
                    java.sql.Date.valueOf(LocalDate.of(1963, 7, 5)),
                    imagen,
                    institutosDaniel
            );


            List<Instituto> institutosPhilip = new ArrayList<>();
            institutosPhilip.add(im);

            Docente phils = new Docente(
                    "phils",
                    "schiller@gmail.com",
                    "Philip",
                    "Schiller",
                    java.sql.Date.valueOf(LocalDate.of(1961, 10, 7)),
                    imagen,
                    institutosPhilip
            );


            List<Instituto> institutosBruce = new ArrayList<>();
            institutosBruce.add(di);

            Docente bruces = new Docente(
                    "bruces",
                    "sewell@gmail.com",
                    "Bruce",
                    "Sewell",
                    java.sql.Date.valueOf(LocalDate.of(1959, 12, 3)),
                    imagen,
                    institutosBruce
            );


            List<Instituto> institutosAdriana = new ArrayList<>();
            institutosAdriana.add(di);

            Docente adri = new Docente(
                    "adri",
                    "agarcia@gmail.com",
                    "Adriana",
                    "García",
                    java.sql.Date.valueOf(LocalDate.of(1978, 7, 28)),
                    imagen,
                    institutosAdriana
            );


            em.persist(heisenberg);
            em.persist(benkenobi);
            em.persist(waston);
            em.persist(house);
            em.persist(timmy);
            em.persist(danny);
            em.persist(phils);
            em.persist(bruces);
            em.persist(adri);


            /*
             * ============================================================
             * 3. CURSOS
             * ============================================================
             */

            Curso c1 = new Curso(
                    "Talleres plenarios",
                    "Talleres plenarios: presentados por cuatro reconocidos "
                    + "matemáticos uruguayos, plantearán diversos tópicos de "
                    + "matemática en el marco de los cuales se realizarán "
                    + "actividades fomentando la integración entre estudiantes, "
                    + "docentes e investigadores.",
                    "3 semanas",
                    15,
                    1,
                    "www.tmu.edu.uy",
                    java.sql.Date.valueOf(LocalDate.of(2026, 2, 1)),
                    il,
                    new ArrayList<>()
            );

            Curso c2 = new Curso(
                    "Seminarios de Resolución de Problemas",
                    "Seminario, todos los jueves en Facultad de Ingeniería a "
                    + "partir del jueves 25 de Julio, en las áreas en que se "
                    + "desarrollan los problemas de las Olimpíadas de Matemática.",
                    "5 semanas",
                    30,
                    2,
                    "www.tmu.edu.uy",
                    java.sql.Date.valueOf(LocalDate.of(2026, 7, 12)),
                    il,
                    new ArrayList<>()
            );

            Curso c3 = new Curso(
                    "Dalavuelta",
                    "Dalavuelta es un proyecto de extensión que nace en el "
                    + "Instituto de Ingeniería Mecánica y Producción Industrial "
                    + "(IIMPI) de Fing, que, si bien inicia su trabajo en el "
                    + "desarrollo de bicicletas accesibles para personas en "
                    + "situación de discapacidad motriz a partir de bicicletas "
                    + "abandonadas, se propuso diseñar otras herramientas para "
                    + "fomentar la accesibilidad.",
                    "10 semanas",
                    60,
                    4,
                    "https://eva.fing.edu.uy/course/view.php?id=783#section-2",
                    java.sql.Date.valueOf(LocalDate.of(2024, 6, 25)),
                    im,
                    new ArrayList<>()
            );

            Curso c4 = new Curso(
                    "Extensionismo Industrial",
                    "El proyecto tiene como objetivo desarrollar "
                    + "intervenciones curriculares en pequeños emprendimientos "
                    + "productivos de diferentes sectores de la industria "
                    + "nacional. La metodología de trabajo permite articular "
                    + "diversas intervenciones, combinando actividades de "
                    + "enseñanza, extensión e investigación por parte de "
                    + "docentes del IMPII.",
                    "12 semanas",
                    75,
                    5,
                    "https://eva.fing.edu.uy/course/view.php?id=783#section-2",
                    java.sql.Date.valueOf(LocalDate.of(2025, 6, 16)),
                    im,
                    new ArrayList<>()
            );

            Curso c5 = new Curso(
                    "Inclusión Energética",
                    "En el proyecto se conjuga el trabajo de docentes y "
                    + "estudiantes de la carrera Ingeniería Industrial Mecánica "
                    + "a través del Módulo de Extensión, en donde se trabaja "
                    + "en el diseño, construcción y prueba de un prototipo de "
                    + "colector solar adquiriendo conocimientos relevantes para "
                    + "luego poder replicarlos junto a las familias en los "
                    + "talleres. Las premisas fundamentales a la hora de pensar "
                    + "los diseños son: por un lado el bajo costo de los "
                    + "materiales y por otro la fácil construcción de forma de "
                    + "poder construirlos ellos mismos.",
                    "6 semanas",
                    45,
                    3,
                    "https://eva.fing.edu.uy/course/view.php?id=783#section-2",
                    java.sql.Date.valueOf(LocalDate.of(2026, 2, 1)),
                    im,
                    new ArrayList<>()
            );

            Curso c6 = new Curso(
                    "Flor del Ceibo",
                    "Flor de Ceibo es un proyecto central de la Universidad "
                    + "de la República, que tiene misión por movilizar la "
                    + "participación de estudiantes universitarios en diversas "
                    + "tareas vinculadas con la puesta en funcionamiento del "
                    + "Plan Ceibal en el territorio nacional.",
                    "15 semanas",
                    150,
                    10,
                    "http://www.flordeceibo.edu.uy/",
                    java.sql.Date.valueOf(LocalDate.of(2008, 7, 27)),
                    di,
                    new ArrayList<>()
            );

            Curso c7 = new Curso(
                    "Taller de robótica educativa.",
                    "La asignatura se organiza en dos etapas. La primer etapa "
                    + "se dicta a través de clases teórico-prácticas, donde se "
                    + "espera además que cada estudiante le dedique horas de "
                    + "estudio. La segunda etapa consiste en que los estudiantes "
                    + "trabajen en grupo sobre el diseño e implementación de una "
                    + "experiencia didáctica de inclusión del robot Butiá en el "
                    + "aula, utilizando los conocimientos aprendidos en clase.",
                    "8 semanas",
                    90,
                    6,
                    "https://eva.fing.edu.uy/course/view.php?id=1187",
                    java.sql.Date.valueOf(LocalDate.of(2024, 2, 2)),
                    in,
                    new ArrayList<>()
            );

            Curso c8 = new Curso(
                    "Participación en investigación sobre el empleo del juego Komikan como recurso didáctico en la Escuela",
                    "Se propone desarrollar una aplicación interactiva para "
                    + "tablet Android basada en el juego de tablero Komikan "
                    + "(versión web del juego https://codepen.io/Borborem/full/OvZBvZ/), "
                    + "que incluya los distintos aspectos concernientes al juego, "
                    + "así como a situaciones específicas particulares.",
                    "9 semanas",
                    45,
                    3,
                    "https://eva.fing.edu.uy/mod/folder/view.php?id=89398",
                    java.sql.Date.valueOf(LocalDate.of(2026, 6, 15)),
                    in,
                    new ArrayList<>()
            );

            Curso c9 = new Curso(
                    "\"Herramientas de apoyo a la enseñanza de inglés. Instalación y evaluación\"",
                    "Se realizarán visitas a escuelas rurales participantes "
                    + "en un proyecto conjunto del grupo PLN y el Programa de "
                    + "Políticas Lingüísticas de ANEP, en el marco del cual se "
                    + "desarrollaron diferentes herramientas para uso de maestros "
                    + "que enseñan inglés con apoyo remoto de profesores "
                    + "especializados desde Montevideo.",
                    "12 semanas",
                    60,
                    4,
                    "https://eva.fing.edu.uy/mod/folder/view.php?id=89398",
                    java.sql.Date.valueOf(LocalDate.of(2026, 5, 24)),
                    in,
                    new ArrayList<>()
            );

            Curso c10 = new Curso(
                    "MicroBit",
                    "El Centro Ceibal se encuentra distribuyendo placas "
                    + "micro:bit (https://microbit.ceibal.edu.uy/) para que "
                    + "estudiantes de primaria y secundaria aprendan nociones "
                    + "básicas de robótica, electrónica y programación de forma "
                    + "autónoma y lúdica. Estas placas se basan en un "
                    + "microcontrolador y cuentan con leds, botones, acelerómetro, "
                    + "brújula, bluetooth y otros sensores. Además, las placas "
                    + "se programan fácilmente con lenguaje tipo \"scratch\" y "
                    + "python, por lo que son muy útiles para un primer "
                    + "acercamiento a la temática.",
                    "15 semanas",
                    105,
                    7,
                    "https://www.fing.edu.uy/noticias/extension/modulo-de-tallerextension-microbit",
                    java.sql.Date.valueOf(LocalDate.of(2026, 3, 13)),
                    ie,
                    new ArrayList<>()
            );

            em.persist(c1);
            em.persist(c2);
            em.persist(c3);
            em.persist(c4);
            em.persist(c5);
            em.persist(c6);
            em.persist(c7);
            em.persist(c8);
            em.persist(c9);
            em.persist(c10);


            /*
             * ============================================================
             * 4. PREVIAS
             *
             * C2 -> C1
             * C3 -> C1
             * C4 -> C1
             * ============================================================
             */

            c2.getPrevias().add(c1);
            c3.getPrevias().add(c1);
            c4.getPrevias().add(c1);


            /*
             * ============================================================
             * 5. EDICIONES
             * ============================================================
             */

            Set<Docente> docentesE1 = new HashSet<>();
            docentesE1.add(bruces);

            Edicion e1 = new Edicion(
                    "Flor del Ceibo - 2010",
                    -1,
                    LocalDate.of(2010, 2, 16),
                    LocalDate.of(2010, 3, 15),
                    LocalDate.of(2010, 7, 7),
                    c6,
                    docentesE1
            );


            Set<Docente> docentesE2 = new HashSet<>();
            docentesE2.add(bruces);
            docentesE2.add(adri);

            Edicion e2 = new Edicion(
                    "Flor del Ceibo - 2012",
                    -1,
                    LocalDate.of(2012, 7, 10),
                    LocalDate.of(2012, 8, 1),
                    LocalDate.of(2012, 11, 20),
                    c6,
                    docentesE2
            );


            Set<Docente> docentesE3 = new HashSet<>();
            docentesE3.add(bruces);
            docentesE3.add(adri);

            Edicion e3 = new Edicion(
                    "Flor del Ceibo - 2025",
                    -1,
                    LocalDate.of(2025, 3, 6),
                    LocalDate.of(2025, 4, 10),
                    LocalDate.of(2025, 8, 7),
                    c6,
                    docentesE3
            );


            Set<Docente> docentesE4 = new HashSet<>();
            docentesE4.add(phils);

            Edicion e4 = new Edicion(
                    "Dalavuelta - 2025",
                    15,
                    LocalDate.of(2024, 7, 20),
                    LocalDate.of(2024, 8, 20),
                    LocalDate.of(2024, 11, 10),
                    c3,
                    docentesE4
            );


            Set<Docente> docentesE5 = new HashSet<>();
            docentesE5.add(phils);

            Edicion e5 = new Edicion(
                    "Extensionismo Industrial - 2025",
                    15,
                    LocalDate.of(2025, 7, 8),
                    LocalDate.of(2025, 8, 10),
                    LocalDate.of(2025, 11, 10),
                    c4,
                    docentesE5
            );


            Set<Docente> docentesE6 = new HashSet<>();
            docentesE6.add(phils);

            Edicion e6 = new Edicion(
                    "Inclusión Energética - 2026",
                    30,
                    LocalDate.of(2026, 2, 20),
                    LocalDate.of(2026, 3, 15),
                    LocalDate.of(2026, 4, 30),
                    c5,
                    docentesE6
            );


            Set<Docente> docentesE7 = new HashSet<>();
            docentesE7.add(heisenberg);

            Edicion e7 = new Edicion(
                    "Taller de robótica educativa - 2024",
                    10,
                    LocalDate.of(2024, 2, 15),
                    LocalDate.of(2024, 3, 10),
                    LocalDate.of(2024, 5, 10),
                    c7,
                    docentesE7
            );


            Set<Docente> docentesE8 = new HashSet<>();
            docentesE8.add(heisenberg);
            docentesE8.add(benkenobi);

            Edicion e8 = new Edicion(
                    "Taller de robótica educativa - 2026",
                    10,
                    LocalDate.of(2026, 2, 15),
                    LocalDate.of(2026, 3, 10),
                    LocalDate.of(2026, 5, 10),
                    c7,
                    docentesE8
            );


            Set<Docente> docentesE9 = new HashSet<>();
            docentesE9.add(benkenobi);
            docentesE9.add(waston);

            Edicion e9 = new Edicion(
                    "Taller de robótica educativa - 2026-2",
                    20,
                    LocalDate.of(2026, 8, 15),
                    LocalDate.of(2026, 9, 10),
                    LocalDate.of(2026, 11, 8),
                    c7,
                    docentesE9
            );


            Set<Docente> docentesE10 = new HashSet<>();
            docentesE10.add(waston);

            Edicion e10 = new Edicion(
                    "Participación en investigación sobre el empleo del juego Komikan como recurso didáctico en la Escuela - 2026",
                    5,
                    LocalDate.of(2026, 7, 10),
                    LocalDate.of(2026, 7, 29),
                    LocalDate.of(2026, 10, 7),
                    c8,
                    docentesE10
            );


            Set<Docente> docentesE11 = new HashSet<>();
            docentesE11.add(heisenberg);

            Edicion e11 = new Edicion(
                    "Herramientas de apoyo a la enseñanza de inglés. Instalación y evaluación - 26",
                    5,
                    LocalDate.of(2026, 6, 2),
                    LocalDate.of(2026, 9, 15),
                    LocalDate.of(2026, 12, 15),
                    c9,
                    docentesE11
            );


            Set<Docente> docentesE12 = new HashSet<>();
            docentesE12.add(house);

            Edicion e12 = new Edicion(
                    "MicroBit - 2026",
                    30,
                    LocalDate.of(2026, 7, 2),
                    LocalDate.of(2026, 8, 12),
                    LocalDate.of(2026, 12, 5),
                    c10,
                    docentesE12
            );


            Set<Docente> docentesE13 = new HashSet<>();
            docentesE13.add(timmy);
            docentesE13.add(danny);

            Edicion e13 = new Edicion(
                    "Talleres plenarios - 2026",
                    -1,
                    LocalDate.of(2026, 3, 2),
                    LocalDate.of(2026, 3, 10),
                    LocalDate.of(2026, 3, 30),
                    c1,
                    docentesE13
            );


            Set<Docente> docentesE14 = new HashSet<>();
            docentesE14.add(timmy);

            Edicion e14 = new Edicion(
                    "Seminarios de Resolución de Problemas - 2026",
                    -1,
                    LocalDate.of(2026, 7, 12),
                    LocalDate.of(2026, 9, 10),
                    LocalDate.of(2026, 10, 20),
                    c2,
                    docentesE14
            );


            em.persist(e1);
            em.persist(e2);
            em.persist(e3);
            em.persist(e4);
            em.persist(e5);
            em.persist(e6);
            em.persist(e7);
            em.persist(e8);
            em.persist(e9);
            em.persist(e10);
            em.persist(e11);
            em.persist(e12);
            em.persist(e13);
            em.persist(e14);


            /*
             * ============================================================
             * 6. INSCRIPCIONES A EDICIONES
             * ============================================================
             */

            em.persist(new InscripcionEdicion(
                    eleven11, e1, LocalDate.of(2010, 2, 20)));

            em.persist(new InscripcionEdicion(
                    chechi, e1, LocalDate.of(2010, 2, 25)));

            em.persist(new InscripcionEdicion(
                    costas, e2, LocalDate.of(2012, 7, 12)));

            em.persist(new InscripcionEdicion(
                    roro, e2, LocalDate.of(2012, 7, 15)));

            em.persist(new InscripcionEdicion(
                    weiss, e2, LocalDate.of(2012, 7, 30)));

            em.persist(new InscripcionEdicion(
                    roro, e3, LocalDate.of(2025, 3, 10)));

            em.persist(new InscripcionEdicion(
                    jeffw, e3, LocalDate.of(2025, 3, 15)));

            em.persist(new InscripcionEdicion(
                    chechi, e4, LocalDate.of(2024, 7, 25)));

            em.persist(new InscripcionEdicion(
                    eleven11, e4, LocalDate.of(2024, 7, 28)));

            em.persist(new InscripcionEdicion(
                    roro, e4, LocalDate.of(2024, 8, 2)));

            em.persist(new InscripcionEdicion(
                    costas, e4, LocalDate.of(2024, 8, 10)));

            em.persist(new InscripcionEdicion(
                    jeffw, e4, LocalDate.of(2024, 8, 15)));

            em.persist(new InscripcionEdicion(
                    costas, e5, LocalDate.of(2025, 7, 18)));

            em.persist(new InscripcionEdicion(
                    chechi, e5, LocalDate.of(2025, 7, 20)));

            em.persist(new InscripcionEdicion(
                    eleven11, e5, LocalDate.of(2025, 7, 29)));

            em.persist(new InscripcionEdicion(
                    weiss, e5, LocalDate.of(2025, 8, 5)));

            em.persist(new InscripcionEdicion(
                    roro, e6, LocalDate.of(2026, 2, 23)));

            em.persist(new InscripcionEdicion(
                    weiss, e6, LocalDate.of(2026, 2, 25)));

            em.persist(new InscripcionEdicion(
                    chechi, e6, LocalDate.of(2026, 2, 28)));

            em.persist(new InscripcionEdicion(
                    eleven11, e6, LocalDate.of(2026, 3, 3)));

            em.persist(new InscripcionEdicion(
                    weiss, e7, LocalDate.of(2017, 2, 18)));

            em.persist(new InscripcionEdicion(
                    roro, e7, LocalDate.of(2024, 2, 20)));

            em.persist(new InscripcionEdicion(
                    eleven11, e7, LocalDate.of(2024, 3, 3)));

            em.persist(new InscripcionEdicion(
                    chechi, e7, LocalDate.of(2024, 3, 5)));

            em.persist(new InscripcionEdicion(
                    jeffw, e8, LocalDate.of(2026, 2, 18)));

            em.persist(new InscripcionEdicion(
                    costas, e8, LocalDate.of(2026, 2, 22)));

            em.persist(new InscripcionEdicion(
                    weiss, e9, LocalDate.of(2026, 8, 18)));

            em.persist(new InscripcionEdicion(
                    chechi, e9, LocalDate.of(2026, 8, 22)));

            em.persist(new InscripcionEdicion(
                    roro, e9, LocalDate.of(2026, 9, 3)));

            em.persist(new InscripcionEdicion(
                    chechi, e10, LocalDate.of(2026, 7, 13)));

            em.persist(new InscripcionEdicion(
                    weiss, e10, LocalDate.of(2026, 7, 20)));

            em.persist(new InscripcionEdicion(
                    roro, e10, LocalDate.of(2026, 7, 22)));

            em.persist(new InscripcionEdicion(
                    weiss, e11, LocalDate.of(2026, 6, 4)));

            em.persist(new InscripcionEdicion(
                    eleven11, e11, LocalDate.of(2026, 7, 18)));

            em.persist(new InscripcionEdicion(
                    jeffw, e11, LocalDate.of(2026, 8, 20)));

            em.persist(new InscripcionEdicion(
                    chechi, e12, LocalDate.of(2026, 7, 12)));

            em.persist(new InscripcionEdicion(
                    roro, e12, LocalDate.of(2026, 7, 14)));

            em.persist(new InscripcionEdicion(
                    eleven11, e12, LocalDate.of(2026, 7, 25)));

            em.persist(new InscripcionEdicion(
                    jeffw, e12, LocalDate.of(2026, 8, 5)));

            em.persist(new InscripcionEdicion(
                    costas, e13, LocalDate.of(2026, 3, 5)));

            em.persist(new InscripcionEdicion(
                    weiss, e13, LocalDate.of(2026, 3, 4)));

            em.persist(new InscripcionEdicion(
                    roro, e13, LocalDate.of(2026, 3, 7)));

            em.persist(new InscripcionEdicion(
                    weiss, e14, LocalDate.of(2026, 7, 15)));

            em.persist(new InscripcionEdicion(
                    costas, e14, LocalDate.of(2026, 7, 20)));

            em.persist(new InscripcionEdicion(
                    roro, e14, LocalDate.of(2026, 8, 6)));

            em.persist(new InscripcionEdicion(
                    chechi, e14, LocalDate.of(2026, 8, 30)));


            /*
             * ============================================================
             * 7. PROGRAMAS DE FORMACIÓN
             *
             * El PDF no especifica fechaRegistro.
             * Se asigna 01/01/2026.
             *
             * P3 aparece como 03/09/6 en el PDF; se interpreta como
             * 03/09/2026 por el contexto de los datos de 2026.
             * ============================================================
             */

            ProgramaFormacion p1 = new ProgramaFormacion(
                    "EFI Ingeniería Mecánica",
                    "Programa mecánica",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 5, 1),
                    LocalDate.of(2026, 10, 31),
                    new HashSet<>()
            );

            ProgramaFormacion p2 = new ProgramaFormacion(
                    "Formación integral",
                    "Programa varios institutos",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 7, 15),
                    LocalDate.of(2027, 1, 1),
                    new HashSet<>()
            );

            ProgramaFormacion p3 = new ProgramaFormacion(
                    "EFI Robótica",
                    "Programa robótica",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 9, 3),
                    LocalDate.of(2026, 11, 18),
                    new HashSet<>()
            );

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);


            /*
             * ============================================================
             * 8. CURSOS QUE INTEGRAN LOS PROGRAMAS
             * ============================================================
             */

            p1.getCursos().add(c3);
            p1.getCursos().add(c4);
            p1.getCursos().add(c5);

            p2.getCursos().add(c2);
            p2.getCursos().add(c4);
            p2.getCursos().add(c6);
            p2.getCursos().add(c8);

            p3.getCursos().add(c7);
            p3.getCursos().add(c10);


            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {

            em.close();
        }
    }
  
    //**************************************************************************
    
}