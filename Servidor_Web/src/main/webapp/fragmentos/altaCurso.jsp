<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="contenedor-formulario">
    <h2>Alta de Curso</h2>
    <hr>
    
    <!-- Div para mostrar mensajes de error desde JavaScript -->
    <div id="mensaje-error" style="color: red; display: none; margin-bottom: 15px;"></div>

    <form id="formAltaCurso">
        
        <div class="form-group">
            <label for="instituto">Instituto que lo brinda:</label>
            <select id="instituto" name="instituto" required>
                <option value="" disabled selected>Seleccione un Instituto...</option>
                <!-- Itera sobre la lista de DtInstituto -->
                <c:forEach var="inst" items="${institutos}">
                    <option value="${inst.nombre}">${inst.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="nombre">Nombre del Curso:</label>
            <input type="text" id="nombre" name="nombre" required placeholder="Ej: Programación Avanzada">
        </div>

        <div class="form-group">
            <label for="descripcion">Descripción:</label>
            <textarea id="descripcion" name="descripcion" rows="3" required></textarea>
        </div>

        <div class="form-group">
            <label for="duracion">Duración:</label>
            <input type="text" id="duracion" name="duracion" required placeholder="Ej: 3 meses">
        </div>

        <div class="form-group">
            <label for="cantidadHoras">Cantidad de Horas:</label>
            <input type="number" id="cantidadHoras" name="cantidadHoras" required min="1">
        </div>

        <div class="form-group">
            <label for="creditos">Créditos:</label>
            <input type="number" id="creditos" name="creditos" required min="0">
        </div>

        <div class="form-group">
            <label for="url">URL Asociada:</label>
            <input type="url" id="url" name="url" required placeholder="https://...">
        </div>

        <div class="form-group">
            <label for="categorias">Categorías (Múltiple):</label>
            <br><small>Mantén presionado Ctrl (Windows) o Cmd (Mac) para seleccionar varias.</small><br>
            <!-- Select múltiple obligatorio (requiere al menos 1 categoría) -->
            <select id="categorias" name="categorias" multiple required size="5">
                <!-- Itera sobre la lista de DtCategoria -->
                <c:forEach var="cat" items="${categorias}">
                    <option value="${cat.nombre}">${cat.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="previas">Cursos Previos (Múltiple, Opcional):</label>
            <br><small>Mantén presionado Ctrl (Windows) o Cmd (Mac) para seleccionar varias.</small><br>
            <!-- Select múltiple opcional (las previas pueden ser ninguna) -->
            <select id="previas" name="previas" multiple size="5">
                <!-- Itera sobre la lista de Strings de nombres de cursos -->
                <c:forEach var="curso" items="${cursosPrevios}">
                    <option value="${curso}">${curso}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-actions" style="margin-top: 20px;">
            <!-- El evento onclick llama a tu futura función en app.js -->
            <button type="button" onclick="altaCurso(event)">Dar de Alta</button>
            <button type="button" onclick="cancelarOperacion()">Cancelar</button>
        </div>
        
    </form>
</div>