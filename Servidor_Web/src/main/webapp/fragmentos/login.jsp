<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="contenedor-formulario" style="max-width: 400px; margin: 0 auto;">
    <h2>Iniciar Sesión</h2>
    <hr>
    
    <div id="mensaje-error-login" style="color: red; display: none; margin-bottom: 15px;"></div>

    <form id="formLogin">
        <div class="form-group">
            <label for="identificador">Nickname o Correo electrónico:</label>
            <input type="text" id="identificador" name="identificador" required>
        </div>

        <div class="form-group">
            <label for="password">Contraseña:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <div class="form-actions" style="margin-top: 20px; text-align: center;">
            <button type="submit" style="width: 100%;">Ingresar</button>
        </div>
    </form>
</div>