<%-- 
    Document   : altaUsuario
    Created on : 7 oct 2026, 19:18:42
    Author     : usuario
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Alta de Usuario</h1>
        <br><br> <!-- salto de linea -->
        <label for="nickname">Nickname:</label>
        <form id="formAltaUsuario" enctype="multipart/form-data">
        <input type="text" id="nickname">
        
        <br><br>

        <label for="nombre">Nombre:</label>
        <input type="text" id="nombre">
        
        <br><br>
        <label for="apellido">Apellido:</label>
        <input type="text" id="apellido">

        <br><br>
        <label for="email">Correo electrónico:</label>
        <input type="text" id="email">

        <br><br>
        <label for="password">Contraseña:</label>
        <input type="password" id="password">

        <br><br>
        <label for="confirmarPassword">Confirmar contraseña:</label>
        <input type="password" id="confirmarPassword">
        
        <br><br>
        <label for="fechaNacimiento">Fecha de nacimiento:</label>
        <input type="date" id="fechaNacimiento">
        
        <br><br>
        <label for="docente">Docente:</label>
        <input type="checkbox" id="docente">

        <br><br>
        <div id="seccionInstitutos" style="display: none;">

        <br><br>

        <label for="instituto">Instituto:</label>
        <select id="instituto">
            <option value="">Seleccione un instituto</option>
        </select>

        <br><br>

        <button type="button" id="agregarInstituto">Agregar instituto</button>
        <button type="button" id="quitarInstituto">Quitar instituto</button>

        <br><br>

        <label for="institutosSeleccionados">Institutos seleccionados:</label>
        <select id="institutosSeleccionados" size="4">
        </select>

        </div>
            <label for="imagen">Imagen de perfil:</label>
            <input type="file" id="imagen" accept=".jpg, .png">

            <br><br>

            <img id="vistaPrevia"
                src="../imagenes/usr.png"
                alt="Imagen de usuario"
                width="150"
                height="150">

        <br><br>
        <button type="submit" id="crearUsuario">Crear usuario</button>
        <button type="reset" id="cancelar">Cancelar</button>
        </form>
        
        <br><br>

        
        <!-- Cargar Imagen seleccionada -->
        <script>
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
        </script>
        <!-- Mostrar elementos seg -->
        
        <script>
            document.getElementById("docente").addEventListener("change", function() {
                const seccionInstitutos = document.getElementById("seccionInstitutos");

                if (this.checked) {
                    seccionInstitutos.style.display = "block";
                } else {
                    seccionInstitutos.style.display = "none";
                }
            });
        </script>
        <!-- ListarInstitutos -->
        <script>
            fetch("../UsuarioServlet?accion=listarInstitutos")
                .then(response => response.json())
                .then(institutos => {

                    const selectInstituto = document.getElementById("instituto");

                    institutos.forEach(instituto => {

                        const opcion = document.createElement("option");

                        opcion.value = instituto.nombre;
                        opcion.textContent = instituto.nombre;

                        selectInstituto.appendChild(opcion);
                    });
                });
        </script>
        
        <script>
            // Botón Agregar instituto
            document.getElementById("agregarInstituto").addEventListener("click", function() {

                const selectInstituto = document.getElementById("instituto");
                const selectSeleccionados = document.getElementById("institutosSeleccionados");

                const institutoSeleccionado = selectInstituto.value;

                if (institutoSeleccionado === "") {
                    alert("Seleccione un instituto.");
                    return;
                }

                for (let i = 0; i < selectSeleccionados.options.length; i++) {

                    if (selectSeleccionados.options[i].value === institutoSeleccionado) {
                        alert("El instituto ya fue agregado.");
                        return;
                    }
                }

                const opcion = document.createElement("option");

                opcion.value = institutoSeleccionado;
                opcion.textContent = institutoSeleccionado;

                selectSeleccionados.appendChild(opcion);
            });


            // Botón Quitar instituto
            document.getElementById("quitarInstituto").addEventListener("click", function() {

                const selectSeleccionados = document.getElementById("institutosSeleccionados");

                if (selectSeleccionados.selectedIndex === -1) {
                    alert("Seleccione un instituto para quitar.");
                    return;
                }

                selectSeleccionados.remove(selectSeleccionados.selectedIndex);
            });
        </script>

        
        <script>
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

                // Verificar campos vacíos
                if (
                    nickname === "" ||
                    nombre === "" ||
                    apellido === "" ||
                    email === "" ||
                    password === "" ||
                    confirmarPassword === "" ||
                    fechaNacimiento === ""
                ) {
                    alert("Debe completar todos los campos.");
                    return;
                }

                // Verificar que las contraseñas sean iguales
                if (password !== confirmarPassword) {
                    alert("Las contraseñas no coinciden.");
                    return;
                }

                // Obtener la imagen seleccionada
                const archivoImagen = document.getElementById("imagen").files[0];

                // Obtener los institutos seleccionados
                const institutos = [];

                const selectSeleccionados =
                    document.getElementById("institutosSeleccionados");

                for (let i = 0; i < selectSeleccionados.options.length; i++) {
                    institutos.push(selectSeleccionados.options[i].value);
                }

                // Determinar el tipo de usuario
                const tipoUsuario = docente ? "DOCENTE" : "ESTUDIANTE";

                // Crear FormData
                const datos = new FormData();

                datos.append("nickname", nickname);
                datos.append("nombre", nombre);
                datos.append("apellido", apellido);
                datos.append("email", email);
                datos.append("password", password);
                datos.append("fechaNacimiento", fechaNacimiento);
                datos.append("tipoUsuario", tipoUsuario);

                // Agregar institutos
                institutos.forEach(function(instituto) {
                    datos.append("institutos", instituto);
                });

                // Agregar imagen
                if (archivoImagen) {
                    datos.append("imagen", archivoImagen);
                }

                // Enviar datos al Servlet
                fetch("../UsuarioServlet?accion=crear", {
                    method: "POST",
                    body: datos
                })
                .then(response => response.text())
                .then(resultado => {
                    console.log("Respuesta del servidor:", resultado);
                })
                .catch(error => {
                    console.error("Error:", error);
                });

            });
        </script>
    </body>
</html>
