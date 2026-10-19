<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenedor-formulario" style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50;">Inscripción a Edición de Curso</h2>
    <hr style="border: 1px solid #eee; margin-bottom: 20px;">
    
    <!-- Caja para mensajes de éxito o error -->
    <div id="mensaje-inscripcion" style="display: none; margin-bottom: 15px; padding: 12px; border-radius: 4px;"></div>

    <form id="formInscripcionEdicion" onsubmit="inscribirEdicion(event)">
         
        <!-- 1. Selección de Instituto -->
        <div class="form-group">
            <label for="instInscripcion" style="font-weight: bold;">1. Seleccione el Instituto:</label>
            <select id="instInscripcion" onchange="cargarCursosParaInscripcion(this.value)" required style="width: 100%; padding: 8px;">
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="${inst.nombre}">${inst.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 2. Selección de Curso (Habilitado por AJAX) -->
        <div class="form-group" style="margin-top: 15px;">
            <label for="cursoInscripcion" style="font-weight: bold;">2. Seleccione el Curso:</label>
            <select id="cursoInscripcion" onchange="cargarEdicionesParaInscripcion(this.value)" required disabled style="width: 100%; padding: 8px;">
                <option value="">Primero seleccione un instituto</option>
            </select>
        </div>

        <!-- 3. Selección de Edición Vigente (Habilitado por AJAX) -->
        <div class="form-group" style="margin-top: 15px;">
            <label for="edicionInscripcion" style="font-weight: bold;">3. Seleccione la Edición:</label>
            <select id="edicionInscripcion" name="edicion" required disabled style="width: 100%; padding: 8px;">
                <option value="">Primero seleccione un curso</option>
            </select>
        </div>

        <div class="form-actions" style="margin-top: 25px; text-align: right;">
            <button type="submit" style="background-color: #3498db; color: white; padding: 10px 20px; border: none; cursor: pointer; font-weight: bold; border-radius: 4px;">Confirmar Inscripción</button>
        </div>
    </form>
</div>