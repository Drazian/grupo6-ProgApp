<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="contenedor-formulario">
    <h2>Alta de Usuario</h2>
    <hr>
    
    <form id="formAltaUsuario" enctype="multipart/form-data">
        <div class="form-group">
            <label for="nickname">Nickname:</label>
            <input type="text" id="nickname" required>
        </div>

        <div class="form-group">
            <label for="nombre">Nombre:</label>
            <input type="text" id="nombre" required>
        </div>
        
        <div class="form-group">
            <label for="apellido">Apellido:</label>
            <input type="text" id="apellido" required>
        </div>

        <div class="form-group">
            <label for="email">Correo electrónico:</label>
            <input type="email" id="email" required>
        </div>

        <div class="form-group">
            <label for="password">Contraseña:</label>
            <input type="password" id="password" required>
        </div>

        <div class="form-group">
            <label for="confirmarPassword">Confirmar contraseña:</label>
            <input type="password" id="confirmarPassword" required>
        </div>
        
        <div class="form-group">
            <label for="fechaNacimiento">Fecha de nacimiento:</label>
            <input type="date" id="fechaNacimiento" required>
        </div>
        
        <div class="form-group" style="display: flex; align-items: center; gap: 10px;">
            <label for="docente" style="margin: 0;">¿Es Docente?</label>
            <input type="checkbox" id="docente">
        </div>

        <!-- Sección de Institutos (Oculta por defecto) -->
        <div id="seccionInstitutos" style="display: none; padding: 15px; background: #f9f9f9; border: 1px solid #ddd; margin-bottom: 15px;">
            <div class="form-group">
                <label for="instituto">Seleccionar Instituto:</label>
                <select id="instituto">
                    <option value="">Seleccione un instituto</option>
                </select>
            </div>
            <div style="margin-bottom: 15px;">
                <button type="button" id="agregarInstituto">Agregar instituto</button>
                <button type="button" id="quitarInstituto">Quitar instituto</button>
            </div>
            <div class="form-group">
                <label for="institutosSeleccionados">Institutos confirmados:</label>
                <select id="institutosSeleccionados" size="4" style="width: 100%;"></select>
            </div>
        </div>
            
        <div class="form-group">
            <label for="imagen">Imagen de perfil:</label>
            <input type="file" id="imagen" accept=".jpg, .png">
            <br><br>
            <!-- Ruta corregida sin el ../ -->
            <img id="vistaPrevia" src="imagenes/usr.png" alt="Imagen de usuario" width="150" height="150" style="border-radius: 5px; object-fit: cover;">
        </div>

        <div class="form-actions">
            <button type="submit" id="crearUsuario">Crear usuario</button>
            <button type="button" onclick="document.getElementById('formAltaUsuario').reset(); document.getElementById('vistaPrevia').src='imagenes/usr.png';">Cancelar</button>
        </div>
    </form>
</div>