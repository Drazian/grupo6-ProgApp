<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="contenedor-formulario" style="max-width: 550px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff;">
    <h2>Modificar Perfil</h2>
    <hr style="margin-bottom: 20px;"> 

    <div id="mensaje-error-modificar" style="display:none; background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px;"></div>

    <form id="formModificarUsuario" enctype="multipart/form-data">
        
        <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Nickname:</label>
            <input type="text" value="${fn:escapeXml(sessionScope.usuarioLogueado.nickname)}" readonly disabled style="width: 100%; padding: 8px; background-color: #e9ecef; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Correo Electrónico:</label>
            <input type="email" value="${fn:escapeXml(sessionScope.usuarioLogueado.email)}" readonly disabled style="width: 100%; padding: 8px; background-color: #e9ecef; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="nombre" style="display: block; font-weight: bold; margin-bottom: 5px;">Nombre:</label>
            <input type="text" id="nombre" name="nombre" value="${fn:escapeXml(sessionScope.usuarioLogueado.nombre)}" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="apellido" style="display: block; font-weight: bold; margin-bottom: 5px;">Apellido:</label>
            <input type="text" id="apellido" name="apellido" value="${fn:escapeXml(sessionScope.usuarioLogueado.apellido)}" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="fechaNacimiento" style="display: block; font-weight: bold; margin-bottom: 5px;">Fecha de Nacimiento:</label>
            <input type="date" id="fechaNacimiento" name="fechaNacimiento" value="${fn:escapeXml(sessionScope.usuarioLogueado.fNacimiento)}" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <!-- SECCIÓN EXCLUSIVA PARA DOCENTES: Gestión de Institutos -->
        <c:if test="${sessionScope.usuarioLogueado.tipoUsuario == 'DOCENTE'}">
            <div id="seccionInstitutosModificar" style="margin-bottom: 20px; border-top: 1px dashed #ccc; padding-top: 15px;">
                <label style="display: block; font-weight: bold; margin-bottom: 5px;">Institutos Asignados:</label>
                
                <div style="display: flex; gap: 10px; align-items: center; margin-bottom: 10px;">
                    <select id="institutoModificar" style="flex: 1; padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        <option value="">Seleccione un instituto para agregar...</option>
                    </select>
                    <button type="button" id="agregarInstitutoModificar" style="padding: 8px 12px; background-color: #0275d8; color: white; border: none; border-radius: 4px; cursor: pointer;">Agregar</button>
                </div>

                <div style="margin-top: 10px;">
                    <select id="institutosSeleccionadosModificar" multiple style="width: 100%; height: 100px; padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                        <c:forEach var="inst" items="${sessionScope.usuarioLogueado.institutos}">
                            <option value="${fn:escapeXml(inst)}">${inst}</option>
                        </c:forEach>
                    </select>
                    <button type="button" id="quitarInstitutoModificar" style="margin-top: 5px; padding: 6px 12px; background-color: #d9534f; color: white; border: none; border-radius: 4px; cursor: pointer;">Quitar Selección</button>
                </div>
            </div>
        </c:if>

        <div style="margin-bottom: 15px;">
            <label for="password" style="display: block; font-weight: bold; margin-bottom: 5px;">Nueva Contraseña (opcional):</label>
            <input type="password" id="password" name="password" placeholder="Dejar en blanco si no se desea cambiar" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label for="confirmarPassword" style="display: block; font-weight: bold; margin-bottom: 5px;">Repetir Contraseña:</label>
            <input type="password" id="confirmarPassword" name="confirmarPassword" placeholder="Repita la nueva contraseña" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Imagen de Perfil:</label>
            <div style="display: flex; align-items: center; gap: 15px; margin-top: 5px;">
                <img id="vistaPreviaModificar" 
                    src="ImagenServlet?nombre=${empty sessionScope.usuarioLogueado.imagen ? 'usr.png' : sessionScope.usuarioLogueado.imagen}" 
                    alt="Foto de perfil" 
                    style="width: 80px; height: 80px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">
                               <input type="file" id="imagen" name="imagen" accept="image/*" />
            </div>
        </div>

        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" style="background-color: #5cb85c; color: white; border: none; padding: 10px 20px; font-weight: bold; cursor: pointer; border-radius: 4px;">Guardar Cambios</button>
        </div>

    </form>
</div>