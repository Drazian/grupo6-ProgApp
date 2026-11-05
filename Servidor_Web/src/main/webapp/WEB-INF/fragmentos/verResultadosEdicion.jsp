<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- CU: Ver resultados de evaluaciones en ediciones de cursos (Docente) --%>
<div style="max-width: 900px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #8e44ad; padding-bottom: 10px;">
        Resultados de Evaluaciones por Edición de Curso
    </h2>
    <p style="color: #7f8c8d; font-size: 13px; margin: 10px 0 15px 0;">
        Solo se listan las ediciones <strong>cerradas</strong> en las que usted participa como docente.
    </p>

    <!-- Filtros Superiores en Cascada: Instituto -> Curso -> Edición cerrada -->
    <div style="display: flex; gap: 15px; margin-bottom: 20px; flex-wrap: wrap;">
        <div style="flex: 1; min-width: 220px;">
            <label for="instResultados" style="font-weight: bold; color: #34495e;">Instituto:</label>
            <select id="instResultados" onchange="cargarCursosParaResultados(this.value)" style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Seleccione un Instituto...</option>
                <c:forEach var="inst" items="${institutos}">
                    <option value="${fn:escapeXml(inst.nombre)}"><c:out value="${inst.nombre}"/></option>
                </c:forEach>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label for="cursoResultados" style="font-weight: bold; color: #34495e;">Curso:</label>
            <select id="cursoResultados" onchange="cargarEdicionesParaResultados(this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un instituto</option>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label for="edicionResultados" style="font-weight: bold; color: #34495e;">Edición cerrada:</label>
            <select id="edicionResultados" onchange="cargarActaEdicion(this.value)" disabled style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #bdc3c7;">
                <option value="">Primero seleccione un curso</option>
            </select>
        </div>
    </div>

    <!-- Contenedor dinámico donde se inyectan los datos básicos de la edición y el acta -->
    <div id="contenedor-acta-resultados">
        <p style="text-align: center; color: #7f8c8d; font-style: italic; margin-top: 40px;">
            Seleccione un Instituto, un Curso y una Edición cerrada para visualizar el acta con los resultados de las evaluaciones.
        </p>
    </div>
</div>
