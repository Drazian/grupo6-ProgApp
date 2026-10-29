// =====================================================================
// NAVEGABILIDAD SPA: FLECHA ATRÁS Y GUARDADO DE HISTORIAL (ROBUSTO Y CIEGO A FANTASMAS)
// =====================================================================
// =====================================================================
// SISTEMA GLOBAL DE NOTIFICACIONES (Intercepta y embellece los 'alert')
// =====================================================================
window.alert = function(mensaje) {
    // 1. Crear el contenedor global flotante si no existe
    let contenedor = document.getElementById('toast-container-global');
    if (!contenedor) {
        contenedor = document.createElement('div');
        contenedor.id = 'toast-container-global';
        // Lo anclamos en la esquina inferior derecha
        contenedor.style.cssText = 'position: fixed; bottom: 20px; right: 20px; z-index: 9999; display: flex; flex-direction: column; gap: 10px; pointer-events: none;';
        document.body.appendChild(contenedor);
    }

    // 2. Inteligencia simple: Detectar si es Error o Éxito leyendo el texto
    const texto = String(mensaje).toLowerCase();
    const esError = texto.includes('error') || texto.includes('fallo') || texto.includes('denegado') || texto.includes('atención') || texto.includes('no coinciden');
    
    const bgColor = esError ? '#e74c3c' : '#2ecc71'; // Rojo carmesí para error, Verde esmeralda para éxito
    const icono = esError ? '✖' : '✔';

    // 3. Construir la tarjeta emergente (Toast)
    const toast = document.createElement('div');
    toast.style.cssText = `
        background-color: ${bgColor}; 
        color: white; 
        padding: 15px 20px; 
        border-radius: 6px; 
        box-shadow: 0 4px 12px rgba(0,0,0,0.15); 
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
        font-size: 14px; 
        font-weight: 500;
        display: flex; 
        align-items: center; 
        gap: 12px; 
        opacity: 0; 
        transform: translateY(20px); 
        transition: all 0.3s ease;
        max-width: 350px;
        line-height: 1.4;
    `;
    const spanIcono = document.createElement('span');
    spanIcono.style.cssText = 'font-size: 18px; font-weight: bold;';
    spanIcono.textContent = icono;
    const spanMsg = document.createElement('span');
    spanMsg.textContent = String(mensaje);
    toast.appendChild(spanIcono);
    toast.appendChild(spanMsg);

    contenedor.appendChild(toast);

    // 4. Animación suave de entrada
    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    // 5. Autodestrucción a los 4 segundos
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(() => toast.remove(), 300); // Espera a que termine la animación para borrarlo del DOM
    }, 4000);
};

window.isNavigating = false; 

function guardarEstadoActualSPA() {
    const contenedor = document.getElementById('contenido-dinamico');
    if (contenedor && !window.isNavigating) {
        history.replaceState({ html: contenedor.innerHTML }, document.title);
    }
}

function crearNuevoEstadoSPA() {
     const contenedor = document.getElementById('contenido-dinamico');
     if (contenedor) {
         history.pushState({ html: contenedor.innerHTML }, document.title);
     }
}

(function inicializarHistorialSPA() {
    const contenedor = document.getElementById('contenido-dinamico');
    if (!contenedor) return;

    document.addEventListener('click', function(e) {
        const link = e.target.closest('a[href="#"]');
        if (link) e.preventDefault(); 
    });

    guardarEstadoActualSPA();

    window.addEventListener("popstate", (evento) => {
        if (evento.state && evento.state.html) {
            window.isNavigating = true; 
            contenedor.innerHTML = evento.state.html;         
            contenedor.querySelectorAll('button').forEach(btn => {
                btn.disabled = false;
            });
            if (document.getElementById('lista-mis-seguidos-social')) {
                let perfilNick = null;
                const pTag = document.querySelector('#detalle-perfil-usuario p');
                if (pTag && pTag.innerText.includes('/')) {
                    perfilNick = pTag.innerText.split('/')[0].trim();
                }
                
                fetch('UsuarioServlet?accion=cargarSocial')
                    .then(res => res.text())
                    .then(html => {
                        contenedor.innerHTML = html;
                        
                        // Reactivamos botones también en la nueva carga por si acaso
                        contenedor.querySelectorAll('button').forEach(btn => {
                            btn.disabled = false;
                        });

                        if (perfilNick && perfilNick !== 'Seleccione') {
                            fetch(`UsuarioServlet?accion=detalleConsultaUsuario&nickname=${encodeURIComponent(perfilNick)}`)
                                .then(r => r.text())
                                .then(profileHtml => {
                                    const contDetalle = document.getElementById('detalle-perfil-usuario');
                                    if (contDetalle) {
                                        const listaUsr = document.getElementById('contenedor-lista-usuarios');
                                        if (listaUsr) listaUsr.style.display = 'none';
                                        contDetalle.style.display = 'block';
                                        contDetalle.innerHTML = profileHtml;
                                        
                                        contDetalle.querySelectorAll('button').forEach(btn => {
                                            btn.disabled = false;
                                        });
                                    }
                                    guardarEstadoActualSPA();
                                });
                        } else {
                            guardarEstadoActualSPA();
                        }
                    })
                    .catch(err => {
                        // Si el servidor llega a fallar o da error 500, al menos los botones siguen vivos
                        console.warn("No se pudo sincronizar con el servidor al navegar:", err);
                    });
            }

            setTimeout(() => { window.isNavigating = false; }, 200);
        }
    });
})();

window.addEventListener("popstate", () => {
    // 1. Si estamos en la vista de Consulta de Curso y el select está vacío, lo rellenamos globalmente
    const selectInstCurso = document.getElementById('instBuscadorCurso');
    if (selectInstCurso && selectInstCurso.options.length <= 1) {
        if (typeof cargarInstitutosGenericos === 'function') {
            cargarInstitutosGenericos('instBuscadorCurso');
        }
    }

    // 2. Si estamos en la vista de Consulta de Edición y el select está vacío
    const selectInstEd = document.getElementById('instBuscadorEd');
    if (selectInstEd && selectInstEd.options.length <= 1) {
        if (typeof cargarInstitutosGenericos === 'function') {
            cargarInstitutosGenericos('instBuscadorEd');
        }
    }

    // 3. Si estamos en el menú lateral o general y faltan los institutos/categorías
    const listaInstMenu = document.getElementById('lista-institutos');
    if (listaInstMenu && listaInstMenu.children.length === 0) {
        if (typeof cargarInstitutosMenu === 'function') cargarInstitutosMenu();
        if (typeof cargarCategoriasMenu === 'function') cargarCategoriasMenu();
    }
});

