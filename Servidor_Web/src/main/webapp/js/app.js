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

//---------------------------------------------------------------------------------

//Para evitar tener que crear un Servlet extra se procede generando un evento para cargar los Institutos y Categorias del menu izquierdo.
document.addEventListener("DOMContentLoaded", function() {
    cargarInstitutosMenu();
    cargarCategoriasMenu();
});

function cargarInstitutosMenu() {
    fetch('MiControladorServlet?accion=obtenerInstitutosJSON')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener institutos');
            return response.json();
        })
        .then(institutos => {
            const ul = document.getElementById('lista-institutos');
            ul.innerHTML = '';

            if (!institutos || institutos.length === 0) {
                ul.innerHTML = '<li style="font-size: 12px; opacity: 0.7;">Sin institutos</li>';
                return;
            }

            institutos.forEach(inst => {
                const li = document.createElement('li');
                li.innerHTML = `<a href="#" onclick="event.preventDefault(); buscarCurso('instituto', '${inst.nombre}')">${inst.nombre}</a>`;
                ul.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error al cargar institutos:', error);
            document.getElementById('lista-institutos').innerHTML = 
                '<li style="font-size: 12px; color: red;">Error al cargar</li>';
        });
}

function cargarCategoriasMenu() {
    fetch('MiControladorServlet?accion=obtenerCategoriasJSON')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener categorías');
            return response.json();
        })
        .then(categorias => {
            const ul = document.getElementById('lista-categorias');
            ul.innerHTML = '';

            if (!categorias || categorias.length === 0) {
                ul.innerHTML = '<li style="font-size: 12px; opacity: 0.7;">Sin categorias</li>';
                return;
            }

            categorias.forEach(cat => {
                const li = document.createElement('li');
                li.innerHTML = `<a href="#" onclick="event.preventDefault(); buscarCurso('categoria', '${cat.nombre}')">${cat.nombre}</a>`;
                ul.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error al cargar categorías:', error);
            document.getElementById('lista-categorias').innerHTML = 
                '<li style="font-size: 12px; color: red;">Error al cargar</li>';
        });
}


function buscarCurso(opcion, nombre) {
    console.log(`Buscando cursos por ${opcion}: ${nombre}`);

    fetch(`CursoServlet?accion=buscarCurso&opcion=${encodeURIComponent(opcion)}&nombre=${encodeURIComponent(nombre)}`)
        .then(response => {
            if (!response.ok) throw new Error('Error en la búsqueda de cursos');
            return response.text();
        })
        .then(htmlResultado => {
            document.getElementById('contenido-dinamico').innerHTML = htmlResultado;
        })
        .catch(error => {
            console.error('Error al buscar cursos:', error);
            document.getElementById('contenido-dinamico').innerHTML = 
                '<div class="alerta-error">Ocurrió un error al cargar los cursos.</div>';
        });
}

//---------------------------------------------------------------------------------

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

//---------------------------------------------------------------------------------

function ProgramaServlet(opcion) {
    const contenedor = document.getElementById('contenido-dinamico');
    
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

function agregarCursoAPrograma(){
    const programa = document.getElementById('selectPrograma').value;
    const curso = document.getElementById('selectCurso').value;
        
    if (!programa.trim()){
        alert("Ingrese un programa valido");
        return;
    }

    if (!curso.trim()){
        alert("Ingrese un curso valido");
        return;
    } 
    
    const params = new URLSearchParams();
    params.append('programaStr', programa);
    params.append('cursoStr', curso);
    
    fetch('ProgramaServlet?accion=agregarCurso', {
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
            ProgramaServlet("formAgregarCurso"); //Reinvocamos el GET
        })
        .catch(error => {
            alert(error.message);
        });
    
}

//---------------------------------------------------------------------------------










