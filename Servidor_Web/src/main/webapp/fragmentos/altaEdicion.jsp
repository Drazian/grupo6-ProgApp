<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenedor-formulario" style="max-width: 600px; margin: 0 auto;">
    <h2>Alta de Edición de Curso</h2>
    <hr>
    
    <div id="mensaje-error-edicion" style="color: red; display: none; margin-bottom: 15px; padding: 10px; border: 1px solid red; background: #ffeeee;"></div>

    <form id="formAltaEdicion" onsubmit="altaEdicion(event)">
        <!-- 1. Selección de Instituto -->
        <div class="form-group">
            <label for="institutoEdicion">Instituto que lo brinda:</label>
            <select id="institutoEdicion" name="instituto" onchange="cargarCursosDeInstituto(this.value)" required>
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="${inst.nombre}">${inst.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 2. Selección de Curso (Se habilita dinámicamente) -->
        <div class="form-group">
            <label for="cursoEdicion">Curso Asociado:</label>
            <select id="cursoEdicion" name="curso" required disabled>
                <option value="">Primero seleccione un instituto</option>
            </select>
        </div>

        <!-- 3. Datos básicos de la edición -->
        <div class="form-group">
            <label for="nombreEdicion">Nombre de la Edición:</label>
            <input type="text" id="nombreEdicion" name="nombre" required placeholder="Ej: Edición 2026 - Semestre 1">
        </div>

        <div style="display: flex; gap: 15px;">
            <div class="form-group" style="flex: 1;">
                <label for="fechaInicio">Fecha de Inicio:</label>
                <input type="date" id="fechaInicio" name="fechaInicio" required>
            </div>
            <div class="form-group" style="flex: 1;">
                <label for="fechaFin">Fecha de Fin:</label>
                <input type="date" id="fechaFin" name="fechaFin" required>
            </div>
        </div>

        <div class="form-group">
            <label for="cupo">Cupo Máximo (Opcional):</label>
            <input type="number" id="cupo" name="cupo" min="1" placeholder="Dejar vacío si no hay límite">
        </div>

        <div class="form-group">
            <label for="docentes">Docentes Participantes (Múltiple):</label>
            <small style="display: block; margin-bottom: 5px; color: #666;">Mantén presionado Ctrl (Windows) o Cmd (Mac) para seleccionar varios.</small>
            <select id="docentes" name="docentes" multiple required style="height: 120px;">
                <c:forEach var="doc" items="${docentes}">
                    <option value="${doc}">${doc}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-actions" style="margin-top: 20px;">
            <button type="submit" style="background-color: #5cb85c; color: white; padding: 10px 15px; border: none; cursor: pointer;">Crear Edición</button>
            <!-- Permite cancelar y limpiar la pantalla -->
            <button type="button" onclick="document.getElementById('formAltaEdicion').reset();" style="background-color: #ccc; padding: 10px 15px; border: none; cursor: pointer;">Cancelar</button>
        </div>
    </form>
</div>