// =====================================================================
// FUNCIONES GENERALES
// =====================================================================


function cargarSeccion(opcion) {
    const contenedor = document.getElementById('contenido-dinamico');
    fetch(`MiControladorServlet?accion=${opcion}`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error('Error al cargar la sección:', error));
}

function ejecutarBusqueda() {
    const query = document.getElementById('input-busqueda').value;
    const contenedor = document.getElementById('contenido-dinamico'); 

    fetch(`MiControladorServlet?accion=buscar&query=${encodeURIComponent(query)}`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error('Error en la búsqueda:', error));
}

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
        guardarEstadoActualSPA(); // FIX: Actualiza estado tras guardar
    })
    .catch(error => console.error('Error al guardar:', error));
}

//---------------------------------------------------------------------------------
// Filtrados y ordenamientos
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

        filas.forEach(fila => {
            const tipoFila = fila.getAttribute('data-tipo').trim().toLocaleString();
            const coincideTipo = (tipoSeleccionado === 'TODOS') || 
                                 (tipoFila === tipoSeleccionado) || 
                                 (tipoSeleccionado === 'usuario' && (tipoFila === 'docente' || tipoFila === 'estudiante'));

            if (coincideTipo) {
                fila.style.display = '';
            } else {
                fila.style.display = 'none';
            }
        });

        filas.sort((a, b) => {
            if (criterioOrden === 'ALFABETICO') {
                const nombreA = (a.getAttribute('data-nombre') || '').toLowerCase();
                const nombreB = (b.getAttribute('data-nombre') || '').toLowerCase();
                return nombreA.localeCompare(nombreB);
            } else if (criterioOrden === 'FECHA') {
                const fechaA = a.getAttribute('data-fecha') || '';
                const fechaB = b.getAttribute('data-fecha') || '';
                return fechaB.localeCompare(fechaA); 
            }
            return 0;
        });

        filas.forEach(fila => tbody.appendChild(fila));
    }

    selectTipo.addEventListener('change', actualizarResultados);
    selectOrden.addEventListener('change', actualizarResultados);
    actualizarResultados();
}

function verDetallesBusqueda(nombre, tipo){     
    const tipoNormalizado = (tipo || '').toLowerCase();
    switch (tipoNormalizado){     
        case "programa":
            cargarDetallePrograma(nombre);
        break;
        case "curso":
            verDetallesCurso(nombre);
        break;
        case "docente":
        case "estudiante":
            cargarConsultaUsuario(() => {
                seleccionarUsuario(nombre);
            });
        break;
    }
}

function BuscadorServlet(){
    const input = document.getElementById('input-busqueda');
    const contenedor = document.getElementById('contenido-dinamico');

    if (!input || !contenedor) return;

    const query = encodeURIComponent(input.value.trim());

    fetch(`BuscadorServlet?query=${query}`)
        .then(response => {
            if (!response.ok) throw new Error('Error en la respuesta del servidor');
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
            initBuscador();
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error('Error al ejecutar la búsqueda:', error));
}

//---------------------------------------------------------------------------------

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
            const ul = document.getElementById('lista-categorias');
            if(ul) ul.innerHTML = '<li style="font-size: 12px; color: red;">Error al cargar</li>';
        });
}

function buscarCurso(opcion, nombre) {
    fetch(`CursoServlet?accion=buscarCurso&opcion=${encodeURIComponent(opcion)}&nombre=${encodeURIComponent(nombre)}`)
        .then(response => {
            if (!response.ok) throw new Error('Error en la búsqueda de cursos');
            return response.text();
        })
        .then(htmlResultado => {
            document.getElementById('contenido-dinamico').innerHTML = htmlResultado;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = '<div class="alerta-error">Ocurrió un error al cargar los cursos.</div>';
        });
}

//---------------------------------------------------------------------------------

function testServlet(){
    const contenedor = document.getElementById('contenido-dinamico');
    fetch(`TestServlet`)
        .then(response => response.text())
        .then(html => {
            contenedor.innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
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
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: params
    })
    .then(response => {
        return response.text().then(texto => {
            if (!response.ok) throw new Error(texto);
            return texto;
        });
    })
    .then(mensajeExito => {
        alert(mensajeExito);
        testServlet(); 
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
            if (opcion === 'formCrear') inicializarEventosCrearPrograma();
            if (opcion === 'formAgregarCurso') inicializarEventosAgregarCursoAPrograma();
            
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error('Error al cargar la sección:', error));
}

function crearPrograma(){
    const nombre = document.getElementById("nombre").value;
    const descripcion = document.getElementById("desc").value;
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;
    
    if (!nombre.trim() || !descripcion.trim() || !fechaInicio.trim() || !fechaFin.trim()){
        alert("Verifique que todos los campos sean válidos");
        return;
    }
    
    const datos = new FormData();
    datos.append('nombre', nombre);
    datos.append('desc', descripcion);
    datos.append('fechaInicio', fechaInicio);
    datos.append('fechaFin', fechaFin);
    
    const archivoImagen = document.getElementById("imagen").files[0];
    if (archivoImagen) datos.append('imagen', archivoImagen);
    
    fetch('ProgramaServlet?accion=crear', { method: 'POST', body: datos })
        .then(response => {
            return response.text().then(texto => {
                if (!response.ok) throw new Error(texto);
                return texto;
            });
        })
        .then(mensajeExito => {
            alert(mensajeExito);
            ProgramaServlet("formCrear"); 
        })
        .catch(error => { alert(error.message); });
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
    const checkboxes = document.querySelectorAll('.check-curso');
    const labelContador = document.getElementById('contadorCursosSeleccionados');

    checkboxes.forEach(chk => {
        chk.addEventListener('change', function() {
            const seleccionados = document.querySelectorAll('.check-curso:checked').length;
            if (labelContador) labelContador.innerText = seleccionados;
            
            const fila = this.closest('tr');
            if (this.checked) fila.style.backgroundColor = '#e8f4f8';
            else fila.style.backgroundColor = 'transparent';
        });
    });
}

function agregarCursoAPrograma() {
    const programa = document.getElementById('selectPrograma').value;
    const checkboxesMarcados = document.querySelectorAll('.check-curso:checked');
    
    if (!programa.trim() || checkboxesMarcados.length === 0) {
        alert("Seleccione un programa y al menos un curso para agregar.");
        return;
    } 
    
    const params = new URLSearchParams();
    params.append('accion', 'agregarCurso');
    params.append('programaStr', programa);
    checkboxesMarcados.forEach(chk => params.append('cursoStr', chk.value));
    
    fetch('ProgramaServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: params
        })
        .then(response => {
            return response.text().then(texto => {
                if (!response.ok) throw new Error(texto);
                return texto;
            });
        })
        .then(mensajeExito => {
            alert(mensajeExito);
            ProgramaServlet("formAgregarCurso"); 
        })
        .catch(error => alert(error.message));
}

