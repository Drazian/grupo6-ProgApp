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

function cargarProgramas() {
    fetch('ProgramaServlet?accion=formVerProgramas')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener la lista de programas');
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('contenido-dinamico').innerHTML = 
                '<div class="alerta-error">Ocurrió un error al cargar los programas de formación.</div>';
        });
}

function cargarDetallePrograma(nombrePrograma) {
    fetch(`ProgramaServlet?accion=detallePrograma&nombre=${encodeURIComponent(nombrePrograma)}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar el detalle del programa');
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('contenido-dinamico').innerHTML = 
                '<div class="alerta-error">Ocurrió un error al cargar el detalle del programa.</div>';
        });
}

// Función para interceptar y enviar el formulario de Alta de Curso
function altaCurso(event) {
    event.preventDefault();

    const divError = document.getElementById('mensaje-error');
    divError.style.display = 'none';
    divError.innerHTML = '';

    const form = document.getElementById('formAltaCurso');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return; 
    }

    // Convertir el FormData a URLSearchParams para que Java extraiga los números y arrays correctamente
    const datos = new URLSearchParams(new FormData(form));

    fetch('CursoServlet?accion=altaCurso', {
        method: 'POST',
        body: datos
    })
    .then(response => {
        if (response.ok) {
            return response.text().then(mensajeExito => {
                alert(mensajeExito); 
                form.reset(); 
            });
        } else {
            return response.text().then(mensajeError => {
                throw new Error(mensajeError);
            });
        }
    })
    .catch(error => {
        divError.innerHTML = "<strong>Error:</strong> " + error.message;
        divError.style.display = 'block';
        divError.scrollIntoView({ behavior: 'smooth', block: 'end' });
    });
}

// Función auxiliar para el botón Cancelar del formulario
function cancelarOperacion() {
    const form = document.getElementById('formAltaCurso');
    if (form) {
        form.reset();
        document.getElementById('mensaje-error').style.display = 'none';
        
        // Opcional: Aquí podrías ocultar el contenedor central si así lo deseas
        // document.getElementById('contenedor-central').innerHTML = '';
    }
}

// Función para pedirle el fragmento al Servlet e inyectarlo en el HTML
function cargarAltaCurso() {
    fetch('CursoServlet?accion=altaCurso')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error de permisos o de servidor");
            }
            return response.text();
        })
        .then(htmlString => {
            // Inyectamos el formulario dentro del sector 3
            document.getElementById('contenido-dinamico').innerHTML = htmlString;
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = 
                "<h3 style='color:red;'>No tienes permisos para ver esta sección.</h3>";
        });
}

// 1. Función para cargar la vista
function cargarAltaUsuario() {
    fetch('fragmentos/altaUsuario.jsp')
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            inicializarEventosUsuario(); // Encendemos la lógica de tu formulario
        })
        .catch(error => console.error("Error al cargar la vista:", error));
}

// 2. Todos tus scripts originales empaquetados para ejecutarse tras inyectar el HTML
function inicializarEventosUsuario() {
    
    // Cargar la lista de institutos (Ruta corregida)
    fetch("UsuarioServlet?accion=listarInstitutos")
        .then(response => response.json())
        .then(institutos => {
            const selectInstituto = document.getElementById("instituto");
            institutos.forEach(instituto => {
                const opcion = document.createElement("option");
                opcion.value = instituto.nombre;
                opcion.textContent = instituto.nombre;
                selectInstituto.appendChild(opcion);
            });
        }).catch(err => console.log("Error cargando institutos", err));

    // Mostrar/Ocultar Institutos
    document.getElementById("docente").addEventListener("change", function() {
        document.getElementById("seccionInstitutos").style.display = this.checked ? "block" : "none";
    });

    // Vista previa de la imagen
    document.getElementById("imagen").addEventListener("change", function(event) {
        const archivo = event.target.files[0];
        if (archivo) {
            const lector = new FileReader();
            lector.onload = function(e) {
                document.getElementById("vistaPrevia").src = e.target.result;
            };
            lector.readAsDataURL(archivo);
        }
    });

    // Botón Agregar Instituto
    document.getElementById("agregarInstituto").addEventListener("click", function() {
        const selectInstituto = document.getElementById("instituto");
        const selectSeleccionados = document.getElementById("institutosSeleccionados");
        const institutoSeleccionado = selectInstituto.value;

        if (institutoSeleccionado === "") return alert("Seleccione un instituto.");

        for (let i = 0; i < selectSeleccionados.options.length; i++) {
            if (selectSeleccionados.options[i].value === institutoSeleccionado) {
                return alert("El instituto ya fue agregado.");
            }
        }
        const opcion = document.createElement("option");
        opcion.value = institutoSeleccionado;
        opcion.textContent = institutoSeleccionado;
        selectSeleccionados.appendChild(opcion);
    });

    // Botón Quitar Instituto
    document.getElementById("quitarInstituto").addEventListener("click", function() {
        const selectSeleccionados = document.getElementById("institutosSeleccionados");
        if (selectSeleccionados.selectedIndex === -1) return alert("Seleccione un instituto para quitar.");
        selectSeleccionados.remove(selectSeleccionados.selectedIndex);
    });

    // Enviar el Formulario
    document.getElementById("formAltaUsuario").addEventListener("submit", function(event) {
        event.preventDefault();

        const nickname = document.getElementById("nickname").value.trim();
        const nombre = document.getElementById("nombre").value.trim();
        const apellido = document.getElementById("apellido").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const confirmarPassword = document.getElementById("confirmarPassword").value;
        const fechaNacimiento = document.getElementById("fechaNacimiento").value;
        const docente = document.getElementById("docente").checked;

        if (password !== confirmarPassword) return alert("Las contraseñas no coinciden.");
        if (docente && document.getElementById("institutosSeleccionados").options.length === 0) {
            return alert("Un docente debe tener al menos un instituto asignado.");
        }

        const datos = new FormData();
        datos.append("nickname", nickname);
        datos.append("nombre", nombre);
        datos.append("apellido", apellido);
        datos.append("email", email);
        datos.append("password", password);
        datos.append("fechaNacimiento", fechaNacimiento);
        datos.append("tipoUsuario", docente ? "DOCENTE" : "ESTUDIANTE");

        const selectSeleccionados = document.getElementById("institutosSeleccionados");
        for (let i = 0; i < selectSeleccionados.options.length; i++) {
            datos.append("institutos", selectSeleccionados.options[i].value);
        }

        const archivoImagen = document.getElementById("imagen").files[0];
        if (archivoImagen) datos.append("imagen", archivoImagen);

        // Ruta corregida
        fetch("UsuarioServlet?accion=crear", {
            method: "POST",
            body: datos
        })
        .then(response => response.text())
        .then(resultado => {
            alert(resultado); // Muestra el mensaje del Servlet
            document.getElementById('formAltaUsuario').reset();
            document.getElementById('vistaPrevia').src = 'imagenes/usr.png';
            document.getElementById('institutosSeleccionados').innerHTML = ''; // Limpia la lista
        })
        .catch(error => alert("Error: " + error));
    });
}

// Carga el formulario HTML
// Carga el formulario HTML
function cargarLogin() {
    fetch('fragmentos/login.jsp')
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            
            // Asignamos el evento Submit al formulario recién inyectado
            document.getElementById("formLogin").addEventListener("submit", function(event) {
                event.preventDefault();
                
                // LA SOLUCIÓN: URLSearchParams convierte los datos al formato de texto estándar que Java espera
                const datos = new URLSearchParams(new FormData(this));
                
                fetch('LoginServlet?accion=login', {
                    method: 'POST',
                    body: datos
                })
                .then(response => {
                    if (response.ok) {
                        // Si el login es correcto, recargamos la página para actualizar el Menú y el Header
                        window.location.reload(); 
                    } else {
                        return response.text().then(err => { throw new Error(err); });
                    }
                })
                .catch(error => {
                    const divError = document.getElementById("mensaje-error-login");
                    divError.innerHTML = error.message;
                    divError.style.display = "block";
                });
            });
        });
}
// Cierra la sesión y recarga la página
function cerrarSesion() {
    fetch('LoginServlet?accion=logout', { method: 'POST' })
        .then(() => window.location.reload());
}

// 1. Llama al Servlet para buscar cursos filtrados y pinta la lista
function cargarCursosPor(opcionBusqueda, nombreFiltro) {
    // Ejemplo: CursoServlet?accion=buscarCurso&opcion=instituto&nombre=CURE
    fetch(`CursoServlet?accion=buscarCurso&opcion=${opcionBusqueda}&nombre=${nombreFiltro}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => console.error("Error cargando lista de cursos:", error));
}

