<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="contenedor-formulario" style="max-width: 550px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff;">
    <h2>Modificar Perfil</h2>
    <hr style="margin-bottom: 20px;">

    <!-- Mensaje de feedback para AJAX -->
    <div id="mensaje-error-modificar" style="display:none; background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px;"></div>

    <form id="formModificarUsuario" enctype="multipart/form-data">
        
        <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Nickname:</label>
            <input type="text" value="${sessionScope.usuarioLogueado.nickname}" readonly disabled style="width: 100%; padding: 8px; background-color: #e9ecef; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Correo Electrónico:</label>
            <input type="email" value="${sessionScope.usuarioLogueado.email}" readonly disabled style="width: 100%; padding: 8px; background-color: #e9ecef; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="nombre" style="display: block; font-weight: bold; margin-bottom: 5px;">Nombre:</label>
            <input type="text" id="nombre" name="nombre" value="${sessionScope.usuarioLogueado.nombre}" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="apellido" style="display: block; font-weight: bold; margin-bottom: 5px;">Apellido:</label>
            <input type="text" id="apellido" name="apellido" value="${sessionScope.usuarioLogueado.apellido}" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="fechaNacimiento" style="display: block; font-weight: bold; margin-bottom: 5px;">Fecha de Nacimiento:</label>
            <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="${sessionScope.usuarioLogueado.fNacimiento}" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Imagen de Perfil:</label>
            <div style="display: flex; align-items: center; gap: 15px; margin-top: 5px;">
                <img id="vistaPreviaModificar" src="imagenes/${empty sessionScope.usuarioLogueado.imagen ? 'usr.png' : sessionScope.usuarioLogueado.imagen}" alt="Vista previa" style="width: 70px; height: 70px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">
                <input type="file" id="imagen" name="imagen" accept="image/*" />
            </div>
        </div>

        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" style="background-color: #5cb85c; color: white; border: none; padding: 10px 20px; font-weight: bold; cursor: pointer; border-radius: 4px;">Guardar Cambios</button>
        </div>

    </form>
</div>