function cargarProgramas() {
    fetch('ProgramaServlet?accion=formVerProgramas')
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener la lista de programas');
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = '<div class="alerta-error">Ocurrió un error al cargar los programas.</div>';
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
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = '<div class="alerta-error">Ocurrió un error al cargar el detalle del programa.</div>';
        });
}

// =====================================================================
// FUNCIONES DE CURSO Y EDICIÓN
// =====================================================================

function altaCurso(event) {
    event.preventDefault();
    const divError = document.getElementById('mensaje-error');
    divError.style.display = 'none';
    const form = document.getElementById('formAltaCurso');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return; 
    }

    const datos = new FormData(form);

    fetch('CursoServlet?accion=altaCurso', { method: 'POST', body: datos })
    .then(response => {
        if (response.ok) {
            return response.text().then(mensajeExito => {
                alert(mensajeExito); 
                form.reset(); 
                guardarEstadoActualSPA(); // FIX: Guarda tras limpiar
            });
        } else {
            return response.text().then(err => { throw new Error(err); });
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
    const datos = new FormData(form);

    fetch('EdicionServlet?accion=altaEdicion', { method: 'POST', body: datos })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                alert(msg);
                form.reset();
                document.getElementById('cursoEdicion').innerHTML = '<option value="">Primero seleccione un instituto</option>';
                document.getElementById('cursoEdicion').disabled = true;
                guardarEstadoActualSPA(); // FIX: Guarda tras limpiar
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
            if (!response.ok) throw new Error("Error de permisos o de servidor");
            return response.text();
        })
        .then(htmlString => {
            document.getElementById('contenido-dinamico').innerHTML = htmlString;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = "<h3 style='color:red;'>No tienes permisos para ver esta sección.</h3>";
        });
}

function cargarAltaUsuario() {
    fetch('fragmentos/altaUsuario.jsp')
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            inicializarEventosUsuario(); 
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
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
                if (selectSeleccionados.options[i].value === institutoSeleccionado) return alert("El instituto ya fue agregado.");
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

            fetch("UsuarioServlet?accion=crear", { method: "POST", body: datos })
            .then(response => response.text())
            .then(resultado => {
                alert(resultado); 
                document.getElementById('formAltaUsuario').reset();
                document.getElementById('vistaPrevia').src = 'imagenes/usr.png';
                document.getElementById('institutosSeleccionados').innerHTML = ''; 
                guardarEstadoActualSPA(); // FIX: Guarda tras limpiar
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
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
            
            const formLogin = document.getElementById("formLogin");
            if(formLogin) {
                formLogin.addEventListener("submit", function(event) {
                    event.preventDefault();
                    const datos = new URLSearchParams(new FormData(this));
                    fetch('LoginServlet?accion=login', { method: 'POST', body: datos })
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
    fetch(`CursoServlet?accion=buscarCurso&opcion=${encodeURIComponent(opcionBusqueda)}&nombre=${encodeURIComponent(nombreFiltro)}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error("Error cargando lista de cursos:", error));
}

function verDetallesCurso(nombreCurso) {
    fetch(`CursoServlet?accion=verDetallesCurso&nombreCurso=${encodeURIComponent(nombreCurso)}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error("Error cargando detalles del curso:", error));
}

function verDetalleEdicion(nombreEdicion) {
    cargarConsultaEdicion(nombreEdicion);
}

function verDetallePrograma(nombrePrograma) {
    cargarDetallePrograma(nombrePrograma);
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
        }).catch(err => console.log(err));

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
        }).catch(err => console.log(err));
}

function cargarAltaEdicion() {
    fetch('EdicionServlet?accion=altaEdicion')
        .then(response => {
            if (!response.ok) return response.text().then(err => { throw new Error("Acceso denegado."); });
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style="color:red; text-align:center;">${error.message}</h3>`;
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
            cursos.forEach(curso => selectCurso.innerHTML += `<option value="${curso}">${curso}</option>`);
            selectCurso.disabled = false;
        }).catch(err => console.log(err));
}

function cargarModificarUsuario() {
    const contenedor = document.getElementById('contenido-dinamico');
    fetch('fragmentos/ModificarUsuario.jsp')
        .then(response => {
            if (!response.ok) throw new Error("No se pudo cargar la vista.");
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
            if (typeof inicializarEventosModificarUsuario === "function") inicializarEventosModificarUsuario();
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => console.error(error));
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
            .then(res => res.json())
            .then(institutos => {
                institutos.forEach(inst => {
                    const opcion = document.createElement("option");
                    opcion.value = inst.nombre;
                    opcion.textContent = inst.nombre;
                    selectInstituto.appendChild(opcion);
                });
            }).catch(err => console.log(err));

        const btnAgregar = document.getElementById("agregarInstitutoModificar");
        if (btnAgregar) {
            btnAgregar.addEventListener("click", function () {
                const instSeleccionado = selectInstituto.value;
                const selectLista = document.getElementById("institutosSeleccionadosModificar");
                if (!instSeleccionado) return alert("Seleccione un instituto.");
                for (let i = 0; i < selectLista.options.length; i++) {
                    if (selectLista.options[i].value === instSeleccionado) return alert("Ya agregado.");
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
                reader.onload = function (evt) { vistaPrevia.src = evt.target.result; };
                reader.readAsDataURL(file);
            }
        });
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        if (divError) { divError.style.display = 'none'; divError.innerText = ''; }

        const pass = document.getElementById('password') ? document.getElementById('password').value : '';
        const confirmPass = document.getElementById('confirmarPassword') ? document.getElementById('confirmarPassword').value : '';

        if (pass.length > 0 || confirmPass.length > 0) {
            if (pass !== confirmPass) return alert("Las contraseñas no coinciden.");
        }

        const selectLista = document.getElementById("institutosSeleccionadosModificar");
        if (selectLista && selectLista.options.length === 0) return alert("Un docente debe tener al menos un instituto asignado.");

        const formData = new FormData(form);
        formData.append('accion', 'modificarUsuario');
        if (selectLista) {
            for (let i = 0; i < selectLista.options.length; i++) formData.append("institutos", selectLista.options[i].value);
        }

        fetch('UsuarioServlet', { method: 'POST', body: formData })
        .then(response => {
            if (!response.ok) return response.text().then(text => { throw new Error(text); });
            return response.text();
        })
        .then(mensajeExito => {
            alert("Cambios guardados correctamente.");
            window.location.reload();
        })
        .catch(error => alert("Error: " + error.message));
    });
}

// FIX IMPORTANTE: Añadimos un parámetro esRefresco para diferenciar cuando navegamos vs cuando mutamos
function cargarConsultaEdicion(nombreEdicion, esRefresco = false) {
    document.getElementById('contenido-dinamico').innerHTML = "<h3 style='text-align:center;'>Cargando edición...</h3>";
    
    fetch(`EdicionServlet?accion=consultaEdicion&nombre=${encodeURIComponent(nombreEdicion)}`)
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la edición");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            
            // FIX: Si es un refresco (tras agregar/quitar docente), REEMPLAZAMOS el estado
            if (esRefresco) {
                guardarEstadoActualSPA();
            } else {
                crearNuevoEstadoSPA();
            }
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style="color:red; text-align:center;">${error.message}</h3>`;
        });
}

function cargarFormInscripcion() {
    fetch('InscripcionServlet?accion=formulario')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Función para Estudiantes.");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red;'>${error.message}</h3>`);
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

    fetch('InscripcionServlet?accion=inscribir', { method: 'POST', body: datos })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                msgDiv.style.display = 'block';
                msgDiv.style.backgroundColor = '#dff0d8';
                msgDiv.style.color = '#3c763d';
                msgDiv.innerHTML = "✔ " + msg;
                form.reset();
                document.getElementById('cursoInscripcion').disabled = true;
                document.getElementById('edicionInscripcion').disabled = true;
                guardarEstadoActualSPA(); // FIX: Guardar tras limpieza
            });
        } else {
            return response.text().then(err => {
                msgDiv.style.display = 'block';
                msgDiv.style.backgroundColor = '#f2dede';
                msgDiv.style.color = '#a94442';
                msgDiv.innerHTML = "<strong>Atención:</strong> " + err;
            });
        }
    }).catch(error => console.error(error));
}

