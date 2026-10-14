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
    event.preventDefault(); 
    
    const formulario = document.getElementById('form-registro');
    const formData = new URLSearchParams(new FormData(formulario)); 

    fetch('MiControladorServlet', {
        method: 'POST',
        body: formData,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    .then(response => response.text())
    .then(htmlRespuesta => {
        document.getElementById('resultado-registro').innerHTML = htmlRespuesta;
        formulario.reset(); 
    })
    .catch(error => console.error('Error al guardar:', error));
}

//---------------------------------------------------------------------------------

//Filtrados y ordenamientos
function initBuscador() {
    const selectTipo = document.getElementById('filtroTipo');
    const selectOrden = document.getElementById('criterioOrden');
    const tablaResultados = document.getElementById('tablaResultados');

    if (!tablaResultados || !selectTipo || !selectOrden) return;

    const tbody = tablaResultados.querySelector('tbody');

    function actualizarResultados() {
        const tipoSeleccionado = selectTipo.value;
        const criterioOrden = selectOrden.value;

        let filas = Array.from(tbody.querySelectorAll('tr.fila-resultado'));

        // 1. FILTRADO POR TIPO
        filas.forEach(fila => {
                    const tipoFila = fila.getAttribute('data-tipo').trim().toLocaleString();
                    
                    //Si se selecciona TODOS, entonces se muestra todo.
                    //Si se selecciona usuarios, se muestran los docentes y los estudiantes.
                    const coincideTipo = (tipoSeleccionado === 'TODOS') || 
                                         (tipoFila === tipoSeleccionado) || 
                                         (tipoSeleccionado === 'usuario' && (tipoFila === 'docente' || tipoFila === 'estudiante'));

                    if (coincideTipo) {
                        fila.style.display = '';
                    } else {
                        fila.style.display = 'none';
                    }
                });

        // 2. ORDENAMIENTO
        filas.sort((a, b) => {
            if (criterioOrden === 'ALFABETICO') {
                const nombreA = (a.getAttribute('data-nombre') || '').toLowerCase();
                const nombreB = (b.getAttribute('data-nombre') || '').toLowerCase();
                return nombreA.localeCompare(nombreB);
            } else if (criterioOrden === 'FECHA') {
                const fechaA = a.getAttribute('data-fecha') || '';
                const fechaB = b.getAttribute('data-fecha') || '';
                return fechaB.localeCompare(fechaA); // Descendente (más reciente primero)
            }
            return 0;
        });

        // 3. REORDENAR EN EL DOM
        filas.forEach(fila => tbody.appendChild(fila));
    }

    selectTipo.addEventListener('change', actualizarResultados);
    selectOrden.addEventListener('change', actualizarResultados);

    actualizarResultados();
}

function verDetallesBusqueda(nombre, tipo){     
    const tipoNormalizado = (tipo || '').toLowerCase();     
    
    //Si es un usuario, verificamos si es el usuario actual
    if (tipoNormalizado === 'docente' || tipoNormalizado === 'estudiante') {
        fetch('UsuarioServlet?accion=getLogueado')
            .then(response => {
                if (!response.ok) throw new Error('Error al obtener usuario logueado');
                return response.text();
            })
            .then(nicknameLogueado => {
                const nickLimpioLogueado = (nicknameLogueado || '').trim();
                
                //Si se selecciona al usuario logueado, se va a cargar el modificar y no a ver el perfil.
                if (nickLimpioLogueado !== '' && nombre.toLowerCase() === nickLimpioLogueado.toLowerCase()) {
                    cargarModificarUsuario();
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
    } else {
        //Si no se selecciono el usuario logueado, seguir curso normal
        switch (tipoNormalizado){
            case "programa":
                cargarDetallePrograma(nombre);
            break;

            case "curso":
                verDetallesCurso(nombre);
            break;

            case "docente":
            case "estudiante":
                verPerfilUsuario(nombre);
            break;

        }
    }
}

function BuscadorServlet(){
    const input = document.getElementById('input-busqueda');
    const contenedor = document.getElementById('contenido-dinamico');

    if (!input || !contenedor) return;

    const query = encodeURIComponent(input.value.trim());

    fetch(`BuscadorServlet?query=${query}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
            initBuscador();
        })
        .catch(error => console.error('Error al ejecutar la búsqueda:', error));
}

//---------------------------------------------------------------------------------

//Para evitar tener que crear un Servlet extra se procede generando un evento para cargar los Institutos y Categorias del menu izquierdo.
document.addEventListener("DOMContentLoaded", function() {
    cargarInstitutosMenu();
    cargarCategoriasMenu();
    cargarMenuLateral();
});

function cargarInstitutosMenu() {
    fetch('MiControladorServlet?accion=obtenerInstitutosJSON')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener institutos');
            return response.json();
        })
        .then(institutos => {
            const ul = document.getElementById('lista-institutos');
            if(ul) {
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
            }
        })
        .catch(error => {
            console.error('Error al cargar institutos:', error);
            const ul = document.getElementById('lista-institutos');
            if(ul) ul.innerHTML = '<li style="font-size: 12px; color: red;">Error al cargar</li>';
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
            if(ul) {
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
            }
        })
        .catch(error => {
            console.error('Error al cargar categorías:', error);
            const ul = document.getElementById('lista-categorias');
            if(ul) ul.innerHTML = '<li style="font-size: 12px; color: red;">Error al cargar</li>';
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
            
            if (opcion === 'formCrear') {
                inicializarEventosCrearPrograma();
            }
            
            if (opcion === 'formAgregarCurso'){
                inicializarEventosAgregarCursoAPrograma();
            }
            
            
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
    
    const datos = new FormData();
    datos.append('nombre', nombre);
    datos.append('desc', descripcion);
    datos.append('fechaInicio', fechaInicio);
    datos.append('fechaFin', fechaFin);
    
    const archivoImagen = document.getElementById("imagen").files[0];
    if (archivoImagen) {
        datos.append('imagen', archivoImagen);
    }
    
    fetch('ProgramaServlet?accion=crear', {
            method: 'POST',
            body: datos
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
            ProgramaServlet("formCrear"); 
        })
        .catch(error => {
            alert(error.message);
        });
    
}

function inicializarEventosCrearPrograma() {
    const inputImagen = document.getElementById("imagen");
    
    if (inputImagen) {
        inputImagen.addEventListener("change", function(event) {
            const archivo = event.target.files[0];
            if (archivo) {
                const lector = new FileReader();
                lector.onload = function(e) {
                    document.getElementById("vistaPrevia").src = e.target.result;
                };
                lector.readAsDataURL(archivo);
            }
        });
    }
}

function inicializarEventosAgregarCursoAPrograma() {
document.querySelectorAll('.btn-seleccionar-curso').forEach(button => {
        button.addEventListener('click', function() {
            const nombreCurso = this.getAttribute('data-nombre');
            
            // 1. Asignar al input oculto
            const inputCurso = document.getElementById('selectCurso');
            if (inputCurso) inputCurso.value = nombreCurso;

            // 2. Actualizar el label inferior
            const label = document.getElementById('labelCursoSeleccionado');
            if (label) label.innerText = nombreCurso;

            // 3. Resaltar la fila seleccionada
            document.querySelectorAll('tr[id^="fila-curso-"]').forEach(fila => {
                fila.classList.remove('table-active');
            });
            
            const filaSeleccionada = document.getElementById('fila-curso-' + nombreCurso);
            if (filaSeleccionada) {
                filaSeleccionada.classList.add('table-active');
            }
        });
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
            ProgramaServlet("formAgregarCurso"); 
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

// =====================================================================
// FUNCIONES DE CURSO Y EDICIÓN (LIMPIAS Y ÚNICAS)
// =====================================================================

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

    // Usar FormData puro para que el navegador genere el multipart/form-data
    const datos = new FormData(form);

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

function altaEdicion(event) {
    event.preventDefault();
    const divError = document.getElementById('mensaje-error-edicion');
    divError.style.display = 'none';

    const form = document.getElementById('formAltaEdicion');
    
    // Usar FormData puro para que el navegador genere el multipart/form-data
    const datos = new FormData(form);

    fetch('EdicionServlet?accion=altaEdicion', {
        method: 'POST',
        body: datos // NO AGREGAR HEADERS AQUÍ. El navegador lo hace solo.
    })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                alert(msg);
                form.reset();
                document.getElementById('cursoEdicion').innerHTML = '<option value="">Primero seleccione un instituto</option>';
                document.getElementById('cursoEdicion').disabled = true;
            });
        } else {
            return response.text().then(err => { throw new Error(err); });
        }
    })
    .catch(error => {
        divError.innerHTML = "<strong>Error:</strong> " + error.message;
        divError.style.display = 'block';
        divError.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
}

// =====================================================================

function cancelarOperacion() {
    const form = document.getElementById('formAltaCurso');
    if (form) {
        form.reset();
        document.getElementById('mensaje-error').style.display = 'none';
    }
}

function cargarAltaCurso() {
    fetch('CursoServlet?accion=altaCurso')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error de permisos o de servidor");
            }
            return response.text();
        })
        .then(htmlString => {
            document.getElementById('contenido-dinamico').innerHTML = htmlString;
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = 
                "<h3 style='color:red;'>No tienes permisos para ver esta sección.</h3>";
        });
}

function cargarAltaUsuario() {
    fetch('fragmentos/altaUsuario.jsp')
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            inicializarEventosUsuario(); 
        })
        .catch(error => console.error("Error al cargar la vista:", error));
}

function inicializarEventosUsuario() {
    fetch("UsuarioServlet?accion=listarInstitutos")
        .then(response => response.json())
        .then(institutos => {
            const selectInstituto = document.getElementById("instituto");
            if(selectInstituto) {
                institutos.forEach(instituto => {
                    const opcion = document.createElement("option");
                    opcion.value = instituto.nombre;
                    opcion.textContent = instituto.nombre;
                    selectInstituto.appendChild(opcion);
                });
            }
        }).catch(err => console.log("Error cargando institutos", err));

    const checkDocente = document.getElementById("docente");
    if(checkDocente) {
        checkDocente.addEventListener("change", function() {
            document.getElementById("seccionInstitutos").style.display = this.checked ? "block" : "none";
        });
    }

    const inputImagen = document.getElementById("imagen");
    if(inputImagen) {
        inputImagen.addEventListener("change", function(event) {
            const archivo = event.target.files[0];
            if (archivo) {
                const lector = new FileReader();
                lector.onload = function(e) {
                    document.getElementById("vistaPrevia").src = e.target.result;
                };
                lector.readAsDataURL(archivo);
            }
        });
    }

    const btnAgregarInst = document.getElementById("agregarInstituto");
    if(btnAgregarInst) {
        btnAgregarInst.addEventListener("click", function() {
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
    }

    const btnQuitarInst = document.getElementById("quitarInstituto");
    if(btnQuitarInst) {
        btnQuitarInst.addEventListener("click", function() {
            const selectSeleccionados = document.getElementById("institutosSeleccionados");
            if (selectSeleccionados.selectedIndex === -1) return alert("Seleccione un instituto para quitar.");
            selectSeleccionados.remove(selectSeleccionados.selectedIndex);
        });
    }

    const formUsuario = document.getElementById("formAltaUsuario");
    if(formUsuario) {
        formUsuario.addEventListener("submit", function(event) {
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
            if(selectSeleccionados) {
                for (let i = 0; i < selectSeleccionados.options.length; i++) {
                    datos.append("institutos", selectSeleccionados.options[i].value);
                }
            }

            const archivoImagen = document.getElementById("imagen").files[0];
            if (archivoImagen) datos.append("imagen", archivoImagen);

            fetch("UsuarioServlet?accion=crear", {
                method: "POST",
                body: datos
            })
            .then(response => response.text())
            .then(resultado => {
                alert(resultado); 
                document.getElementById('formAltaUsuario').reset();
                document.getElementById('vistaPrevia').src = 'imagenes/usr.png';
                document.getElementById('institutosSeleccionados').innerHTML = ''; 
            })
            .catch(error => alert("Error: " + error));
        });
    }
}

function cargarLogin() {
    fetch('fragmentos/login.jsp')
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            
            const formLogin = document.getElementById("formLogin");
            if(formLogin) {
                formLogin.addEventListener("submit", function(event) {
                    event.preventDefault();
                    
                    const datos = new URLSearchParams(new FormData(this));
                    
                    fetch('LoginServlet?accion=login', {
                        method: 'POST',
                        body: datos
                    })
                    .then(response => {
                        if (response.ok) {
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
            }
        });
}

function cerrarSesion() {
    fetch('LoginServlet?accion=logout', { method: 'POST' })
        .then(() => window.location.reload());
}

function cargarCursosPor(opcionBusqueda, nombreFiltro) {
    fetch(`CursoServlet?accion=buscarCurso&opcion=${opcionBusqueda}&nombre=${nombreFiltro}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => console.error("Error cargando lista de cursos:", error));
}

function verDetallesCurso(nombreCurso) {
    fetch(`CursoServlet?accion=verDetallesCurso&nombreCurso=${nombreCurso}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => console.error("Error cargando detalles del curso:", error));
}

function verDetalleEdicion(nombreEdicion) {
    alert("Próximamente: Redirigiendo a Consulta de Edición -> " + nombreEdicion);
}

function verDetallePrograma(nombrePrograma) {
    alert("Próximamente: Redirigiendo a Consulta de Programa -> " + nombrePrograma);
}

function cargarMenuLateral() {
    fetch('UsuarioServlet?accion=listarInstitutos')
        .then(response => response.json())
        .then(institutos => {
            const ulInstitutos = document.getElementById('lista-institutos');
            if(ulInstitutos) {
                ulInstitutos.innerHTML = ''; 

                institutos.forEach(inst => {
                    const li = document.createElement('li');
                    li.style.marginBottom = "5px";
                    li.innerHTML = `<a href="#" onclick="cargarCursosPor('instituto', '${inst.nombre}')">${inst.nombre}</a>`;
                    ulInstitutos.appendChild(li);
                });
            }
        })
        .catch(error => console.error("Error al cargar institutos:", error));

    fetch('MiControladorServlet?accion=obtenerCategoriasJSON')
        .then(response => response.json())
        .then(categorias => {
            const ulCategorias = document.getElementById('lista-categorias');
            if(ulCategorias) {
                ulCategorias.innerHTML = ''; 

                categorias.forEach(cat => {
                    const li = document.createElement('li');
                    li.style.marginBottom = "5px";
                    li.innerHTML = `<a href="#" onclick="cargarCursosPor('categoria', '${cat.nombre}')">${cat.nombre}</a>`;
                    ulCategorias.appendChild(li);
                });
            }
        })
        .catch(error => console.error("Error al cargar categorías:", error));
}

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

function cargarModificarUsuario() {
    const contenedor = document.getElementById('contenido-dinamico');

    fetch('fragmentos/ModificarUsuario.jsp')
        .then(response => {
            if (!response.ok) {
                throw new Error("No se pudo cargar la vista de modificación.");
            }
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
            if (typeof inicializarEventosModificarUsuario === "function") {
                inicializarEventosModificarUsuario();
            }
        })
        .catch(error => console.error("Error al cargar la vista de modificación:", error));
}

function inicializarEventosModificarUsuario() {
    const form = document.getElementById('formModificarUsuario');
    const inputImagen = document.getElementById('imagen');
    const vistaPrevia = document.getElementById('vistaPreviaModificar');
    const divError = document.getElementById('mensaje-error-modificar');

    if (!form) return;

    const selectInstituto = document.getElementById("institutoModificar");
    if (selectInstituto) {
        fetch("UsuarioServlet?accion=listarInstitutos")
            .then(response => response.json())
            .then(institutos => {
                institutos.forEach(inst => {
                    const opcion = document.createElement("option");
                    opcion.value = inst.nombre;
                    opcion.textContent = inst.nombre;
                    selectInstituto.appendChild(opcion);
                });
            })
            .catch(err => console.error("Error cargando institutos:", err));

        const btnAgregar = document.getElementById("agregarInstitutoModificar");
        if (btnAgregar) {
            btnAgregar.addEventListener("click", function () {
                const instSeleccionado = selectInstituto.value;
                const selectLista = document.getElementById("institutosSeleccionadosModificar");

                if (!instSeleccionado) return alert("Seleccione un instituto de la lista.");

                for (let i = 0; i < selectLista.options.length; i++) {
                    if (selectLista.options[i].value === instSeleccionado) {
                        return alert("El instituto ya fue agregado.");
                    }
                }

                const opcion = document.createElement("option");
                opcion.value = instSeleccionado;
                opcion.textContent = instSeleccionado;
                selectLista.appendChild(opcion);
            });
        }

        const btnQuitar = document.getElementById("quitarInstitutoModificar");
        if (btnQuitar) {
            btnQuitar.addEventListener("click", function () {
                const selectLista = document.getElementById("institutosSeleccionadosModificar");
                if (selectLista.selectedIndex === -1) return alert("Seleccione un instituto para quitar.");
                selectLista.remove(selectLista.selectedIndex);
            });
        }
    }

    if (inputImagen && vistaPrevia) {
        inputImagen.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (evt) {
                    vistaPrevia.src = evt.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        if (divError) {
            divError.style.display = 'none';
            divError.innerText = '';
        }

        const pass = document.getElementById('password') ? document.getElementById('password').value : '';
        const confirmPass = document.getElementById('confirmarPassword') ? document.getElementById('confirmarPassword').value : '';

        if (pass.length > 0 || confirmPass.length > 0) {
            if (pass !== confirmPass) {
                if (divError) {
                    divError.innerText = "Las contraseñas no coinciden. Por favor verifique.";
                    divError.style.display = 'block';
                } else {
                    alert("Las contraseñas no coinciden.");
                }
                return;
            }
        }

        const selectLista = document.getElementById("institutosSeleccionadosModificar");
        if (selectLista && selectLista.options.length === 0) {
            if (divError) {
                divError.innerText = "Un docente debe tener al menos un instituto asignado.";
                divError.style.display = 'block';
            } else {
                alert("Un docente debe tener al menos un instituto asignado.");
            }
            return;
        }

        const formData = new FormData(form);
        formData.append('accion', 'modificarUsuario');

        if (selectLista) {
            for (let i = 0; i < selectLista.options.length; i++) {
                formData.append("institutos", selectLista.options[i].value);
            }
        }

        fetch('UsuarioServlet', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.text();
        })
        .then(mensajeExito => {
            alert("Cambios guardados correctamente.");
            window.location.reload();
        })
        .catch(error => {
            if (divError) {
                divError.innerText = error.message || "Error al actualizar el perfil.";
                divError.style.display = 'block';
            } else {
                alert("Error: " + error.message);
            }
        });
    });
}

function cargarConsultaEdicion(nombreEdicion) {
    document.getElementById('contenido-dinamico').innerHTML = "<h3 style='text-align:center;'>Cargando edición...</h3>";
    
    fetch(`EdicionServlet?accion=consultaEdicion&nombre=${encodeURIComponent(nombreEdicion)}`)
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la edición");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style="color:red; text-align:center;">${error.message}</h3>`;
        });
}

function cargarFormInscripcion() {
    fetch('InscripcionServlet?accion=formulario')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Esta función es solo para Estudiantes.");
            return response.text();
        })
        .then(html => document.getElementById('contenido-dinamico').innerHTML = html)
        .catch(error => document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red; text-align:center; padding: 20px;'>${error.message}</h3>`);
}

function cargarCursosParaInscripcion(nombreInstituto) {
    const selectCurso = document.getElementById('cursoInscripcion');
    const selectEdicion = document.getElementById('edicionInscripcion');
    
    selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
    selectEdicion.disabled = true;

    if (!nombreInstituto) {
        selectCurso.innerHTML = '<option value="">Primero seleccione un instituto</option>';
        selectCurso.disabled = true;
        return;
    }

    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${nombreInstituto}`)
        .then(response => response.json())
        .then(cursos => {
            selectCurso.innerHTML = '<option value="">Seleccione un Curso...</option>';
            cursos.forEach(c => selectCurso.innerHTML += `<option value="${c}">${c}</option>`);
            selectCurso.disabled = false;
        });
}

function cargarEdicionesParaInscripcion(nombreCurso) {
    const selectEdicion = document.getElementById('edicionInscripcion');
    
    if (!nombreCurso) {
        selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
        selectEdicion.disabled = true;
        return;
    }

    fetch(`InscripcionServlet?accion=listarEdiciones&curso=${nombreCurso}`)
        .then(response => response.json())
        .then(ediciones => {
            if(ediciones.length === 0) {
                selectEdicion.innerHTML = '<option value="">No hay ediciones disponibles</option>';
                selectEdicion.disabled = true;
            } else {
                selectEdicion.innerHTML = '<option value="">Seleccione la Edición...</option>';
                ediciones.forEach(e => selectEdicion.innerHTML += `<option value="${e}">${e}</option>`);
                selectEdicion.disabled = false;
            }
        });
}

function inscribirEdicion(event) {
    event.preventDefault();
    const msgDiv = document.getElementById('mensaje-inscripcion');
    const form = document.getElementById('formInscripcionEdicion');
    const datos = new URLSearchParams(new FormData(form));

    fetch('InscripcionServlet?accion=inscribir', {
        method: 'POST',
        body: datos
    })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                msgDiv.style.display = 'block';
                msgDiv.style.backgroundColor = '#dff0d8';
                msgDiv.style.color = '#3c763d';
                msgDiv.style.border = '1px solid #d6e9c6';
                msgDiv.innerHTML = "✔ " + msg;
                
                form.reset();
                document.getElementById('cursoInscripcion').disabled = true;
                document.getElementById('edicionInscripcion').disabled = true;
            });
        } else {
            return response.text().then(err => {
                msgDiv.style.display = 'block';
                msgDiv.style.backgroundColor = '#f2dede';
                msgDiv.style.color = '#a94442';
                msgDiv.style.border = '1px solid #ebccd1';
                msgDiv.innerHTML = "<strong>Atención:</strong> " + err;
            });
        }
    })
    .catch(error => console.error('Error procesando inscripción:', error));
}

// =====================================================================
// FUNCIONALIDAD DE RED SOCIAL Y VER PERFIL
// =====================================================================

function cargarSocial() {
    fetch('UsuarioServlet?accion=cargarSocial')
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = error.message;
        });
}

function seguirRapido(nickname) {
    const params = new URLSearchParams();
    params.append('accion', 'seguir');
    params.append('nicknameASeguir', nickname);

    ejecutarAccionSocial(params);
}

function dejarDeSeguirRapido(nickname) {
    const params = new URLSearchParams();
    params.append('accion', 'dejarSeguir');
    params.append('nicknameSeleccionado', nickname);

    ejecutarAccionSocial(params);
}

function ejecutarAccionSocial(params) {
    fetch('UsuarioServlet', {
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
        cargarSocial(); // Recarga la lista para actualizar los botones de estado
    })
    .catch(error => {
        alert(error.message);
    });
}

function verPerfilUsuario(nickname) {
    const contenedor = document.getElementById('detalle-perfil-usuario');
    if (contenedor) {
        contenedor.innerHTML = '<h4 style="text-align:center; color:#3498db;">Cargando perfil...</h4>';
        
        fetch(`UsuarioServlet?accion=verPerfil&nickname=${encodeURIComponent(nickname)}`)
            .then(response => {
                if (!response.ok) throw new Error("Error al cargar el perfil");
                return response.text();
            })
            .then(html => {
                contenedor.innerHTML = html;
            })
            .catch(error => {
                contenedor.innerHTML = `<p style="color:red; text-align:center;">${error.message}</p>`;
            });
    }
}

function filtrarUsuariosRedSocial() {
    const input = document.getElementById('filtroNickname');
    if (!input) return;
    
    const filtro = input.value.toLowerCase().trim();
    const filas = document.querySelectorAll('.fila-usuario-social');

    filas.forEach(fila => {
        const nickname = fila.getAttribute('data-nickname');
        if (nickname.includes(filtro)) {
            fila.style.display = '';
        } else {
            fila.style.display = 'none';
        }
    });
}

// =====================================================================
// CASO DE USO: SELECCIONAR ESTUDIANTES A EDICIÓN (DOCENTE)
// =====================================================================

function cargarFormSeleccionEstudiantes() {
    fetch('InscripcionServlet?accion=formularioSeleccion')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Función exclusiva para Docentes.");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red; text-align:center;'>${error.message}</h3>`;
        });
}

