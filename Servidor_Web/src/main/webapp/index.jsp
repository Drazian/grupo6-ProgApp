<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>EDEXT</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

    <!-- SECTOR 1: Barra Superior Completa -->
    <header class="sector-1">
        <div class="logo">MiLogo</div>
        
        <div class="buscador">
            <input type="text" id="input-busqueda" placeholder="Buscar..." onkeypress="if(event.key === 'Enter') BuscadorServlet()">
            <button type="button" onclick="BuscadorServlet()">Buscar</button>
        </div>      
        
        <!-- Bloque de Sesión Dinámico -->
        <div class="sesion" style="display: flex; align-items: center; gap: 15px;">
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogueado}">
                    <!-- Si hay sesión: Mostramos Imagen, Saludo con Rol y botón Salir -->
                    <div style="display: flex; align-items: center; gap: 10px;">
                        
                        <!-- Foto de perfil servida dinámicamente desde ImagenServlet -->
                        <img src="ImagenServlet?nombre=${empty sessionScope.usuarioLogueado.imagen ? 'usr.png' : sessionScope.usuarioLogueado.imagen}" 
                             alt="Foto de perfil" 
                             style="width: 35px; height: 35px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">

                        <span style="font-weight: bold;">
                            Hola, ${sessionScope.usuarioLogueado.nombre} ${sessionScope.usuarioLogueado.apellido}
                            <span style="font-size: 12px; font-weight: normal; opacity: 0.8;">(${sessionScope.usuarioLogueado.tipoUsuario})</span>
                        </span>

                        <button onclick="cerrarSesion()" style="background-color: #d9534f; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 3px;">Cerrar Sesión</button>
                    
                        <button type="button" 
                                onclick="event.preventDefault(); cargarMiPerfil('${sessionScope.usuarioLogueado.nickname}');" 
                                style="background-color: #2980b9; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 13px;">
                            Mi Perfil
                        </button>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Si NO hay sesión: Botón de Iniciar Sesión -->
                    <button onclick="cargarLogin()" style="background-color: #5cb85c; color: white; border: none; padding: 5px 15px; cursor: pointer; border-radius: 3px; font-weight: bold;">Iniciar Sesión</button>
                    <button onclick="cargarAltaUsuario()" style="background-color: #0275d8; color: white; border: none; padding: 5px 15px; cursor: pointer; border-radius: 3px; font-weight: bold;">Registrarse</button>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <div class="contenedor-inferior">
        <!-- SECTOR 2: Menú Lateral Izquierdo -->
        <aside class="sector-2">
            <nav>
                <!-- Grupo 1: Institutos (Dinamico) -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Institutos</summary>
                    <ul id="lista-institutos" style="list-style: none; padding-left: 15px;">
                        <li style="font-size: 12px; opacity: 0.7;">Cargando...</li>
                    </ul>
                </details>

               
                
                <!-- Menú Usuarios -->
                <c:if test="${not empty sessionScope.usuarioLogueado}">
                 <hr style="opacity: 0.2; margin: 10px 0;">
                    <details>
                        <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Mi Perfil</summary>
                        <ul style="list-style: none; padding-left: 15px; margin-top: 5px;">
                                <li><a href="#" onclick="cargarModificarUsuario(); return false;">Modificar perfil</a></li>
                                <li><a href="#" onclick="cargarSocial(); return false;">Social</a></li>

                        </ul>
                    </details>
                </c:if>
                <hr style="border: 0; border-top: 1px solid rgba(255, 255, 255, 0.2); margin: 5px 0;">

                <!-- Grupo 2: Categorias (Dinamico) -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Categorias</summary>
                    <ul id="lista-categorias" style="list-style: none; padding-left: 15px;">
                        <li style="font-size: 12px; opacity: 0.7;">Cargando...</li>
                    </ul>
                </details>

                <hr style="opacity: 0.2; margin: 10px 0;"> 
                           
                <!-- Grupo Usuarios -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Usuarios</summary>
                    <ul style="list-style: none; padding-left: 15px; margin: 5px 0 0 0;">
                        <li style="margin-bottom: 5px;">
                            <a href="#" onclick="event.preventDefault(); cargarConsultaUsuario();" style="text-decoration: none; color: inherit; font-size: 14px;">
                                Ver información de usuarios
                            </a>
                        </li>
                    </ul>
                </details>

                <hr style="opacity: 0.2; margin: 10px 0;"> 
                
                <!-- Grupo 3: Cursos e Inscripciones (Restringido a Docentes) -->
                <c:if test="${not empty sessionScope.usuarioLogueado and sessionScope.usuarioLogueado.tipoUsuario == 'DOCENTE'}">
                    <details>
                        <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Cursos</summary>
                        <ul style="list-style: none; padding-left: 15px;">
                            <li><a href="#" onclick="cargarAltaCurso()">Alta Curso</a></li>
                            <li><a href="#" onclick="cargarAltaEdicion()">Alta Edición de Curso</a></li>
                            <li><a href="#" onclick="cargarFormSeleccionEstudiantes()">Evaluar Inscripciones</a></li>
                        </ul>                      
                    </details>
                    <hr style="opacity: 0.2; margin: 10px 0;">
                </c:if>
                
                <!-- Grupo: Estudiantes (Restringido a Estudiantes) -->
                <c:if test="${not empty sessionScope.usuarioLogueado and sessionScope.usuarioLogueado.tipoUsuario == 'ESTUDIANTE'}">
                    <details>
                        <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Inscripciones</summary>
                        <ul style="list-style: none; padding-left: 15px;">
                            <li><a href="#" onclick="cargarFormInscripcion()">Inscripción a Edición</a></li>
                        </ul>
                    </details>
                    <hr style="opacity: 0.2; margin: 10px 0;">
                </c:if>    
                    
                <!-- Grupo 4: Programas de Formación (Estatico) -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Programas</summary>
                    <ul style="list-style: none; padding-left: 15px;">
                        <li><a href="#" onclick="ProgramaServlet('formCrear')">Crear Programa</a></li>
                        <li><a href="#" onclick="ProgramaServlet('formAgregarCurso')">Agregar curso a Programa</a></li>
                        <li><a href="#" onclick="ProgramaServlet('formVerProgramas')">Ver Programas</a></li>
                    </ul>
                </details>

                <hr style="opacity: 0.2; margin: 10px 0;">

                <!-- Grupo 5: Funciones Generales / Legacy (Estatico) -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Opciones Generales</summary>
                    <ul style="list-style: none; padding-left: 15px;">
                        <li><a href="#" onclick="cargarSeccion('productos')">Ver Productos</a></li>
                        <li><a href="#" onclick="cargarSeccion('abrirFormulario')">Agregar Producto</a></li>
                        <li><a href="#" onclick="cargarSeccion('reportes')">Reportes</a></li>
                        <li><a href="#" onclick="cargarSeccion('configuracion')">Configuración</a></li>
                        <li><a href="#" onclick="testServlet()">TestTemporal</a></li>                     
                    </ul>
                </details>
            </nav>
        </aside>

        <!-- SECTOR 3: Panel de Contenido Dinámico -->
        <main class="sector-3" id="contenido-dinamico">
            <h2>Bienvenido</h2>
            <p>Selecciona una opción del menú o realiza una búsqueda.</p>
        </main>
    </div>

    <script src="js/app.js"></script>
</body>
</html>