// =====================================================================
// FUNCIONALIDAD DE RED SOCIAL Y VER PERFIL
// =====================================================================

// FIX IMPORTANTE: Añadimos parámetro esRefresco
function cargarSocial(esRefresco = false) {
    fetch('UsuarioServlet?accion=cargarSocial')
        .then(response => {
            if (!response.ok) return response.text().then(err => { throw new Error(err); });
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            
            // FIX: Si es refresco (por ejemplo tras seguir/dejar de seguir masivo), reemplaza. Sino, crea.
            if (esRefresco) {
                guardarEstadoActualSPA();
            } else {
                crearNuevoEstadoSPA();
            }
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = error.message;
        });
}

function seguirRapido(nickname) {
    const btn = document.querySelector(`button[onclick="seguirRapido('${nickname}')"]`);
    if (btn) toggleSeguirUsuario(btn, nickname, 'seguir');
}

function dejarDeSeguirRapido(nickname) {
    const btn = document.querySelector(`button[onclick="dejarDeSeguirRapido('${nickname}')"]`);
    if (btn) toggleSeguirUsuario(btn, nickname, 'dejarSeguir');
}

function ejecutarAccionSocial(params) {
    fetch('UsuarioServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: params
    })
    .then(response => {
        if (!response.ok) return response.text().then(texto => { throw new Error(texto); });
        return response.text();
    })
    .then(mensajeExito => {
        alert(mensajeExito);
        cargarSocial(true); // FIX: Pasamos true para REEMPLAZAR estado en vez de push
    })
    .catch(error => alert(error.message));
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
                crearNuevoEstadoSPA(); // FIX: Abrir el perfil es una nueva "vista" para el usuario
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
        if (nickname.includes(filtro)) fila.style.display = '';
        else fila.style.display = 'none';
    });
}

function cargarFormSeleccionEstudiantes() {
    fetch('InscripcionServlet?accion=formularioSeleccion')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Función exclusiva para Docentes.");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red;'>${error.message}</h3>`;
        });
}

