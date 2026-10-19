<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div style="max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #27ae60; padding-bottom: 10px;">
        Estudiantes Aceptados por Edición de Curso
    </h2>
 
    <!-- Filtros Superiores en Cascada: Instituto -> Curso -> Edición -->
    <div style="display: flex; gap: 15px; margin-bottom: 20px; flex-wrap: wrap;">
        <div style="flex: 1; min-width: 220px;">
            <label style="font-weight: bold; color: #34495e;">Instituto:</label>
            <select id="instAceptados" onchange="cargarCursosParaAceptados(this.value)" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="${inst.nombre}">${inst.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label style="font-weight: bold; color: #34495e;">Curso:</label>
            <select id="cursoAceptados" onchange="cargarEdicionesParaAceptados(this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un instituto</option>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label style="font-weight: bold; color: #34495e;">Edición:</label>
            <select id="edicionAceptados" onchange="cargarTablaAceptados(this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un curso</option>
            </select>
        </div>
    </div>

    <!-- Contenedor dinámico donde se inyectan los datos básicos de la edición y la tabla de aceptados -->
    <div id="contenedor-tabla-aceptados">
        <p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">
            Seleccione un Instituto, un Curso y una Edición para visualizar sus datos básicos y los estudiantes aceptados.
        </p>
    </div>
</div>