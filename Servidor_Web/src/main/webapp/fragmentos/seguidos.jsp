<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<h2>Red Social - Seguidores y Seguidos</h2>

<div id="mensaje-error-social" class="alerta-error" style="display: none; color: red; margin-bottom: 15px;"></div>
<div id="mensaje-exito-social" class="alerta-exito" style="display: none; color: green; margin-bottom: 15px;"></div>

<!-- Formulario para Buscar y Seguir un Usuario -->
<fieldset style="margin-bottom: 20px; padding: 15px;">
    <legend><b>Seguir a un nuevo Usuario</b></legend>
    <form id="formSeguirUsuario" style="display: flex; gap: 10px; align-items: center;">
        <label for="nicknameASeguir">Nickname:</label>
        <input type="text" id="nicknameASeguir" name="nicknameASeguir" placeholder="Ej: juan123" required>
        <button type="submit" style="background-color: #0275d8; color: white; border: none; padding: 6px 12px; cursor: pointer; border-radius: 3px;">Seguir</button>
    </form>
</fieldset>

<div style="display: flex; gap: 30px;">
    
    <!-- Bloque 1: Usuarios que el usuario sigue -->
    <div style="flex: 1;">
        <fieldset style="padding: 15px;">
            <legend><b>Usuarios que Sigues</b></legend>
            <form id="formDejarSeguir">
                <select id="nicknameSeleccionado" name="nicknameSeleccionado" size="8" style="width: 100%; margin-bottom: 10px;" required>
                    <c:forEach var="seguido" items="${requestScope.listaSeguidos}">
                        <option value="${seguido}">${seguido}</option>
                    </c:forEach>
                </select>

                <button type="submit" style="background-color: #d9534f; color: white; border: none; padding: 6px 12px; cursor: pointer; border-radius: 3px; width: 100%;">
                    Dejar de seguir
                </button>
            </form>
        </fieldset>
    </div>

    <!-- Bloque 2: Usuarios que siguen al usuario -->
    <div style="flex: 1;">
        <fieldset style="padding: 15px;">
            <legend><b>Tus Seguidores</b></legend>
            <form id="formEliminarSeguidor">
                <select id="seguidorSeleccionado" name="seguidorSeleccionado" size="8" style="width: 100%; margin-bottom: 10px;" required>
                    <c:forEach var="seguidor" items="${requestScope.listaSeguidores}">
                        <option value="${seguidor}">${seguidor}</option>
                    </c:forEach>
                </select>

                <button type="submit" style="background-color: #f0ad4e; color: white; border: none; padding: 6px 12px; cursor: pointer; border-radius: 3px; width: 100%;">
                    Remover Seguidor
                </button>
            </form>
        </fieldset>
    </div>

</div>