function cargarCursosParaSeleccion(nombreInstituto) {
    const selectCurso = document.getElementById('cursoSeleccion');
    const selectEdicion = document.getElementById('edicionSeleccion');
    const tablaContenedor = document.getElementById('contenedor-tabla-inscripciones');
    const btnGuardar = document.getElementById('btnGuardarSeleccion');
    
    selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
    selectEdicion.disabled = true;
    if(tablaContenedor) tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione Instituto, Curso y Edición para ver las inscripciones.</p>';
    if(btnGuardar) btnGuardar.disabled = true;

    if (!nombreInstituto) {
        selectCurso.innerHTML = '<option value="">Primero seleccione un instituto</option>';
        selectCurso.disabled = true;
        return;
    }

    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${encodeURIComponent(nombreInstituto)}`)
        .then(response => response.json())
        .then(cursos => {
            selectCurso.innerHTML = '<option value="">Seleccione un Curso...</option>';
            cursos.forEach(c => {
                const option = document.createElement('option');
                option.value = c;
                option.textContent = c;
                selectCurso.appendChild(option);
            });
            selectCurso.disabled = false;
        }).catch(err => console.log(err));
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
            if (document.getElementById('cuerpoTablaInscripciones')) btnGuardar.disabled = false;
            else btnGuardar.disabled = true;
            
            guardarEstadoActualSPA(); // FIX: Guardamos el estado actual al cargar la sub-tabla
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

    fetch('InscripcionServlet', { method: 'POST', body: datos })
    .then(response => {
        if (response.ok) {
            return response.text().then(msg => {
                msgDiv.style.display = 'block';
                msgDiv.style.backgroundColor = '#dff0d8';
                msgDiv.style.color = '#3c763d';
                msgDiv.innerHTML = "✔ " + msg;
                
                const edicionActual = document.getElementById('edicionOculta').value;
                setTimeout(() => cargarTablaInscripciones(edicionActual), 1500); // Re-carga la tabla y allí guardará estado
            });
        } else {
            return response.text().then(err => { throw new Error(err); });
        }
    })
    .catch(error => {
        msgDiv.style.display = 'block';
        msgDiv.style.backgroundColor = '#f2dede';
        msgDiv.style.color = '#a94442';
        msgDiv.innerHTML = "<strong>Error:</strong> " + error.message;
    })
    .finally(() => {
        btnGuardar.disabled = false;
        btnGuardar.innerText = 'Guardar Selección';
    });
}

function cargarConsultaUsuario(callback) {
    const contenedor = document.getElementById('contenido-dinamico');
    contenedor.innerHTML = '<h3 style="text-align:center; color:#3498db;">Cargando usuarios...</h3>';

    fetch('UsuarioServlet?accion=consultaUsuario')
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la consulta de usuario.");
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
            
            if (typeof callback === 'function') {
                callback();
            }
        })
        .catch(error => {
            contenedor.innerHTML = `<p style="color:red; text-align:center;">${error.message}</p>`;
        });
}

function cargarDetalleConsultaUsuario(nickname) {
    const contenedor = document.getElementById('detalle-usuario-contenido');
    if (!contenedor) return;

    contenedor.innerHTML = '<h4 style="text-align:center; color:#3498db;">Cargando detalles del usuario...</h4>';

    fetch(`UsuarioServlet?accion=detalleConsultaUsuario&nickname=${encodeURIComponent(nickname)}`)
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la información del usuario.");
            return response.text();
        })
        .then(html => {
            contenedor.innerHTML = html;
            guardarEstadoActualSPA(); // FIX: Es una sub-vista, sobrescribimos el estado actual
        })
        .catch(error => {
            contenedor.innerHTML = `<p style="color:red; text-align:center;">${error.message}</p>`;
        });
}

function cambiarPestana(idPestana) {
    const contenidos = document.querySelectorAll('.contenido-tab');
    contenidos.forEach(div => div.style.display = 'none');
    const botones = document.querySelectorAll('.tab-btn');
    botones.forEach(btn => { btn.style.background = '#e0e0e0'; btn.style.color = '#333'; });

    const tabSeleccionado = document.getElementById(idPestana);
    if (tabSeleccionado) tabSeleccionado.style.display = 'block';

    const btnSeleccionado = document.getElementById(`btn-${idPestana}`);
    if (btnSeleccionado) { btnSeleccionado.style.background = '#3498db'; btnSeleccionado.style.color = 'white'; }
}

function cargarMiPerfil(nickname) {
    if (!nickname) return;
    const contenedorPrincipal = document.getElementById('contenido-dinamico');
    if (!contenedorPrincipal) return;

    contenedorPrincipal.innerHTML = '<h4 style="text-align:center; color:#3498db; margin-top:20px;">Cargando Mi Perfil...</h4>';

    fetch('UsuarioServlet?accion=consultaUsuario')
        .then(response => {
            if (!response.ok) throw new Error("Error al cargar la interfaz de consulta.");
            return response.text();
        })
        .then(htmlConsulta => {
            contenedorPrincipal.innerHTML = htmlConsulta;
            
            const lista = document.getElementById('contenedor-lista-usuarios');
            if (lista) lista.style.display = 'none';

            const detalle = document.getElementById('contenedor-detalle-usuario');
            if (detalle) detalle.style.display = 'block';

            const btnVolver = contenedorPrincipal.querySelector('button[onclick="volverAListaUsuarios()"]');
            if (btnVolver) btnVolver.style.display = 'none';

            const titulo = contenedorPrincipal.querySelector('h2');
            if (titulo) titulo.textContent = 'Mi Perfil';

            cargarDetalleConsultaUsuario(nickname);
            crearNuevoEstadoSPA(); // FIX: Nuevo estado principal
        })
        .catch(error => {
            contenedorPrincipal.innerHTML = `<p style="color:red; text-align:center; padding:20px;">${error.message}</p>`;
        });
}

function cargarFormListarAceptados() {
    fetch('InscripcionServlet?accion=formularioAceptados')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Función exclusiva para Docentes.");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red; text-align:center;'>${error.message}</h3>`;
        });
}

function cargarCursosParaAceptados(nombreInstituto) {
    const selectCurso = document.getElementById('cursoAceptados');
    const selectEdicion = document.getElementById('edicionAceptados');
    const tablaContenedor = document.getElementById('contenedor-tabla-aceptados');

    selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
    selectEdicion.disabled = true;
    tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione un Instituto, un Curso y una Edición para visualizar sus datos básicos y los estudiantes aceptados.</p>';

    if (!nombreInstituto) {
        selectCurso.innerHTML = '<option value="">Primero seleccione un instituto</option>';
        selectCurso.disabled = true;
        return;
    }

    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${encodeURIComponent(nombreInstituto)}`)
        .then(response => response.json())
        .then(cursos => {
            selectCurso.innerHTML = '<option value="">Seleccione un Curso...</option>';
            cursos.forEach(c => selectCurso.innerHTML += `<option value="${c}">${c}</option>`);
            selectCurso.disabled = false;
        }).catch(err => console.log(err));
}

function cargarEdicionesParaAceptados(nombreCurso) {
    const selectEdicion = document.getElementById('edicionAceptados');
    const tablaContenedor = document.getElementById('contenedor-tabla-aceptados');

    tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione un Instituto, un Curso y una Edición para visualizar sus datos básicos y los estudiantes aceptados.</p>';

    if (!nombreCurso) {
        selectEdicion.innerHTML = '<option value="">Primero seleccione un curso</option>';
        selectEdicion.disabled = true;
        return;
    }

    fetch(`InscripcionServlet?accion=listarEdiciones&curso=${encodeURIComponent(nombreCurso)}`)
        .then(response => response.json())
        .then(ediciones => {
            if (ediciones.length === 0) {
                selectEdicion.innerHTML = '<option value="">No hay ediciones disponibles</option>';
                selectEdicion.disabled = true;
            } else {
                selectEdicion.innerHTML = '<option value="">Seleccione la Edición...</option>';
                ediciones.forEach(e => selectEdicion.innerHTML += `<option value="${e}">${e}</option>`);
                selectEdicion.disabled = false;
            }
        });
}

function cargarTablaAceptados(nombreEdicion) {
    const tablaContenedor = document.getElementById('contenedor-tabla-aceptados');

    if (!nombreEdicion) {
        tablaContenedor.innerHTML = '<p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">Seleccione un Instituto, un Curso y una Edición para visualizar sus datos básicos y los estudiantes aceptados.</p>';
        return;
    }

    tablaContenedor.innerHTML = '<h4 style="text-align:center; color: #27ae60;">Cargando datos de la edición y estudiantes aceptados...</h4>';

    fetch(`InscripcionServlet?accion=listarAceptados&edicion=${encodeURIComponent(nombreEdicion)}`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudieron obtener los datos.");
            return response.text();
        })
        .then(html => {
            tablaContenedor.innerHTML = html;
            guardarEstadoActualSPA(); // FIX: Guardar el estado de la sub-tabla
        })
        .catch(error => {
            tablaContenedor.innerHTML = `<p style="color:red; text-align:center;">Error al cargar los estudiantes aceptados.</p>`;
        });
}

function cargarResultadosInscripciones() {
    fetch('InscripcionServlet?accion=misResultados')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Función exclusiva para Estudiantes.");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red; text-align:center;'>${error.message}</h3>`;
        });
}

