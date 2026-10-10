<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="contenedor-formulario" style="max-width: 500px; margin: 0 auto; padding: 20px;">
    <h2>Modificar Perfil</h2>
    <hr>

    <div id="mensaje-error-modificar" style="color: red; display: none; margin-bottom: 15px;"></div>

    <form id="formModificarUsuario" enctype="multipart/form-data">
        
        <div class="form-group">
            <label for="nickname">Nickname:</label>
            <input type="text" id="nickname" name="nickname" value="${sessionScope.usuarioLogueado.nickname}" readonly style="background-color: #e9ecef; cursor: not-allowed;">
        </div>

        <div class="form-group">
            <label for="email">Correo electrónico:</label>
            <input type="email" id="email" name="email" value="${sessionScope.usuarioLogueado.email}" readonly style="background-color: #e9ecef; cursor: not-allowed;">
        </div>

        <div class="form-group">
            <label for="nombre">Nombre:</label>
            <input type="text" id="nombre" name="nombre" value="${sessionScope.usuarioLogueado.nombre}" required>
        </div>

        <div class="form-group">
            <label for="apellido">Apellido:</label>
            <input type="text" id="apellido" name="apellido" value="${sessionScope.usuarioLogueado.apellido}" required>
        </div>

        <div class="form-group">
            <label for="fechaNacimiento">Fecha de Nacimiento:</label>
            <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="${sessionScope.usuarioLogueado.fNacimiento}" required>
        </div>

        <div class="form-group">
            <label>Imagen de Perfil:</label><br>
            <img id="vistaPreviaModificar" 
                 src="imagenes/${empty sessionScope.usuarioLogueado.imagen ? 'usr.png' : sessionScope.usuarioLogueado.imagen}" 
                 style="width: 80px; height: 80px; border-radius: 50%; object-fit: cover; margin-bottom: 10px; border: 1px solid #ccc;">
            <input type="file" id="imagen" name="imagen" accept=".jpg,.png">
        </div>

        <c:if test="${sessionScope.usuarioLogueado.tipoUsuario == 'DOCENTE'}">
            <div id="seccionInstitutosModificar" style="border: 1px solid #ddd; padding: 10px; margin-top: 15px; border-radius: 5px;">
                <label><strong>Institutos del Docente:</strong></label>
                
                <div style="display: flex; gap: 5px; margin-top: 5px;">
                    <select id="selectInstitutoModificar" style="flex: 1;">
                        <option value="">-- Seleccionar Instituto --</option>
                    </select>
                    <button type="button" id="btnAgregarInstitutoModificar">Agregar</button>
                </div>

                <div style="margin-top: 10px;">
                    <select id="institutosSeleccionadosModificar" name="institutos" multiple style="width: 100%; height: 80px;">
                        <c:forEach var="inst" items="${sessionScope.usuarioLogueado.institutos}">
                            <option value="${inst}" selected>${inst}</option>
                        </c:forEach>
                    </select>
                </div>
                <button type="button" id="btnQuitarInstitutoModificar" style="margin-top: 5px; background-color: #d9534f; color: white; border: none; padding: 3px 8px; cursor: pointer;">Quitar Seleccionado</button>
            </div>
        </c:if>

        <div class="form-actions" style="margin-top: 20px; text-align: center; display: flex; gap: 10px;">
            <button type="submit" style="flex: 1; background-color: #0275d8; color: white;">Guardar Cambios</button>
            <button type="button" onclick="cancelarOperacion()" style="flex: 1; background-color: #6c757d; color: white;">Cancelar</button>
        </div>
    </form>
</div>
