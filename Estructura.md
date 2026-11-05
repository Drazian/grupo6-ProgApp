└── edEXT/
    ├── nb-configuration.xml
    ├── pom.xml
    ├── Estructura.md
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
    │           │   │   └── fragmentos/
    │           │   │       ├── listarAceptados.jsp
    │           │   │       ├── consultaCurso.jsp
    │           │   │       ├── fragmentos.7z
    │           │   │       ├── programaDetalles.jsp
    │           │   │       ├── altaCurso.jsp
    │           │   │       ├── error.jsp
    │           │   │       ├── tablaAceptados.jsp
    │           │   │       ├── test.jsp
    │           │   │       ├── inscripcionEdicion.jsp
    │           │   │       ├── listaCursos.jsp
    │           │   │       ├── inscripcionPrograma.jsp
    │           │   │       ├── resultados.jsp
    │           │   │       ├── verProgramas.jsp
    │           │   │       ├── busqueda.jsp
    │           │   │       ├── crearPrograma.jsp
    │           │   │       ├── altaUsuario.jsp
    │           │   │       ├── seguidos.jsp
    │           │   │       ├── ModificarUsuario.jsp
    │           │   │       ├── login.jsp
    │           │   │       ├── altaEdicion.jsp
    │           │   │       ├── consultaUsuario.jsp
    │           │   │       ├── resultadosInscripciones.jsp
    │           │   │       ├── consultaEdicion.jsp
    │           │   │       ├── seleccionEstudiantes.jsp
    │           │   │       ├── agregarCursoPrograma.jsp
    │           │   │       ├── detallePerfil.jsp
    │           │   │       ├── detalleConsultaUsuario.jsp
    │           │   │       └── tablaInscripciones.jsp
    │           │   └── imagenes/
    │           │       └── usr.png
    │           └── java/
    │               └── edu/
    │                   └── edext/
    │                       ├── tools/
    │                       │   ├── GestorImagenes.java
    │                       │   ├── cfg.java
    │                       │   └── Utils.java
    │                       ├── controlador/
    │                       │   ├── Fabrica.java
    │                       │   ├── UsuarioServlet.java
    │                       │   ├── InscripcionServlet.java
    │                       │   ├── LoginHelper.java
    │                       │   ├── CursoServlet.java
    │                       │   ├── LoginServlet.java
    │                       │   ├── ImagenServlet.java
    │                       │   ├── EdicionServlet.java
    │                       │   ├── BuscadorServlet.java
    │                       │   ├── MiControladorServlet.java
    │                       │   ├── TestServlet.java
    │                       │   └── ProgramaServlet.java
    │                       └── modelo/
    │                           ├── DtResultadoBusqueda.java
    │                           └── Remux.java
    └── Servidor_Central/
        ├── nb-configuration.xml
        ├── pom.xml
        ├── nbactions.xml
        └── src/
            ├── main/
            │   ├── resources/
            │   │   ├── tinylog.properties
            │   │   ├── META-INF/
            │   │   │   ├── persistence.xml
            │   │   │   └── persistence(mysql Lab16).xml
            │   │   └── Images/
            │   │       ├── Edicion.Taller de robótica educativa - 2026-2.jpeg
            │   │       ├── Edicion.Flor del Ceibo - 2010.jpeg
            │   │       ├── Programa.Formación integral.jpeg
            │   │       ├── jeffw.jpeg
            │   │       ├── Curso.Flor del Ceibo.png
            │   │       ├── Edicion.Flor del Ceibo - 2025.jpeg
            │   │       ├── Curso.Inclusión Energética.jpg
            │   │       ├── waston.jpg
            │   │       ├── Edicion.Dalavuelta - 2025.jpeg
            │   │       ├── house.jpeg
            │   │       ├── Edicion.Taller de robótica educativa - 2024.jpeg
            │   │       ├── Programa.EFI Ingeniería Mecánica.jpeg
            │   │       ├── Programa.EFI Robótica.jpeg
            │   │       ├── Curso.Dalavuelta.png
            │   │       ├── Edicion.Flor del Ceibo - 2012.jpg
            │   │       ├── Curso.Taller de robótica educativa.jpg
            │   │       ├── usr.png
            │   │       ├── Curso.Participación en investigación sobre el empleo del juego Komikan como recurso didáctico en la Escuela.png
            │   │       ├── Curso.MicroBit.jpg
            │   │       ├── heisenberg.png
            │   │       ├── Edicion.Taller de robótica educativa - 2026.jpeg
            │   │       ├── timmy.jpeg
            │   │       ├── roro.jpeg
            │   │       ├── chechi.jpeg
            │   │       └── benkenobi.jpg
            │   └── java/
            │       └── edu/
            │           └── edext/
            │               ├── edEXT.java
            │               ├── tools/
            │               │   ├── jTable.java
            │               │   ├── cfg.java
            │               │   ├── cronometroHelper.java
            │               │   ├── ArbolHelper.java
            │               │   ├── indexHelper.java
            │               │   ├── ConsultaEdicionHelper.java
            │               │   ├── CrearProgramaHelper.java
            │               │   ├── InscripcionEdicionHelper.java
            │               │   └── Utils.java
            │               ├── datatypes/
            │               │   ├── DtInscripcionPrograma.java
            │               │   ├── DtCurso.java
            │               │   ├── DtInstituto.java
            │               │   ├── DtPrograma.java
            │               │   ├── DtEdicion.java
            │               │   ├── LocalDateAdapter.java
            │               │   ├── DtCategoria.java
            │               │   ├── MapAdapter.java
            │               │   ├── DtUsuario.java
            │               │   ├── DtInscripcionEdicion.java
            │               │   ├── TipoUsuario.java
            │               │   └── DtConsultaCurso.java
            │               ├── logica/
            │               │   ├── Fabrica.java
            │               │   ├── GestorImagenes.java
            │               │   ├── ControladorHelper.java
            │               │   ├── UsuarioHelper.java
            │               │   ├── ProgramaFormacionHelper.java
            │               │   ├── InscripcionProgramaHelper.java
            │               │   ├── EdicionHelper.java
            │               │   ├── cargarDatosDePruebaHelper.java
            │               │   ├── IBridgeSOAP.java
            │               │   ├── BridgeSOAP.java
            │               │   ├── IControlador.java
            │               │   ├── Controlador.java
            │               │   └── InscripcionEdicionHelper.java
            │               ├── presentacion/
            │               │   ├── AltaInstituto.form
            │               │   ├── ConsultaEdicion.form
            │               │   ├── AltaUsuario.form
            │               │   ├── ConsultaEdicion.java
            │               │   ├── ConsultaPrograma.form
            │               │   ├── Config.java
            │               │   ├── AltaCurso.java
            │               │   ├── InscripcionEdicion.form
            │               │   ├── Config.form
            │               │   ├── CrearProgramaForm.java
            │               │   ├── agregarCursoPrograma.java
            │               │   ├── ConsultaUsuario.form
            │               │   ├── AltaUsuario.java
            │               │   ├── index.java
            │               │   ├── ConsultaCurso.java
            │               │   ├── AltaCategoria.form
            │               │   ├── Arbol.java
            │               │   ├── AltaInstituto.java
            │               │   ├── ConsultaUsuario.java
            │               │   ├── InscripcionEdicion.java
            │               │   ├── ConsultaCurso.form
            │               │   ├── AltaCurso.form
            │               │   ├── ModificarUsuario.java
            │               │   ├── CrearProgramaForm.form
            │               │   ├── Arbol.form
            │               │   ├── AltaCategoria.java
            │               │   ├── ModificarUsuario.form
            │               │   ├── AltaEdicion.java
            │               │   ├── AltaEdicion.form
            │               │   ├── index.form
            │               │   ├── ConsultaPrograma.java
            │               │   └── agregarCursoPrograma.form
            │               └── entitys/
            │                   ├── Instituto.java
            │                   ├── ProgramaFormacion.java
            │                   ├── Usuario.java
            │                   ├── InscripcionEdicion.java
            │                   ├── InscripcionEdicionID.java
            │                   ├── Estudiante.java
            │                   ├── Categoria.java
            │                   ├── Curso.java
            │                   ├── InscripcionProgramaID.java
            │                   ├── Docente.java
            │                   ├── InscripcionPrograma.java
            │                   └── Edicion.java
            └── test/
                ├── resources/
                │   └── Images/
                │       └── benkenobi.jpg
                └── java/
                    └── edu/
                        └── edext/
                            └── logica/
                                └── ControladorTest.java

