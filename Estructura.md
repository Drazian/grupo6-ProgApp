└── edEXT/
    ├── install.sh
    ├── .gitignore
    ├── nb-configuration.xml
    ├── deploy
    ├── pom.xml
    ├── tomcat-users.xml
    ├── miapp.png
    ├── nbactions.xml
    ├── Servidor_Web/
    │   ├── nb-configuration.xml
    │   ├── pom.xml
    │   ├── nbactions.xml
    │   └── src/
    │       ├── test/
    │       │   └── java/
    │       │       └── .gitkeep
    │       └── main/
    │           ├── resources/
    │           │   ├── tinylog.properties
    │           │   └── META-INF/
    │           │       └── .gitkeep
    │           ├── webapp/
    │           │   ├── index.jsp
    │           │   ├── META-INF/
    │           │   │   └── context.xml
    │           │   ├── js/
    │           │   │   └── app.js
    │           │   ├── css/
    │           │   │   └── estilos.css
    │           │   ├── WEB-INF/
    │           │   │   ├── beans.xml
    │           │   │   ├── web.xml
    │           │   │   ├── fragmentos/
    │           │   │   │   ├── ingresarResultados.jsp
    │           │   │   │   ├── listarAceptados.jsp
    │           │   │   │   ├── consultaCurso.jsp
    │           │   │   │   ├── programaDetalles.jsp
    │           │   │   │   ├── tendencias.jsp
    │           │   │   │   ├── altaCurso.jsp
    │           │   │   │   ├── verResultadosEdicion.jsp
    │           │   │   │   ├── error.jsp
    │           │   │   │   ├── tablaAceptados.jsp
    │           │   │   │   ├── test.jsp
    │           │   │   │   ├── inscripcionEdicion.jsp
    │           │   │   │   ├── listaCursos.jsp
    │           │   │   │   ├── inscripcionPrograma.jsp
    │           │   │   │   ├── resultados.jsp
    │           │   │   │   ├── verProgramas.jsp
    │           │   │   │   ├── busqueda.jsp
    │           │   │   │   ├── crearPrograma.jsp
    │           │   │   │   ├── altaUsuario.jsp
    │           │   │   │   ├── tablaActaEdicion.jsp
    │           │   │   │   ├── seguidos.jsp
    │           │   │   │   ├── ModificarUsuario.jsp
    │           │   │   │   ├── login.jsp
    │           │   │   │   ├── altaEdicion.jsp
    │           │   │   │   ├── consultaUsuario.jsp
    │           │   │   │   ├── resultadosInscripciones.jsp
    │           │   │   │   ├── tablaIngresoResultados.jsp
    │           │   │   │   ├── consultaEdicion.jsp
    │           │   │   │   ├── seleccionEstudiantes.jsp
    │           │   │   │   ├── resultadosEvaluaciones.jsp
    │           │   │   │   ├── agregarCursoPrograma.jsp
    │           │   │   │   ├── detallePerfil.jsp
    │           │   │   │   ├── datosEdicionResultados.jspf
    │           │   │   │   ├── detalleValoracion.jsp
    │           │   │   │   ├── valorarCurso.jsp
    │           │   │   │   ├── detalleConsultaUsuario.jsp
    │           │   │   │   └── tablaInscripciones.jsp
    │           │   │   ├── movil/
    │           │   │   │   ├── edicion.jsp
    │           │   │   │   ├── pie.jspf
    │           │   │   │   ├── cursos.jsp
    │           │   │   │   ├── inicio.jsp
    │           │   │   │   ├── login.jsp
    │           │   │   │   ├── curso.jsp
    │           │   │   │   ├── mensaje.jsp
    │           │   │   │   ├── ediciones.jsp
    │           │   │   │   └── cabecera.jspf
    │           │   │   └── tags/
    │           │   │       ├── resumenValoraciones.tag
    │           │   │       └── estrellas.tag
    │           │   ├── imagenes/
    │           │   │   └── usr.png
    │           │   └── movil/
    │           │       └── static/
    │           │           ├── img/
    │           │           │   └── logo-edext.svg
    │           │           ├── js/
    │           │           │   └── bootstrap.bundle.min.js
    │           │           └── css/
    │           │               ├── bootstrap.min.css
    │           │               └── movil.css
    │           └── java/
    │               └── edu/
    │                   └── edext/
    │                       ├── tools/
    │                       │   ├── cfgw.java
    │                       │   └── Utilsw.java
    │                       ├── controlador/
    │                       │   ├── Fabrica.java
    │                       │   ├── UsuarioServlet.java
    │                       │   ├── TendenciasServlet.java
    │                       │   ├── VisitasCursoFilter.java
    │                       │   ├── InscripcionServlet.java
    │                       │   ├── LoginHelper.java
    │                       │   ├── CursoServlet.java
    │                       │   ├── LoginServlet.java
    │                       │   ├── ImagenServlet.java
    │                       │   ├── EdicionServlet.java
    │                       │   ├── DispositivoMovilFilter.java
    │                       │   ├── ActaEstudioServlet.java
    │                       │   ├── MovilServlet.java
    │                       │   ├── BuscadorServlet.java
    │                       │   ├── MiControladorServlet.java
    │                       │   ├── TestServlet.java
    │                       │   ├── ProgramaServlet.java
    │                       │   └── ValoracionServlet.java
    │                       ├── resources/
    │                       │   └── .gitkeep
    │                       └── modelo/
    │                           ├── ActaEstudioPdf.java
    │                           ├── DtResultadoBusqueda.java
    │                           ├── Remux.java
    │                           └── RecordarmeMovil.java
    ├── Servidor_Central/
    │   ├── nb-configuration.xml
    │   ├── pom.xml
    │   ├── nbactions.xml
    │   └── src/
    │       ├── main/
    │       │   ├── resources/
    │       │   │   ├── tinylog.properties
    │       │   │   ├── META-INF/
    │       │   │   │   ├── persistence.xml
    │       │   │   │   └── persistence (maria).xml
    │       │   │   └── Images/
    │       │   │       ├── Edicion.Taller de robótica educativa - 2026-2.jpeg
    │       │   │       ├── Edicion.Flor del Ceibo - 2010.jpeg
    │       │   │       ├── Programa.Formación integral.jpeg
    │       │   │       ├── jeffw.jpeg
    │       │   │       ├── Curso.Flor del Ceibo.png
    │       │   │       ├── Edicion.Flor del Ceibo - 2025.jpeg
    │       │   │       ├── Curso.Inclusión Energética.jpg
    │       │   │       ├── waston.jpg
    │       │   │       ├── Edicion.Dalavuelta - 2025.jpeg
    │       │   │       ├── house.jpeg
    │       │   │       ├── Edicion.Taller de robótica educativa - 2024.jpeg
    │       │   │       ├── Programa.EFI Ingeniería Mecánica.jpeg
    │       │   │       ├── Programa.EFI Robótica.jpeg
    │       │   │       ├── Curso.Dalavuelta.png
    │       │   │       ├── Edicion.Flor del Ceibo - 2012.jpg
    │       │   │       ├── Curso.Taller de robótica educativa.jpg
    │       │   │       ├── usr.png
    │       │   │       ├── Curso.Participación en investigación sobre el empleo del juego Komikan como recurso didáctico en la Escuela.png
    │       │   │       ├── Curso.MicroBit.jpg
    │       │   │       ├── heisenberg.png
    │       │   │       ├── Edicion.Taller de robótica educativa - 2026.jpeg
    │       │   │       ├── timmy.jpeg
    │       │   │       ├── roro.jpeg
    │       │   │       ├── chechi.jpeg
    │       │   │       └── benkenobi.jpg
    │       │   └── java/
    │       │       └── edu/
    │       │           └── edext/
    │       │               ├── edEXT.java
    │       │               ├── tools/
    │       │               │   ├── jTable.java
    │       │               │   ├── cfg.java
    │       │               │   ├── cronometroHelper.java
    │       │               │   ├── ArbolHelper.java
    │       │               │   ├── indexHelper.java
    │       │               │   ├── ConsultaEdicionHelper.java
    │       │               │   ├── ConfigBaseDatos.java
    │       │               │   ├── CrearProgramaHelper.java
    │       │               │   ├── InscripcionEdicionHelper.java
    │       │               │   └── Utils.java
    │       │               ├── datatypes/
    │       │               │   ├── DtInscripcionPrograma.java
    │       │               │   ├── DtCurso.java
    │       │               │   ├── DtValoracionesCurso.java
    │       │               │   ├── DtActaEstudio.java
    │       │               │   ├── DtInstituto.java
    │       │               │   ├── DtPrograma.java
    │       │               │   ├── DtEdicion.java
    │       │               │   ├── LocalDateAdapter.java
    │       │               │   ├── DtCategoria.java
    │       │               │   ├── ResultadoCurso.java
    │       │               │   ├── MapAdapter.java
    │       │               │   ├── DtUsuario.java
    │       │               │   ├── DtInscripcionEdicion.java
    │       │               │   ├── DtCursoVisitas.java
    │       │               │   ├── DtResultadoCurso.java
    │       │               │   ├── TipoUsuario.java
    │       │               │   └── DtConsultaCurso.java
    │       │               ├── logica/
    │       │               │   ├── Fabrica.java
    │       │               │   ├── GestorImagenes.java
    │       │               │   ├── ControladorHelper.java
    │       │               │   ├── UsuarioHelper.java
    │       │               │   ├── ProgramaFormacionHelper.java
    │       │               │   ├── InscripcionProgramaHelper.java
    │       │               │   ├── EdicionHelper.java
    │       │               │   ├── cargarDatosDePruebaHelper.java
    │       │               │   ├── IBridgeSOAP.java
    │       │               │   ├── BridgeSOAP.java
    │       │               │   ├── IControlador.java
    │       │               │   ├── ValoracionHelper.java
    │       │               │   ├── ActaEstudioHelper.java
    │       │               │   ├── Controlador.java
    │       │               │   ├── InscripcionEdicionHelper.java
    │       │               │   └── TendenciasHelper.java
    │       │               ├── presentacion/
    │       │               │   ├── AltaInstituto.form
    │       │               │   ├── ConsultaEdicion.form
    │       │               │   ├── AltaUsuario.form
    │       │               │   ├── About.form
    │       │               │   ├── ConsultaEdicion.java
    │       │               │   ├── ConsultaPrograma.form
    │       │               │   ├── Config.java
    │       │               │   ├── AltaCurso.java
    │       │               │   ├── InscripcionEdicion.form
    │       │               │   ├── Config.form
    │       │               │   ├── CrearProgramaForm.java
    │       │               │   ├── agregarCursoPrograma.java
    │       │               │   ├── ConsultaUsuario.form
    │       │               │   ├── AltaUsuario.java
    │       │               │   ├── index.java
    │       │               │   ├── ConsultaCurso.java
    │       │               │   ├── About.java
    │       │               │   ├── AltaCategoria.form
    │       │               │   ├── Arbol.java
    │       │               │   ├── AltaInstituto.java
    │       │               │   ├── ConsultaUsuario.java
    │       │               │   ├── InscripcionEdicion.java
    │       │               │   ├── ConsultaCurso.form
    │       │               │   ├── AltaCurso.form
    │       │               │   ├── ModificarUsuario.java
    │       │               │   ├── CrearProgramaForm.form
    │       │               │   ├── Arbol.form
    │       │               │   ├── AltaCategoria.java
    │       │               │   ├── ModificarUsuario.form
    │       │               │   ├── AltaEdicion.java
    │       │               │   ├── AltaEdicion.form
    │       │               │   ├── index.form
    │       │               │   ├── ConsultaPrograma.java
    │       │               │   └── agregarCursoPrograma.form
    │       │               └── entitys/
    │       │                   ├── Instituto.java
    │       │                   ├── ValoracionCurso.java
    │       │                   ├── ProgramaFormacion.java
    │       │                   ├── Usuario.java
    │       │                   ├── ValoracionCursoID.java
    │       │                   ├── InscripcionEdicion.java
    │       │                   ├── InscripcionEdicionID.java
    │       │                   ├── Estudiante.java
    │       │                   ├── Categoria.java
    │       │                   ├── Curso.java
    │       │                   ├── InscripcionProgramaID.java
    │       │                   ├── Docente.java
    │       │                   ├── InscripcionPrograma.java
    │       │                   ├── VisitasCurso.java
    │       │                   └── Edicion.java
    │       └── test/
    │           ├── resources/
    │           │   └── Images/
    │           │       └── benkenobi.jpg
    │           └── java/
    │               └── edu/
    │                   └── edext/
    │                       └── logica/
    │                           └── ControladorTest.java
    ├── Test_Puertos/
    │   ├── pom.xml
    │   └── src/
    │       └── main/
    │           ├── resources/
    │           │   ├── tinylog.properties
    │           │   └── imagen/
    │           │       ├── modem.png
    │           │       ├── free.png
    │           │       ├── neutral.png
    │           │       └── busy.png
    │           └── java/
    │               └── edu/
    │                   └── edext/
    │                       ├── Test.form
    │                       ├── Test.java
    │                       └── tools/
    │                           ├── utils.java
    │                           └── cfg.java
    └── centralCli/
        ├── pom.xml
        ├── nbactions.xml
        └── src/
            └── main/
                ├── resources/
                │   └── tinylog.properties
                └── java/
                    └── edu/
                        └── edext/
                            ├── SimpleFTP.java
                            ├── Launcher.java
                            ├── centralCli.java
                            └── tools/
                                ├── utilsCCli.java
                                └── cfgCCli.java
 
