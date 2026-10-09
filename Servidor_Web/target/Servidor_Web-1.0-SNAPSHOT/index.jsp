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
        <div class="sesion">Usuario: Juan Pérez</div>
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
                
                <!-- Grupo 3: Programas de Formación (Estatico) -->
                <details open>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Programas</summary>
                    <ul style="list-style: none; padding-left: 15px;">
                        <li><a href="#" onclick="ProgramaServlet('formCrear')">Crear Programa</a></li>
                        <li><a href="#" onclick="ProgramaServlet('formAgregarCurso')">Agregar curso a Programa</a></li>
                        <li><a href="#" onclick="ProgramaServlet('formVerProgramas')">Ver Programas</a></li>
                    </ul>
                </details>

                <hr style="opacity: 0.2; margin: 10px 0;">
                
                                <!-- Grupo 4: Usuarios -->
                <details>
                    <summary style="cursor: pointer; font-weight: bold; padding: 5px 0;">Usuarios</summary>
                    <ul style="list-style: none; padding-left: 15px;">
                        <li><a href="#" onclick="event.preventDefault(); UsuarioServlet('formAlta')">Alta de usuario</a></li>
                        <li><a href="#" onclick="event.preventDefault(); UsuarioServlet('formModificar')">Modificar usuario</a></li>
                        <li><a href="#" onclick="event.preventDefault(); UsuarioServlet('formConsultar')">Consultar usuario</a></li>
                        <li><a href="#" onclick="event.preventDefault(); UsuarioServlet('formSeguidos')">Usuarios seguidos</a></li>
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

