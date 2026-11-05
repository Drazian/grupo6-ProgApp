<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- CU: Ingresar resultados de evaluaciones de estudiantes en ediciones de cursos (Docente) --%>
<div style="max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #2980b9; padding-bottom: 10px;">
        Ingresar Resultados de Evaluaciones
    </h2>
    <p style="color: #7f8c8d; font-size: 13px; margin: 10px 0 15px 0;">
        Solo se listan las ediciones <strong>abiertas</strong> en las que usted participa como docente.
        Una vez cerrada la edición, las notas ya no se pueden modificar.
    </p>

    <!-- Filtros Superiores en Cascada: Instituto -> Curso -> Edición abierta -->
    <div style="display: flex; gap: 15px; margin-bottom: 20px; flex-wrap: wrap;">
        <div style="flex: 1; min-width: 220px;">
            <label for="instIngreso" style="font-weight: bold; color: #34495e;">Instituto:</label>
            <select id="instIngreso" onchange="cargarCursosResultados('ingresar', this.value)" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="${fn:escapeXml(inst.nombre)}"><c:out value="${inst.nombre}"/></option>
                </c:forEach>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label for="cursoIngreso" style="font-weight: bold; color: #34495e;">Curso:</label>
            <select id="cursoIngreso" onchange="cargarEdicionesResultados('ingresar', this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un instituto</option>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label for="edicionIngreso" style="font-weight: bold; color: #34495e;">Edición:</label>
            <select id="edicionIngreso" onchange="seleccionarEdicionResultados('ingresar', this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un curso</option>
            </select>
        </div>
    </div>

    <!-- Contenedor dinámico: datos básicos de la edición + planilla de notas -->
    <div id="contenedor-ingreso-resultados">
        <p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">
            Seleccione un Instituto, un Curso y una Edición para ingresar las notas de los estudiantes aceptados.
        </p>
    </div>
</div>