// 2. Llama al Servlet para buscar un curso específico y pinta sus detalles
function verDetallesCurso(nombreCurso) {
    // Ejemplo: CursoServlet?accion=verDetallesCurso&nombreCurso=Programacion Avanzada
    fetch(`CursoServlet?accion=verDetallesCurso&nombreCurso=${nombreCurso}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => console.error("Error cargando detalles del curso:", error));
}

// 3. Preparativos para los siguientes Casos de Uso (Consulta Edicion / Consulta Programa)
function verDetalleEdicion(nombreEdicion) {
    alert("Próximamente: Redirigiendo a Consulta de Edición -> " + nombreEdicion);
    // Aquí luego haremos el fetch a EdicionServlet
}

function verDetallePrograma(nombrePrograma) {
    alert("Próximamente: Redirigiendo a Consulta de Programa -> " + nombrePrograma);
    // Aquí luego haremos el fetch a ProgramaServlet
}

// Se ejecuta automáticamente cuando la página index.jsp termina de cargar
document.addEventListener("DOMContentLoaded", function() {
    cargarMenuLateral();
});

function cargarMenuLateral() {
    // 1. Cargar "n" Institutos dinámicamente
    // Usamos el mismo endpoint que ya tenías funcionando en tu alta de usuario
    fetch('UsuarioServlet?accion=listarInstitutos')
        .then(response => response.json())
        .then(institutos => {
            const ulInstitutos = document.getElementById('lista-institutos');
            ulInstitutos.innerHTML = ''; // Limpiamos el "Cargando..."

            institutos.forEach(inst => {
                const li = document.createElement('li');
                li.style.marginBottom = "5px";
                // Aquí enlazamos el clic con la función del Caso de Uso
                li.innerHTML = `<a href="#" onclick="cargarCursosPor('instituto', '${inst.nombre}')">${inst.nombre}</a>`;
                ulInstitutos.appendChild(li);
            });
        })
        .catch(error => console.error("Error al cargar institutos:", error));

    // 2. Cargar "n" Categorías dinámicamente
    fetch('CursoServlet?accion=listarCategoriasJson')
        .then(response => response.json())
        .then(categorias => {
            const ulCategorias = document.getElementById('lista-categorias');
            ulCategorias.innerHTML = ''; // Limpiamos el "Cargando..."

            categorias.forEach(cat => {
                const li = document.createElement('li');
                li.style.marginBottom = "5px";
                // Enlazamos el clic con la función del Caso de Uso
                li.innerHTML = `<a href="#" onclick="cargarCursosPor('categoria', '${cat.nombre}')">${cat.nombre}</a>`;
                ulCategorias.appendChild(li);
            });
        })
        .catch(error => console.error("Error al cargar categorías:", error));
}

// 1. Función para pintar la vista de Alta de Edición (Deberás llamarla desde el menú lateral)
function cargarAltaEdicion() {
    fetch('EdicionServlet?accion=altaEdicion')
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error("Acceso denegado: " + response.status); });
            }
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style="color:red; text-align:center;">${error.message}. ¿Iniciaste sesión como Docente?</h3>`;
        });
}