function filtrarMisInscripciones(estadoSeleccionado) {
    const filas = document.querySelectorAll('#tablaMisInscripcionesBody tr.fila-mi-inscripcion');
    filas.forEach(fila => {
        const estadoFila = fila.getAttribute('data-estado');
        if (estadoSeleccionado === 'TODOS' || estadoFila === estadoSeleccionado) fila.style.display = '';
        else fila.style.display = 'none';
    });
}

function cargarSelectorConsultaCurso() {
    const htmlBuscador = `
        <div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px;">
            <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">Consultar Curso</h2>
            <div style="display: flex; gap: 15px; margin-bottom: 20px;">
                <div style="flex: 1;">
                    <label style="font-weight: bold;">Instituto:</label>
                    <select id="instBuscadorCurso" onchange="cargarCursosParaBuscador(this.value)" style="width: 100%; padding: 8px;">
                        <option value="">Seleccione Instituto...</option>
                    </select>
                </div>
                <div style="flex: 1;">
                    <label style="font-weight: bold;">Curso:</label>
                    <select id="cursoBuscadorFinal" onchange="verDetallesCurso(this.value)" disabled style="width: 100%; padding: 8px;">
                        <option value="">Primero seleccione un instituto</option>
                    </select>
                </div>
            </div>
        </div>
    `;
    document.getElementById('contenido-dinamico').innerHTML = htmlBuscador;
    cargarInstitutosGenericos('instBuscadorCurso'); 
    crearNuevoEstadoSPA();
}

function cargarSelectorConsultaEdicion() {
    const htmlBuscador = `
        <div style="max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px;">
            <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">Consultar Edición de Curso</h2>
            <div style="display: flex; gap: 15px; margin-bottom: 20px;">
                <div style="flex: 1;">
                    <label style="font-weight: bold;">Instituto:</label>
                    <select id="instBuscadorEd" onchange="cargarCursosParaBuscadorEdicion(this.value)" style="width: 100%; padding: 8px;">
                        <option value="">Seleccione Instituto...</option>
                    </select>
                </div>
                <div style="flex: 1;">
                    <label style="font-weight: bold;">Curso:</label>
                    <select id="cursoBuscadorEd" onchange="cargarEdicionesParaBuscadorEdicion(this.value)" disabled style="width: 100%; padding: 8px;">
                        <option value="">Primero seleccione instituto</option>
                    </select>
                </div>
                <div style="flex: 1;">
                    <label style="font-weight: bold;">Edición:</label>
                    <select id="edicionBuscadorFinal" onchange="cargarConsultaEdicion(this.value)" disabled style="width: 100%; padding: 8px;">
                        <option value="">Primero seleccione curso</option>
                    </select>
                </div>
            </div>
        </div>
    `;
    document.getElementById('contenido-dinamico').innerHTML = htmlBuscador;
    cargarInstitutosGenericos('instBuscadorEd');
    crearNuevoEstadoSPA(); // FIX: Nuevo estado
}

function cargarInstitutosGenericos(selectId) {
    fetch('CursoServlet?accion=listarInstitutosJSON')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById(selectId);
            if (select) data.forEach(inst => select.innerHTML += `<option value="${inst}">${inst}</option>`);
        }).catch(err => {
            const select = document.getElementById(selectId);
            if (select) select.innerHTML = '<option value="">Error cargando</option>';
        });
}

