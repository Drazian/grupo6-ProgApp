// Función para cargar fragmentos de vistas desde el menú
function cargarSeccion(opcion) {
    const contenedor = document.getElementById('contenido-dinamico');
    
    // Llamada al Controlador (Servlet) pidiendo una acción específica
    fetch(`MiControladorServlet?accion=${opcion}`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
        })
        .catch(error => console.error('Error al cargar la sección:', error));
}

// Función para el botón de búsqueda del Sector 1
function ejecutarBusqueda() {
    const query = document.getElementById('input-busqueda').value;
    const contenedor = document.getElementById('contenido-dinamico');

    fetch(`MiControladorServlet?accion=buscar&query=${encodeURIComponent(query)}`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
        })
        .catch(error => console.error('Error en la búsqueda:', error));
}

// Función para interceptar y enviar formularios con AJAX sin recargar la página
function guardarProducto(event) {
    event.preventDefault(); // Evita que la página se recargue por defecto
    
    const formulario = document.getElementById('form-registro');
    const formData = new URLSearchParams(new FormData(formulario)); // Prepara los datos

    fetch('MiControladorServlet', {
        method: 'POST',
        body: formData,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    .then(response => response.text())
    .then(htmlRespuesta => {
        // Mostramos el mensaje de éxito directamente en el contenedor del formulario
        document.getElementById('resultado-registro').innerHTML = htmlRespuesta;
        formulario.reset(); // Limpia los inputs
    })
    .catch(error => console.error('Error al guardar:', error));
}