// 2. Función dinámica para cargar cursos al cambiar el <select> de Institutos
function cargarCursosDeInstituto(nombreInstituto) {
    const selectCurso = document.getElementById('cursoEdicion');
    
    if (!nombreInstituto) {
        selectCurso.innerHTML = '<option value="">Primero seleccione un instituto</option>';
        selectCurso.disabled = true;
        return;
    }

    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${nombreInstituto}`)
        .then(response => response.json())
        .then(cursos => {
            selectCurso.innerHTML = '<option value="">Seleccione un Curso...</option>';
            cursos.forEach(curso => {
                selectCurso.innerHTML += `<option value="${curso}">${curso}</option>`;
            });
            selectCurso.disabled = false;
        })
        .catch(error => console.error("Error cargando cursos del instituto:", error));
}

// 3. Función para interceptar el formulario y enviarlo por POST
function altaEdicion(event) {
    event.preventDefault();
    const divError = document.getElementById('mensaje-error-edicion');
    divError.style.display = 'none';

    const form = document.getElementById('formAltaEdicion');
    
    // Convertimos los datos para soportar listas múltiples (select multiple)
    const datos = new URLSearchParams(new FormData(form));

    fetch('EdicionServlet?accion=altaEdicion', {
        method: 'POST',
        body: datos
    })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                alert(msg); // Muestra éxito
                form.reset(); // Limpia la pantalla para otra edición
                document.getElementById('cursoEdicion').innerHTML = '<option value="">Primero seleccione un instituto</option>';
                document.getElementById('cursoEdicion').disabled = true;
            });
        } else {
            return response.text().then(err => { throw new Error(err); });
        }
    })
    .catch(error => {
        // En caso de nombre duplicado, el usuario verá el error aquí y podrá corregirlo
        divError.innerHTML = "<strong>Error:</strong> " + error.message;
        divError.style.display = 'block';
        divError.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
}
//---------------------------------------------------------------------------------