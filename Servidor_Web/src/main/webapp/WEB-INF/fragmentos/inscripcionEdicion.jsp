<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenedor-formulario" style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50;">Inscripción a Edición de Curso</h2>
    <hr style="border: 1px solid #eee; margin-bottom: 20px;">
    
    <!-- Caja para mensajes de éxito o error -->
    <div id="mensaje-inscripcion" style="display: none; margin-bottom: 15px; padding: 12px; border-radius: 4px;"></div>

    <form id="formInscripcionEdicion" onsubmit="inscribirEdicion(event)">
         
        <!-- 1. El curso se busca por Instituto o por Categoría (letra 6.1) -->
        <div class="form-group">
            <span style="font-weight: bold;">1. Buscar el curso por:</span>
            <label style="margin-left: 12px; cursor: pointer;">
                <input type="radio" name="filtroInscripcion" value="instituto" checked onchange="cambiarFiltroInscripcion(this.value)"> Instituto
            </label>
            <label style="margin-left: 12px; cursor: pointer;">
                <input type="radio" name="filtroInscripcion" value="categoria" onchange="cambiarFiltroInscripcion(this.value)"> Categoría
            </label>
            <select id="instInscripcion" onchange="cargarCursosParaInscripcion(this.value)" required style="width: 100%; padding: 8px; margin-top: 8px;">
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="<c:out value='${inst.nombre}'/>"><c:out value="${inst.nombre}"/></option>
                </c:forEach>
            </select>
            <select id="catInscripcion" onchange="cargarCursosPorCategoriaParaInscripcion(this.value)" style="display: none; width: 100%; padding: 8px; margin-top: 8px;">
                <option value="">Seleccione una Categoría...</option>
                <c:forEach var="cat" items="${categorias}">
                    <option value="<c:out value='${cat.nombre}'/>"><c:out value="${cat.nombre}"/></option>
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

        <!-- 3. Edición vigente del curso, con sus datos básicos (Habilitado por AJAX) -->
        <div class="form-group" style="margin-top: 15px;">
            <label for="edicionInscripcion" style="font-weight: bold;">3. Seleccione la Edición vigente:</label>
            <select id="edicionInscripcion" name="edicion" onchange="mostrarDatosEdicionInscripcion(this.value)" required disabled style="width: 100%; padding: 8px;">
                <option value="">Primero seleccione un curso</option>
            </select>
            <div id="datos-edicion-inscripcion" style="display: none; margin-top: 10px; padding: 10px 12px; background: #f4f8fb; border: 1px solid #d6e4f0; border-radius: 4px; font-size: 14px; color: #2c3e50; line-height: 1.6;"></div>
        </div>

        <!-- 4. Video de Motivación (Opcional) -->
        <div class="form-group" style="margin-top: 15px;">
            <label for="videoURL" style="font-weight: bold;">4. URL del Video de Motivación (opcional):</label>
            <input type="url" id="videoURL" name="videoURL" placeholder="https://www.youtube.com/watch?v=..." style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
        </div>

        <div class="form-actions" style="margin-top: 25px; text-align: right;">
            <button type="submit" style="background-color: #3498db; color: white; padding: 10px 20px; border: none; cursor: pointer; font-weight: bold; border-radius: 4px;">Confirmar Inscripción</button>
        </div>
    </form>
</div>