function cargarCursosParaBuscador(instituto) {
    const selectCurso = document.getElementById('cursoBuscadorFinal');
    selectCurso.innerHTML = '<option value="">Seleccione Curso...</option>';
    selectCurso.disabled = true;
    if(!instituto) return;
    
    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${encodeURIComponent(instituto)}`)
        .then(res => res.json())
        .then(cursos => {
            cursos.forEach(c => {
                const option = document.createElement('option');
                option.value = c;
                option.textContent = c;
                selectCurso.appendChild(option);
            });
            selectCurso.disabled = false;
        });
}

function cargarCursosParaBuscadorEdicion(instituto) {
    const selectCurso = document.getElementById('cursoBuscadorEd');
    document.getElementById('edicionBuscadorFinal').innerHTML = '<option value="">Primero seleccione curso</option>';
    document.getElementById('edicionBuscadorFinal').disabled = true;
    selectCurso.innerHTML = '<option value="">Seleccione Curso...</option>';
    selectCurso.disabled = true;
    
    if(!instituto) return;
    
    fetch(`EdicionServlet?accion=listarCursosPorInstituto&instituto=${encodeURIComponent(instituto)}`)
        .then(res => res.json())
        .then(cursos => {
            cursos.forEach(c => {
                const option = document.createElement('option');
                option.value = c;
                option.textContent = c;
                selectCurso.appendChild(option);
            });
            selectCurso.disabled = false;
        });
}

function cargarEdicionesParaBuscadorEdicion(curso) {
    const selectEdicion = document.getElementById('edicionBuscadorFinal');
    selectEdicion.innerHTML = '<option value="">Seleccione Edición...</option>';
    selectEdicion.disabled = true;
    if(!curso) return;

    fetch(`InscripcionServlet?accion=listarEdiciones&curso=${encodeURIComponent(curso)}`)
        .then(res => res.json())
        .then(ediciones => {
            ediciones.forEach(e => selectEdicion.innerHTML += `<option value="${e}">${e}</option>`);
            selectEdicion.disabled = false;
        });
}

function cargarFormInscripcionPrograma() {
    fetch('ProgramaServlet?accion=formInscripcionPrograma')
        .then(response => {
            if (!response.ok) throw new Error("Acceso denegado. Función exclusiva para Estudiantes.");
            return response.text();
        })
        .then(html => {
            document.getElementById('contenido-dinamico').innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Nuevo estado
        })
        .catch(error => {
            document.getElementById('contenido-dinamico').innerHTML = `<h3 style='color:red; text-align:center;'>${error.message}</h3>`;
        });
}

function inscribirPrograma(event) {
    event.preventDefault();
    const msgDiv = document.getElementById('mensaje-inscripcion-prog');
    const form = document.getElementById('formInscripcionPrograma');
    const datos = new URLSearchParams(new FormData(form));
    datos.append('accion', 'inscribirPrograma');

    fetch('ProgramaServlet', { method: 'POST', body: datos })
    .then(response => {
        return response.text().then(msg => {
            msgDiv.style.display = 'block';
            if (response.ok) {
                msgDiv.style.backgroundColor = '#dff0d8';
                msgDiv.style.color = '#3c763d';
                msgDiv.innerHTML = "✔ " + msg;
                form.reset();
                guardarEstadoActualSPA(); // FIX: Guardar tras limpieza
            } else {
                msgDiv.style.backgroundColor = '#f2dede';
                msgDiv.style.color = '#a94442';
                msgDiv.innerHTML = "<strong>Atención:</strong> " + msg;
            }
        });
    }).catch(error => console.log(error));
}

// =====================================================================
// CONTROLADOR DE MODALES MODERNOS
// =====================================================================
function mostrarModal(titulo, htmlCuerpo, botonesHtml = null) {
    document.getElementById('modal-titulo').innerText = titulo;
    document.getElementById('modal-cuerpo').innerHTML = htmlCuerpo;
    
    const pie = document.getElementById('modal-pie');
    if (botonesHtml) pie.innerHTML = botonesHtml;
    else pie.innerHTML = `<button class="btn-moderno" style="background-color: #3498db;" onclick="cerrarModal()">Aceptar</button>`;
    
    const modal = document.getElementById('modal-global');
    modal.classList.remove('modal-oculto');
    modal.classList.add('modal-visible');
}

function cerrarModal() {
    const modal = document.getElementById('modal-global');
    modal.classList.remove('modal-visible');
    modal.classList.add('modal-oculto');
}

function cargarFormularioAgregarDocente(nombreEdicion) {
    fetch('EdicionServlet?accion=listarDocentesJSON')
        .then(res => res.json())
        .then(docentes => {
            let opcionesHtml = '<option value="">Seleccione un docente...</option>';
            docentes.forEach(doc => opcionesHtml += `<option value="${doc}">${doc}</option>`);

            const htmlCuerpo = `
                <div style="margin-bottom: 15px;">
                    <p style="margin-top: 0; color: #7f8c8d;">Selecciona el docente que deseas añadir a la edición <strong>${nombreEdicion}</strong>.</p>
                    <label style="font-weight: bold; color: #2c3e50;">Docente:</label>
                    <select id="select-nuevo-docente" style="width: 100%; padding: 10px; border-radius: 6px; border: 1px solid #bdc3c7; margin-top: 5px;">
                        ${opcionesHtml}
                    </select>
                </div>
            `;
            const htmlBotones = `
                <button class="btn-moderno" style="background-color: #95a5a6; color: white;" onclick="cerrarModal()">Cancelar</button>
                <button class="btn-moderno" style="background-color: #2ecc71; margin-left: 10px;" onclick="guardarDocenteEdicion('${nombreEdicion}')">Guardar Docente</button>
            `;
            mostrarModal('Asignar Nuevo Docente', htmlCuerpo, htmlBotones);
        }).catch(err => alert("Error cargando la lista."));
}

function guardarDocenteEdicion(nombreEdicion) {
    const nicknameDocente = document.getElementById('select-nuevo-docente').value;
    if (!nicknameDocente) return alert("Selecciona un docente de la lista.");

    const params = new URLSearchParams();
    params.append('accion', 'agregarDocente');
    params.append('edicion', nombreEdicion);
    params.append('docente', nicknameDocente);

    fetch('EdicionServlet', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
    .then(response => {
        if (response.ok) {
            cerrarModal(); 
            // FIX: Actualizamos la tabla de docentes y pasamos 'true' para indicar que es un reemplazo de estado
            cargarConsultaEdicion(nombreEdicion, true); 
        } else {
            response.text().then(errorMsg => alert("Error: " + errorMsg));
        }
    });
}

function confirmarQuitarDocente(nombreEdicion, nicknameDocente, nombreCompleto) {
    const htmlCuerpo = `
        <div style="text-align: center; padding: 10px;">
            <p style="font-size: 15px; color: #2c3e50;">¿Estás seguro de que deseas quitar al docente <strong style="color: #e74c3c;">${nombreCompleto}</strong> de esta edición?</p>
            <p style="font-size: 13px; color: #7f8c8d;">Se actualizará la base de datos inmediatamente.</p>
        </div>
    `;
    const htmlBotones = `
        <button class="btn-moderno" style="background-color: #95a5a6; color: white;" onclick="cerrarModal()">Cancelar</button>
        <button class="btn-moderno" style="background-color: #e74c3c; margin-left: 10px;" onclick="ejecutarQuitarDocente('${nombreEdicion}', '${nicknameDocente}')">Sí, Quitar</button>
    `;
    mostrarModal('Confirmar Eliminación', htmlCuerpo, htmlBotones);
}

function ejecutarQuitarDocente(nombreEdicion, nicknameDocente) {
    const params = new URLSearchParams();
    params.append('accion', 'quitarDocente');
    params.append('edicion', nombreEdicion);
    params.append('docente', nicknameDocente);

    fetch('EdicionServlet', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params })
    .then(response => {
        if (response.ok) {
            cerrarModal();
            // FIX: Actualizamos la tabla de docentes y pasamos 'true' para reemplazar estado
            cargarConsultaEdicion(nombreEdicion, true); 
        } else {
            response.text().then(errorMsg => alert("Error al quitar: " + errorMsg));
        }
    });
}

function seleccionarUsuario(nickname) {
    document.getElementById('contenedor-lista-usuarios').style.display = 'none';
    document.getElementById('contenedor-detalle-usuario').style.display = 'block';
    const contenedorDetalle = document.getElementById('detalle-usuario-contenido');
    if (!contenedorDetalle) return;

    contenedorDetalle.innerHTML = '<p style="text-align: center; color: #7f8c8d; margin-top: 40px;">Cargando datos...</p>';

    fetch(`UsuarioServlet?accion=detalleConsultaUsuario&nickname=${encodeURIComponent(nickname)}`)
        .then(response => {
            if (!response.ok) throw new Error("Error del servidor");
            return response.text(); 
        })
        .then(html => {
            contenedorDetalle.innerHTML = html;
            crearNuevoEstadoSPA(); // FIX: Abrir la tarjeta de usuario es un nuevo estado
        })
        .catch(error => {
            contenedorDetalle.innerHTML = `<p style="color:red; text-align:center;">Error: ${error.message}</p>`;
        });
}

function volverAListaUsuarios() {
    document.getElementById('contenedor-detalle-usuario').style.display = 'none';
    document.getElementById('contenedor-lista-usuarios').style.display = 'block';
    
    const contenedorDetalle = document.getElementById('detalle-usuario-contenido');
    if (contenedorDetalle) contenedorDetalle.innerHTML = `<p style="color: #7f8c8d; text-align: center; margin-top: 60px;">Cargando información del usuario...</p>`;
    
    crearNuevoEstadoSPA(); // FIX: Volver a la lista es un nuevo estado
}

// =====================================================================
// FUNCIONALIDAD DE SEGUIR/DEJAR DE SEGUIR EN TIEMPO REAL (INVENCIBLE)
// =====================================================================

function toggleSeguirUsuario(btnElementClickeado, nickname, accionActual) {
    if (btnElementClickeado && btnElementClickeado.disabled) return;

    const nickStr = String(nickname).trim();
    const nickLower = nickStr.toLowerCase();
    
    // 1. ESCÁNER ABSOLUTO DE BOTONES
    const botones = [];
    document.querySelectorAll('button').forEach(btn => {
        const attr = btn.getAttribute('onclick');
        if (attr && attr.includes('toggleSeguirUsuario') && (attr.includes(`'${nickStr}'`) || attr.toLowerCase().includes(`'${nickLower}'`))) {
            botones.push(btn);
        }
    });

    botones.forEach(b => b.disabled = true);

    // 2. APLICAR ESTADOS A TODOS A LA VEZ
    if (accionActual === 'seguir') {
        botones.forEach(btn => {
            btn.innerHTML = 'Dejar de seguir'; 
            btn.style.backgroundColor = '#d9534f'; 
            btn.style.color = 'white'; 
            btn.setAttribute('onclick', `toggleSeguirUsuario(this, '${nickStr}', 'dejarSeguir')`);
        });
    } else {
        botones.forEach(btn => {
            btn.innerHTML = 'Seguir';
            btn.style.backgroundColor = '#0275d8'; 
            btn.style.color = 'white';
            btn.setAttribute('onclick', `toggleSeguirUsuario(this, '${nickStr}', 'seguir')`);
        });
    }

    // 3. ACTUALIZAR CAJA TOP-RIGHT ("Mis Seguidos")
    const listaMisSeguidos = document.getElementById('lista-mis-seguidos-social');
    if (listaMisSeguidos) {
        const itemsTopRight = Array.from(listaMisSeguidos.querySelectorAll('li'));
        const itemExistente = itemsTopRight.find(li => li.innerHTML.toLowerCase().includes(`<strong>${nickLower}</strong>`));
        
        if (accionActual === 'seguir') {
            if (!itemExistente) {
                const msgVacio = document.getElementById('social-sin-seguidos');
                if (msgVacio) msgVacio.style.display = 'none';
                
                const nuevoLi = document.createElement('li');
                nuevoLi.id = `social-seguido-${nickLower}`;
                nuevoLi.style.cssText = "padding: 6px 0; border-bottom: 1px solid #eee; display: flex; justify-content: space-between;";
                nuevoLi.innerHTML = `<span>👤 <strong>${nickStr}</strong></span> <span onclick="toggleSeguirUsuario(this, '${nickStr}', 'dejarSeguir')" style="color: #e74c3c; cursor: pointer; font-weight: bold;" title="Dejar de seguir">X</span>`;
                listaMisSeguidos.appendChild(nuevoLi);
            }
        } else {
            if (itemExistente) itemExistente.remove();
            if (listaMisSeguidos.querySelectorAll('li[id^="social-seguido-"]').length === 0) {
                 const msgVacio = document.getElementById('social-sin-seguidos');
                 if (msgVacio) msgVacio.style.display = 'block';
            }
        }
    }

    // 4. ACTUALIZAR CAJA BOTTOM-RIGHT Y CONTADOR DE PERFIL ABIERTO
    const inputMiNickname = document.getElementById('nickname-usuario-logueado');
    const miNickname = inputMiNickname ? inputMiNickname.value : null;
    const listaSeguidoresPerfil = document.getElementById('lista-seguidores-perfil');
    
    if (miNickname && listaSeguidoresPerfil) {
        const miNickClean = miNickname.toLowerCase();
        const itemsPerfil = Array.from(listaSeguidoresPerfil.querySelectorAll('li'));
        const miItem = itemsPerfil.find(li => li.innerHTML.toLowerCase().includes(`<b>${miNickClean}</b>`));

        if (accionActual === 'seguir') {
            if (!miItem) {
                const msgVacioSeg = document.getElementById('mensaje-sin-seguidores-perfil');
                if (msgVacioSeg) msgVacioSeg.style.display = 'none';
                
                const li = document.createElement('li');
                li.id = `li-seguidor-perfil-${miNickClean}`;
                li.style.cssText = "padding: 4px 0; border-bottom: 1px dashed #eee; font-size: 13px;";
                li.innerHTML = `👤 <b>${miNickname}</b>`;
                listaSeguidoresPerfil.appendChild(li);
            }
        } else {
            if (miItem) miItem.remove();
            if (listaSeguidoresPerfil.querySelectorAll('li[id^="li-seguidor-perfil-"]').length === 0) {
                 const msgVacioSeg = document.getElementById('mensaje-sin-seguidores-perfil');
                 if (msgVacioSeg) msgVacioSeg.style.display = 'block';
            }
        }
    }

    // Recalcular contador visual
    const contadorSeguidores = document.getElementById('contador-seguidores-perfil');
    if (contadorSeguidores && listaSeguidoresPerfil) {
        contadorSeguidores.innerText = listaSeguidoresPerfil.querySelectorAll('li[id^="li-seguidor-perfil-"]').length;
    }

    // 5. AVISAMOS AL SERVIDOR Y GUARDAMOS
    const params = new URLSearchParams();
    params.append('accion', accionActual);
    if (accionActual === 'seguir') params.append('nicknameASeguir', nickStr);
    else params.append('nicknameSeleccionado', nickStr);

    fetch('UsuarioServlet', { method: 'POST', body: params })
    .then(res => {
        if (!res.ok) throw new Error("Fallo en BD");
        guardarEstadoActualSPA(); 
    })
    .catch(err => {
        console.error(err);
        alert("Error de conexión. Recargando la vista...");
        window.location.reload();
    })
    .finally(() => {
        botones.forEach(btn => btn.disabled = false);
    });
}