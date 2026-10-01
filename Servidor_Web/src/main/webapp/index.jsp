<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Aplicación MVC</title>
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
                <ul>
                    <li><a href="#" onclick="cargarSeccion('productos')">Ver Productos</a></li>
                    <li><a href="#" onclick="cargarSeccion('abrirFormulario')">Agregar Producto</a></li>
                    <li><a href="#" onclick="cargarSeccion('reportes')">reportes</a></li>
                    <li><a href="#" onclick="cargarSeccion('configuracion')">Configuración</a></li>
                </ul>
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

