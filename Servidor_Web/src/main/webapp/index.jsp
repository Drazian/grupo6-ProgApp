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
            <input type="text" id="input-busqueda" placeholder="Buscar...">
            <button onclick="ejecutarBusqueda()">Buscar</button>
        </div>
        
        <!-- Bloque de Sesión Dinámico -->
        <div class="sesion" style="display: flex; align-items: center; gap: 15px;">
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogueado}">
                    <!-- Si hay sesión: Mostramos Imagen, Nombre, Rol y botón Salir -->
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <img src="imagenes/${sessionScope.usuarioLogueado.imagen}" alt="Perfil" style="width: 35px; height: 35px; border-radius: 50%; object-fit: cover; border: 1px solid white;">
                        <span style="font-weight: bold;">
                            Hola, ${sessionScope.usuarioLogueado.nombre} 
                            <span style="font-size: 12px; font-weight: normal; opacity: 0.8;">(${sessionScope.usuarioLogueado.tipoUsuario})</span>
                        </span>
                        <button onclick="cerrarSesion()" style="background-color: #d9534f; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 3px;">Cerrar Sesión</button>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Si NO hay sesión: Botón de Iniciar Sesión -->
                    <button onclick="cargarLogin()" style="background-color: #5cb85c; color: white; border: none; padding: 5px 15px; cursor: pointer; border-radius: 3px; font-weight: bold;">Iniciar Sesión</button>
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

                <hr style="opacity: 0.2; margin: 10px 0;">

                <!-- Grupo 2: Categorias (Dinamico) -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Categorias</summary>
                    <ul id="lista-categorias" style="list-style: none; padding-left: 15px;">
                        <li style="font-size: 12px; opacity: 0.7;">Cargando...</li>
                    </ul>
                </details>

                <hr style="opacity: 0.2; margin: 10px 0;">                
                
                <!-- Grupo 3: Cursos (Restringido a Docentes) -->
                <c:if test="${not empty sessionScope.usuarioLogueado and sessionScope.usuarioLogueado.tipoUsuario == 'DOCENTE'}">
                    <details open>
                        <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Cursos</summary>
                        <ul style="list-style: none; padding-left: 15px;">
                            <li><a href="#" onclick="cargarAltaCurso()">Alta Curso</a></li>
                            <!-- Aquí agregarás "Alta edición" en el futuro -->
                        </ul>
                    </details>
                    <hr style="opacity: 0.2; margin: 10px 0;">
                </c:if>
                
                <!-- Grupo 4: Programas de Formación (Estatico) -->
                <details open>
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
                        <li><a href="#" onclick="cargarAltaUsuario()">Alta de Usuario</a></li>                     
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