package edu.edext.logica;
//**************************** Capa Logica *************************************
import edu.edext.tools.Utils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.time.LocalDate;
import org.tinylog.Logger;
import java.util.List;

/**
 *
 * @author vdraco - codigo de diego
 */
public class cargarDatosDePruebaHelper {
    private final EntityManagerFactory emf;
    private EntityManager em;
    public final int NOINSCRIPTO=0;
    public final int INSCRIPTO= 1;
    public final int RECHAZADO=3;
    public final int ACEPTADO=2;
    
    public cargarDatosDePruebaHelper(EntityManagerFactory emf){
        this.emf=emf;
        this.em=this.emf.createEntityManager();
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

        //EntityManager em = emf.createEntityManager(); 

        try {

            em.getTransaction().begin();

            /*
             * ============================================================
             * VERIFICAR SI LOS DATOS DE PRUEBA YA FUERON CARGADOS
             * ============================================================
             */

            if (em.find(Instituto.class, "IN") != null
                    || em.find(Categoria.class,"Social")!=null
                    || em.find(Usuario.class, "eleven11") != null
                    || em.find(Curso.class, "Talleres plenarios") != null
                    || em.find(Edicion.class, "Flor del Ceibo - 2010") != null
                    || em.find(ProgramaFormacion.class, "EFI Ingeniería Mecánica") != null ) {

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

            
            // ***********************************************************
            // 9. CATEGORIAS
            // ***********************************************************
            
            Categoria t1=new Categoria("Social");
            Categoria t2=new Categoria("Industrial");
            Categoria t3=new Categoria("Educativo");
            Categoria t4=new Categoria("Interdisciplinar");


            em.persist(t1);
            em.persist(t2);
            em.persist(t3);
            em.persist(t4);
            
            
            /*
             * ============================================================
             * 2. USUARIOS
             *
             * Todas las imágenes utilizan usr.png.
             * ============================================================
             */

            String imagen = "usr.png", password=Utils.Crypt.encodeBCrypt("1234");

            // ---------------- ESTUDIANTES ----------------

            Estudiante EL = new Estudiante(
                    "eleven11",
                    password,
                    "eleven11@gmail.com",
                    "Eleven",
                    "Twelve",
                    java.sql.Date.valueOf(LocalDate.of(1971, 12, 31)),
                    imagen
            );

            Estudiante CO = new Estudiante(
                    "costas",
                    password,
                    "gcostas@gmail.com",
                    "Gerardo",
                    "Costas",
                    java.sql.Date.valueOf(LocalDate.of(1983, 11, 15)),
                    imagen
            );

            Estudiante RO = new Estudiante(
                    "roro",
                    password,
                    "rcotelo@yahoo.com",
                    "Rodrigo",
                    "Cotelo",
                    java.sql.Date.valueOf(LocalDate.of(1975, 8, 2)),
                    "roro.jpeg"
            );
            

            Estudiante CH = new Estudiante(
                    "chechi",
                    password,
                    "cgarrido@hotmail.com",
                    "Cecilia",
                    "Garrido",
                    java.sql.Date.valueOf(LocalDate.of(1987, 9, 12)),
                    "chechi.jpeg"
            );

            Estudiante JW = new Estudiante(
                    "jeffw",
                    password,
                    "jwilliams@gmail.com",
                    "Jeff",
                    "Williams",
                    java.sql.Date.valueOf(LocalDate.of(1964, 11, 27)),
                    "jeffw.jpeg"
            );

            Estudiante WE = new Estudiante(
                    "weiss",
                    password,
                    "aweiss@hotmail.com",
                    "Adrian",
                    "Weiss",
                    java.sql.Date.valueOf(LocalDate.of(1978, 12, 23)),
                    imagen
            );

            
            // ---------------- DOCENTES ----------------

            List<Instituto> institutosWalter = new ArrayList<>();
            institutosWalter.add(in);

            Docente WW = new Docente(
                    "heisenberg",
                    password,
                    "heisenberg@gmail.com",
                    "Walter",
                    "White",
                    java.sql.Date.valueOf(LocalDate.of(1956, 3, 7)),
                    "heisenberg.png",
                    institutosWalter
            );


            List<Instituto> institutosObi = new ArrayList<>();
            institutosObi.add(in);

            Docente OK = new Docente(
                    "benkenobi",
                    password,
                    "benKenobi@gmail.com",
                    "Obi-Wan",
                    "Kenobi",
                    java.sql.Date.valueOf(LocalDate.of(1914, 4, 2)),
                    "benkenobi.jpg",
                    institutosObi
            );


            List<Instituto> institutosEmma = new ArrayList<>();
            institutosEmma.add(in);

            Docente EW = new Docente(
                    "waston",
                    password,
                    "e.watson@gmail.com",
                    "Emma",
                    "Watson",
                    java.sql.Date.valueOf(LocalDate.of(1990, 4, 15)),
                    "waston.jpg",
                    institutosEmma
            );


            List<Instituto> institutosHouse = new ArrayList<>();
            institutosHouse.add(ie);

            Docente GH = new Docente(
                    "house",
                    password,
                    "greghouse@gmail.com",
                    "Gregory",
                    "House",
                    java.sql.Date.valueOf(LocalDate.of(1959, 5, 15)),
                    "house.jpeg",
                    institutosHouse
            );


            List<Instituto> institutosTim = new ArrayList<>();
            institutosTim.add(il);

            Docente TC = new Docente(
                    "timmy",
                    password,
                    "tim.cook@apple.com",
                    "Tim",
                    "Cook",
                    java.sql.Date.valueOf(LocalDate.of(1960, 11, 1)),
                    "timmy.jpeg",
                    institutosTim
            );


            List<Instituto> institutosDaniel = new ArrayList<>();
            institutosDaniel.add(il);

            Docente DR = new Docente(
                    "danny",
                    password,
                    "dan.riccio@gmail.com",
                    "Daniel",
                    "Riccio",
                    java.sql.Date.valueOf(LocalDate.of(1963, 7, 5)),
                    imagen,
                    institutosDaniel
            );


            List<Instituto> institutosPhilip = new ArrayList<>();
            institutosPhilip.add(im);

            Docente PS = new Docente(
                    "phils",
                    password,
                    "schiller@gmail.com",
                    "Philip",
                    "Schiller",
                    java.sql.Date.valueOf(LocalDate.of(1961, 10, 7)),
                    imagen,
                    institutosPhilip
            );


            List<Instituto> institutosBruce = new ArrayList<>();
            institutosBruce.add(di);

            Docente BS = new Docente(
                    "bruces",
                    password,
                    "sewell@gmail.com",
                    "Bruce",
                    "Sewell",
                    java.sql.Date.valueOf(LocalDate.of(1959, 12, 3)),
                    imagen,
                    institutosBruce
            );


            List<Instituto> institutosAdriana = new ArrayList<>();
            institutosAdriana.add(di);

            Docente AG = new Docente(
                    "adri",
                    password,
                    "agarcia@gmail.com",
                    "Adriana",
                    "García",
                    java.sql.Date.valueOf(LocalDate.of(1978, 7, 28)),
                    imagen,
                    institutosAdriana
            );

            // *****************************************************************
            // SEGUIDOR - SEGUIDO
            // *****************************************************************
            
            EL.agregarSeguido(RO);
            EL.agregarSeguido(WW);
            EL.agregarSeguido(EW);
            CO.agregarSeguido(CH);
            CO.agregarSeguido(JW);
            CO.agregarSeguido(OK);
            CO.agregarSeguido(DR);
            CO.agregarSeguido(PS);
            RO.agregarSeguido(CO);
            RO.agregarSeguido(WE);
            CH.agregarSeguido(EL);
            CH.agregarSeguido(JW);
            CH.agregarSeguido(GH);
            JW.agregarSeguido(EL);
            WE.agregarSeguido(CO);
            WE.agregarSeguido(WW);
            WE.agregarSeguido(TC);
            WW.agregarSeguido(RO);
            WW.agregarSeguido(AG);
            OK.agregarSeguido(DR);
            EW.agregarSeguido(WE);
            EW.agregarSeguido(WW);
            GH.agregarSeguido(JW);
            GH.agregarSeguido(WE);
            GH.agregarSeguido(OK);
            TC.agregarSeguido(EL);
            TC.agregarSeguido(CH);
            TC.agregarSeguido(JW);
            DR.agregarSeguido(CO);
            DR.agregarSeguido(WE);
            DR.agregarSeguido(WW);
            PS.agregarSeguido(BS);
            BS.agregarSeguido(PS);
            AG.agregarSeguido(WW);
            
            
            em.persist(EL);
            em.persist(CO);
            em.persist(RO);
            em.persist(CH);
            em.persist(JW);
            em.persist(WE);
            
            em.persist(WW);
            em.persist(OK);
            em.persist(EW);
            em.persist(GH);
            em.persist(TC);
            em.persist(DR);
            em.persist(PS);
            em.persist(BS);
            em.persist(AG);

            /*
             * ============================================================
             * 3. CURSOS
             * ============================================================
             */
            
            List<Categoria> catc3=new ArrayList<>(), catc4=new ArrayList<>(),
                            catc5=new ArrayList<>(), catc6=new ArrayList<>(),
                            catc7=new ArrayList<>(), catc8=new ArrayList<>(),
                            catc9=new ArrayList<>(), catc10=new ArrayList<>();
            catc3.add(t1);
            catc7.add(t1);
            catc9.add(t1);
            catc4.add(t2);
            catc5.add(t2);
            catc10.add(t2);
            catc6.add(t3);
            catc7.add(t3);
            catc8.add(t3);
            catc9.add(t3);
            catc10.add(t3);
            catc6.add(t4);
            
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
                    new ArrayList<>(),
                    new ArrayList<>(),
                    null
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
                    new ArrayList<>(),
                    new ArrayList<>(),
                    null
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
                    catc3,
                    new ArrayList<>(),
                    "Curso.Dalavuelta.png"
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
                    catc4,
                    new ArrayList<>(),
                    null
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
                    catc5,
                    new ArrayList<>(),
                    "Curso.Inclusión Energética.jpg"
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
                    catc6,
                    new ArrayList<>(),
                    "Curso.Flor del Ceibo.png"
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
                    catc7,
                    new ArrayList<>(),
                    "Curso.Taller de robótica educativa.jpg"
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
                    catc8,
                    new ArrayList<>(),
                    "Curso.Participación en investigación sobre el empleo del juego Komikan como recurso didáctico en la Escuela.png"
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
                    catc9,
                    new ArrayList<>(),
                    null
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
                    catc10,
                    new ArrayList<>(),
                    "Curso.MicroBit.jpg"
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

            List<Docente> docentesE1 = new ArrayList<>();
            docentesE1.add(BS);

            Edicion e1 = new Edicion(
                    "Flor del Ceibo - 2010",
                    -1,
                    LocalDate.of(2010, 2, 16),
                    LocalDate.of(2010, 3, 15),
                    LocalDate.of(2010, 7, 7),
                    c6,
                    docentesE1,
                    "Edicion.Flor del Ceibo - 2010.jpeg"
            );


            List<Docente> docentesE2 = new ArrayList<>();
            docentesE2.add(BS);
            docentesE2.add(AG);

            Edicion e2 = new Edicion(
                    "Flor del Ceibo - 2012",
                    -1,
                    LocalDate.of(2012, 7, 10),
                    LocalDate.of(2012, 8, 1),
                    LocalDate.of(2012, 11, 20),
                    c6,
                    docentesE2,
                    "Edicion.Flor del Ceibo - 2012.jpg"
            );


            List<Docente> docentesE3 = new ArrayList<>();
            docentesE3.add(BS);
            docentesE3.add(AG);

            Edicion e3 = new Edicion(
                    "Flor del Ceibo - 2025",
                    -1,
                    LocalDate.of(2025, 3, 6),
                    LocalDate.of(2025, 4, 10),
                    LocalDate.of(2025, 8, 7),
                    c6,
                    docentesE3,
                    "Edicion.Flor del Ceibo - 2025.jpeg"
            );


            List<Docente> docentesE4 = new ArrayList<>();
            docentesE4.add(PS);

            Edicion e4 = new Edicion(
                    "Dalavuelta - 2025",
                    15,
                    LocalDate.of(2024, 7, 20),
                    LocalDate.of(2024, 8, 20),
                    LocalDate.of(2024, 11, 10),
                    c3,
                    docentesE4,
                    "Edicion.Dalavuelta - 2025.jpeg"
            );


            List<Docente> docentesE5 = new ArrayList<>();
            docentesE5.add(PS);

            Edicion e5 = new Edicion(
                    "Extensionismo Industrial - 2025",
                    15,
                    LocalDate.of(2025, 7, 8),
                    LocalDate.of(2025, 8, 10),
                    LocalDate.of(2025, 11, 10),
                    c4,
                    docentesE5,
                    null
            );


            List<Docente> docentesE6 = new ArrayList<>();
            docentesE6.add(PS);

            Edicion e6 = new Edicion(
                    "Inclusión Energética - 2026",
                    30,
                    LocalDate.of(2026, 2, 20),
                    LocalDate.of(2026, 3, 15),
                    LocalDate.of(2026, 4, 30),
                    c5,
                    docentesE6,
                    null
            );


            List<Docente> docentesE7 = new ArrayList<>();
            docentesE7.add(WW);

            Edicion e7 = new Edicion(
                    "Taller de robótica educativa - 2024",
                    10,
                    LocalDate.of(2024, 2, 15),
                    LocalDate.of(2024, 3, 10),
                    LocalDate.of(2024, 5, 10),
                    c7,
                    docentesE7,
                    "Edicion.Taller de robótica educativa - 2024.jpeg"
            );


            List<Docente> docentesE8 = new ArrayList<>();
            docentesE8.add(WW);
            docentesE8.add(OK);

            Edicion e8 = new Edicion(
                    "Taller de robótica educativa - 2026",
                    10,
                    LocalDate.of(2026, 2, 15),
                    LocalDate.of(2026, 3, 10),
                    LocalDate.of(2026, 5, 10),
                    c7,
                    docentesE8,
                    "Edicion.Taller de robótica educativa - 2026.jpeg"
            );


            List<Docente> docentesE9 = new ArrayList<>();
            docentesE9.add(OK);
            docentesE9.add(EW);

            Edicion e9 = new Edicion(
                    "Taller de robótica educativa - 2026-2",
                    20,
                    LocalDate.of(2026, 8, 15),
                    LocalDate.of(2026, 9, 10),
                    LocalDate.of(2026, 11, 8),
                    c7,
                    docentesE9,
                    "Edicion.Taller de robótica educativa - 2026-2.jpeg"
            );


            List<Docente> docentesE10 = new ArrayList<>();
            docentesE10.add(EW);

            Edicion e10 = new Edicion(
                    "Participación en investigación sobre el empleo del juego Komikan como recurso didáctico en la Escuela - 2026",
                    5,
                    LocalDate.of(2026, 7, 10),
                    LocalDate.of(2026, 7, 29),
                    LocalDate.of(2026, 10, 7),
                    c8,
                    docentesE10,
                    null
            );


            List<Docente> docentesE11 = new ArrayList<>();
            docentesE11.add(WW);

            Edicion e11 = new Edicion(
                    "Herramientas de apoyo a la enseñanza de inglés. Instalación y evaluación - 26",
                    5,
                    LocalDate.of(2026, 6, 2),
                    LocalDate.of(2026, 9, 15),
                    LocalDate.of(2026, 12, 15),
                    c9,
                    docentesE11,
                    null
            );


            List<Docente> docentesE12 = new ArrayList<>();
            docentesE12.add(GH);

            Edicion e12 = new Edicion(
                    "MicroBit - 2026",
                    30,
                    LocalDate.of(2026, 7, 2),
                    LocalDate.of(2026, 8, 12),
                    LocalDate.of(2026, 12, 5),
                    c10,
                    docentesE12,
                    null
            );


            List<Docente> docentesE13 = new ArrayList<>();
            docentesE13.add(TC);
            docentesE13.add(DR);

            Edicion e13 = new Edicion(
                    "Talleres plenarios - 2026",
                    -1,
                    LocalDate.of(2026, 3, 2),
                    LocalDate.of(2026, 3, 10),
                    LocalDate.of(2026, 3, 30),
                    c1,
                    docentesE13,
                    null
            );


            List<Docente> docentesE14 = new ArrayList<>();
            docentesE14.add(TC);

            Edicion e14 = new Edicion(
                    "Seminarios de Resolución de Problemas - 2026",
                    -1,
                    LocalDate.of(2026, 7, 12),
                    LocalDate.of(2026, 9, 10),
                    LocalDate.of(2026, 10, 20),
                    c2,
                    docentesE14,
                    null
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
                    EL, e1, LocalDate.of(2010, 2, 20), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CH, e1, LocalDate.of(2010, 2, 25), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CO, e2, LocalDate.of(2012, 7, 12), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    RO, e2, LocalDate.of(2012, 7, 15), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    WE, e2, LocalDate.of(2012, 7, 30), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    RO, e3, LocalDate.of(2025, 3, 10), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    JW, e3, LocalDate.of(2025, 3, 15), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CH, e4, LocalDate.of(2024, 7, 25), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    EL, e4, LocalDate.of(2024, 7, 28), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    RO, e4, LocalDate.of(2024, 8, 2), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CO, e4, LocalDate.of(2024, 8, 10), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    JW, e4, LocalDate.of(2024, 8, 15), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    CO, e5, LocalDate.of(2025, 7, 18), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CH, e5, LocalDate.of(2025, 7, 20), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    EL, e5, LocalDate.of(2025, 7, 29), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    WE, e5, LocalDate.of(2025, 8, 5), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    RO, e6, LocalDate.of(2026, 2, 23), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    WE, e6, LocalDate.of(2026, 2, 25), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    CH, e6, LocalDate.of(2026, 2, 28), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    EL, e6, LocalDate.of(2026, 3, 3), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    WE, e7, LocalDate.of(2017, 2, 18), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    RO, e7, LocalDate.of(2024, 2, 20), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    EL, e7, LocalDate.of(2024, 3, 3), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CH, e7, LocalDate.of(2024, 3, 5), 0));

            em.persist(new InscripcionEdicion(
                    JW, e8, LocalDate.of(2026, 2, 18), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CO, e8, LocalDate.of(2026, 2, 22), ACEPTADO));

            // E8 - RO no existe fecha en los datos de prueba 
            em.persist(new InscripcionEdicion(
                    RO, e8, LocalDate.of(2026, 3, 18), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    WE, e9, LocalDate.of(2026, 8, 18), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    CH, e9, LocalDate.of(2026, 8, 22), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    RO, e9, LocalDate.of(2026, 9, 3), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    CH, e10, LocalDate.of(2026, 7, 13), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    WE, e10, LocalDate.of(2026, 7, 20), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    RO, e10, LocalDate.of(2026, 7, 22), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    WE, e11, LocalDate.of(2026, 6, 4), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    EL, e11, LocalDate.of(2026, 7, 18), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    JW, e11, LocalDate.of(2026, 8, 20), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    CH, e12, LocalDate.of(2026, 7, 12), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    RO, e12, LocalDate.of(2026, 7, 14), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    EL, e12, LocalDate.of(2026, 7, 25), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    JW, e12, LocalDate.of(2026, 8, 5), INSCRIPTO));

            em.persist(new InscripcionEdicion(
                    CO, e13, LocalDate.of(2026, 3, 5), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    WE, e13, LocalDate.of(2026, 3, 4), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    RO, e13, LocalDate.of(2026, 3, 7), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    WE, e14, LocalDate.of(2026, 7, 15), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CO, e14, LocalDate.of(2026, 7, 20), RECHAZADO));

            em.persist(new InscripcionEdicion(
                    RO, e14, LocalDate.of(2026, 8, 6), ACEPTADO));

            em.persist(new InscripcionEdicion(
                    CH, e14, LocalDate.of(2026, 8, 30), RECHAZADO));

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
                    new ArrayList<>(),
                    new ArrayList<>(),
                    "Programa.EFI Ingeniería Mecánica.jpeg"
            );

            ProgramaFormacion p2 = new ProgramaFormacion(
                    "Formación integral",
                    "Programa varios institutos",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 7, 15),
                    LocalDate.of(2027, 1, 1),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    "Programa.Formación integral.jpeg"
            );

