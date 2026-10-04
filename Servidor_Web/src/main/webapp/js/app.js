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













function testServlet(){
    const contenedor = document.getElementById('contenido-dinamico');
    
    fetch(`TestServlet`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
        })
        .catch(error => console.error('Error al cargar la sección:', error));    
}

function crearInstituto(){
    const nombre = document.getElementById("nombre").value;
    
    if (!nombre.trim()){
        alert("Ingrese un nombre valido");
        return;
    }
    
    const params = new URLSearchParams();
    params.append('nombre', nombre);
    
fetch('TestServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: params
    })
    .then(response => {
        return response.text().then(texto => {
            if (!response.ok) {
                throw new Error(texto);
            }
            return texto;
        });
    })
    .then(mensajeExito => {
        alert(mensajeExito);
        testServlet(); //Reinvocamos el GET
    })
    .catch(error => {
        alert(error.message);
    });
    
}








function ProgramaServlet(opcion) {
    const contenedor = document.getElementById('contenido-dinamico');
    
    // Llamada al Controlador (Servlet) pidiendo una acción específica
    fetch(`ProgramaServlet?accion=${opcion}`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
        })
        .catch(error => console.error('Error al cargar la sección:', error));
}

function crearPrograma(){
    const nombre = document.getElementById("nombre").value;
    const descripcion = document.getElementById("desc").value;
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;
    
    if (!nombre.trim()){
        alert("Ingrese un nombre valido");
        return;
    }

    if (!descripcion.trim()){
        alert("Ingrese una descripcion valida");
        return;
    }
    
        if (!fechaInicio.trim() || !fechaFin.trim()){
        alert("Ingrese un periodo valido");
        return;
    }
    
    
    const params = new URLSearchParams();
    params.append('nombre', nombre);
    params.append('desc', descripcion);
    params.append('fechaInicio', fechaInicio);
    params.append('fechaFin', fechaFin);
    
fetch('ProgramaServlet?accion=crear', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: params
    })
    .then(response => {
        return response.text().then(texto => {
            if (!response.ok) {
                throw new Error(texto);
            }
            return texto;
        });
    })
    .then(mensajeExito => {
        alert(mensajeExito);
        ProgramaServlet("formCrear"); //Reinvocamos el GET
    })
    .catch(error => {
        alert(error.message);
    });
    
}