function cargarCursosParaSeleccion(nombreInstituto) {
    const selectCurso = document.getElementById('cursoSeleccion');
    const selectEdicion = document.getElementById('edicionSeleccion');
    const tablaContenedor = document.getElementById('contenedor-tabla-inscripciones');
    const btnGuardar = document.getElementById('btnGuardarSeleccion');
    
    selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
    selectEdicion.disabled = true;
    tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione Instituto, Curso y Edición para ver las inscripciones.</p>';
    if(btnGuardar) btnGuardar.disabled = true;

    if (!nombreInstituto) {
        selectCurso.innerHTML = '<option value="">Primero seleccione un instituto</option>';
        selectCurso.disabled = true;
        return;
    }

    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${nombreInstituto}`)
        .then(response => response.json())
        .then(cursos => {
            selectCurso.innerHTML = '<option value="">Seleccione un Curso...</option>';
            cursos.forEach(c => selectCurso.innerHTML += `<option value="${c}">${c}</option>`);
            selectCurso.disabled = false;
        });
}

function cargarEdicionesParaSeleccion(nombreCurso) {
    const selectEdicion = document.getElementById('edicionSeleccion');
    const tablaContenedor = document.getElementById('contenedor-tabla-inscripciones');
    const btnGuardar = document.getElementById('btnGuardarSeleccion');
    
    tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione Instituto, Curso y Edición para ver las inscripciones.</p>';
    if(btnGuardar) btnGuardar.disabled = true;

    if (!nombreCurso) {
        selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
        selectEdicion.disabled = true;
        return;
    }

    fetch(`InscripcionServlet?accion=listarEdiciones&curso=${nombreCurso}`)
        .then(response => response.json())
        .then(ediciones => {
            if(ediciones.length === 0) {
                selectEdicion.innerHTML = '<option value="">No hay ediciones disponibles</option>';
                selectEdicion.disabled = true;
            } else {
                selectEdicion.innerHTML = '<option value="">Seleccione la Edición...</option>';
                ediciones.forEach(e => selectEdicion.innerHTML += `<option value="${e}">${e}</option>`);
                selectEdicion.disabled = false;
            }
        });
}

function cargarTablaInscripciones(nombreEdicion) {
    const tablaContenedor = document.getElementById('contenedor-tabla-inscripciones');
    const btnGuardar = document.getElementById('btnGuardarSeleccion');
    const inputOculto = document.getElementById('edicionOculta');
    
    if (!nombreEdicion) {
        tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione Instituto, Curso y Edición para ver las inscripciones.</p>';
        if(btnGuardar) btnGuardar.disabled = true;
        return;
    }

    inputOculto.value = nombreEdicion;
    tablaContenedor.innerHTML = '<h4 style="text-align:center; color: #3498db;">Cargando inscripciones...</h4>';

    fetch(`InscripcionServlet?accion=listarInscripciones&edicion=${encodeURIComponent(nombreEdicion)}`)
        .then(response => response.text())
        .then(html => {
            tablaContenedor.innerHTML = html;
            
            if (document.getElementById('cuerpoTablaInscripciones')) {
                btnGuardar.disabled = false;
            } else {
                btnGuardar.disabled = true;
            }
        })
        .catch(error => {
            tablaContenedor.innerHTML = `<p style="color:red; text-align:center;">Error al cargar la tabla.</p>`;
        });
}

function ordenarInscripciones(criterio) {
    const tbody = document.getElementById('cuerpoTablaInscripciones');
    if (!tbody) return;

    const filas = Array.from(tbody.querySelectorAll('tr.fila-inscripcion'));

    filas.sort((a, b) => {
        if (criterio === 'prioridad') {
            const prioA = parseFloat(a.getAttribute('data-prioridad')) || 0;
            const prioB = parseFloat(b.getAttribute('data-prioridad')) || 0;
            return prioB - prioA;
        } else {
            const fechaA = a.getAttribute('data-fecha') || '';
            const fechaB = b.getAttribute('data-fecha') || '';
            return fechaA.localeCompare(fechaB);
        }
    });

    filas.forEach(fila => tbody.appendChild(fila));
}

function guardarSeleccionEstudiantes(event) {
    event.preventDefault();
    const msgDiv = document.getElementById('mensaje-seleccion');
    const form = document.getElementById('formSeleccionEstudiantes');
    const btnGuardar = document.getElementById('btnGuardarSeleccion');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    btnGuardar.disabled = true;
    btnGuardar.innerText = 'Guardando...';
    msgDiv.style.display = 'none';

    const datos = new URLSearchParams(new FormData(form));
    datos.append('accion', 'guardarSeleccion');

    fetch('InscripcionServlet', {
        method: 'POST',
        body: datos
    })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                msgDiv.style.display = 'block';
                msgDiv.style.backgroundColor = '#dff0d8';
                msgDiv.style.color = '#3c763d';
                msgDiv.style.border = '1px solid #d6e9c6';
                msgDiv.innerHTML = "✔ " + msg;
                
                const edicionActual = document.getElementById('edicionOculta').value;
                setTimeout(() => cargarTablaInscripciones(edicionActual), 1500);
            });
        } else {
            return response.text().then(err => { throw new Error(err); });
        }
    })
    .catch(error => {
        msgDiv.style.display = 'block';
        msgDiv.style.backgroundColor = '#f2dede';
        msgDiv.style.color = '#a94442';
        msgDiv.style.border = '1px solid #ebccd1';
        msgDiv.innerHTML = "<strong>Error:</strong> " + error.message;
    })
    .finally(() => {
        btnGuardar.disabled = false;
        btnGuardar.innerText = 'Guardar Selección';
    });
}

//---------------------------------------------------------
//------- Consulta Usuario --------------------------------
//---------------------------------------------------------

function cargarConsultaUsuario() {
    const contenedor = document.getElementById('contenido-dinamico');
    contenedor.innerHTML = '<h3 style="text-align:center; color:#3498db;">Cargando usuarios...</h3>';

    fetch('UsuarioServlet?accion=consultaUsuario')
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la consulta de usuario.");
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
        })
        .catch(error => {
            contenedor.innerHTML = `<p style="color:red; text-align:center;">${error.message}</p>`;
        });
}

function cargarDetalleConsultaUsuario(nickname) {
    const contenedor = document.getElementById('contenedor-detalle-consulta-usuario');
    if (!contenedor) return;

    contenedor.innerHTML = '<h4 style="text-align:center; color:#3498db;">Cargando detalles del usuario...</h4>';

    fetch(`UsuarioServlet?accion=detalleConsultaUsuario&nickname=${encodeURIComponent(nickname)}`)
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la información del usuario.");
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
        })
        .catch(error => {
            contenedor.innerHTML = `<p style="color:red; text-align:center;">${error.message}</p>`;
        });
}

function cambiarPestana(idPestana) {
    const contenidos = document.querySelectorAll('.contenido-tab');
    contenidos.forEach(div => div.style.display = 'none');

    const botones = document.querySelectorAll('.tab-btn');
    botones.forEach(btn => {
        btn.style.background = '#e0e0e0';
        btn.style.color = '#333';
    });

    const tabSeleccionado = document.getElementById(idPestana);
    if (tabSeleccionado) tabSeleccionado.style.display = 'block';

    const btnSeleccionado = document.getElementById(`btn-${idPestana}`);
    if (btnSeleccionado) {
        btnSeleccionado.style.background = '#3498db';
        btnSeleccionado.style.color = 'white';
    }
}