            ProgramaFormacion p3 = new ProgramaFormacion(
                    "EFI Robótica",
                    "Programa robótica",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 9, 3),
                    LocalDate.of(2026, 11, 18),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    "Programa.EFI Robótica.jpeg"
            );

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            
            // *****************************************************************
            // 10 INSCRIPCION A PROGRAMA
            // *****************************************************************


            em.persist(new InscripcionPrograma(
                    EL, p1, LocalDate.of(2026, 4, 15), 0));
            
            em.persist(new InscripcionPrograma(
                    CH, p1, LocalDate.of(2026, 4, 20), 0));

            em.persist(new InscripcionPrograma(
                    CO, p2, LocalDate.of(2026, 7, 10), 0));

            em.persist(new InscripcionPrograma(
                    CH, p2, LocalDate.of(2026, 7, 10), 0));

            em.persist(new InscripcionPrograma(
                    EL, p2, LocalDate.of(2026, 7, 12), 0));
            
            em.persist(new InscripcionPrograma(
                    WE, p2, LocalDate.of(2026, 7, 15), 0));

            em.persist(new InscripcionPrograma(
                    RO, p2, LocalDate.of(2026, 7, 18), 0));

            em.persist(new InscripcionPrograma(
                    CO, p3, LocalDate.of(2026, 8, 11), 0));

            em.persist(new InscripcionPrograma(
                    CH, p3, LocalDate.of(2026, 8, 15), 0));

            em.persist(new InscripcionPrograma(
                    RO, p3, LocalDate.of(2026, 8, 22), 0));

            em.persist(new InscripcionPrograma(
                    JW, p3, LocalDate.of(2026, 8, 30), 0));
            
            
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
            
            Utils.Imagen.extractImage(RO.getImagen());
            Utils.Imagen.extractImage(CH.getImagen());
            Utils.Imagen.extractImage(JW.getImagen());
            Utils.Imagen.extractImage(WW.getImagen());
            Utils.Imagen.extractImage(OK.getImagen());
            Utils.Imagen.extractImage(EW.getImagen());
            Utils.Imagen.extractImage(GH.getImagen());
            Utils.Imagen.extractImage(TC.getImagen());

            Utils.Imagen.extractImage(c3.getImagen());
            Utils.Imagen.extractImage(c5.getImagen());
            Utils.Imagen.extractImage(c6.getImagen());
            Utils.Imagen.extractImage(c7.getImagen());
            Utils.Imagen.extractImage(c8.getImagen());
            Utils.Imagen.extractImage(c10.getImagen());
            
            Utils.Imagen.extractImage(e1.getImagen());
            Utils.Imagen.extractImage(e2.getImagen());
            Utils.Imagen.extractImage(e3.getImagen());
            Utils.Imagen.extractImage(e4.getImagen());
            Utils.Imagen.extractImage(e7.getImagen());
            Utils.Imagen.extractImage(e8.getImagen());
            Utils.Imagen.extractImage(e9.getImagen());

            Utils.Imagen.extractImage(p1.getImagen());
            Utils.Imagen.extractImage(p2.getImagen());
            Utils.Imagen.extractImage(p3.getImagen());
            
            
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