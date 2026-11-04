<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="max-width: 950px; margin: 0 auto; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
    
    <!-- Encabezado con Botón Volver y Título -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap; gap: 10px;">
        <h2 style="margin: 0; color: #333; font-size: 24px;">Detalles del Programa: ${programa.nombre}</h2>
        <button type="button" onclick="cargarProgramas()" 
                style="background-color: #6c757d; color: white; border: none; padding: 8px 14px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: bold; transition: background 0.2s;"
                onmouseover="this.style.backgroundColor='#5a6268';" 
                onmouseout="this.style.backgroundColor='#6c757d';">
            ← Volver a Programas
        </button>
    </div> 
    <hr style="opacity: 0.3; margin: 15px 0 20px 0;">

    <!-- Sección Principal: Info y Imagen -->
    <div style="display: flex; gap: 25px; align-items: flex-start; flex-wrap: wrap; margin-bottom: 25px;">
        
        <!-- Imagen del Programa -->
        <div style="flex-shrink: 0; text-align: center;">
            <img src="ImagenServlet?nombre=${empty programa.imagen ? 'usr.png' : programa.imagen}" 
                 alt="Imagen del Programa" 
                 style="width: 140px; height: 140px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc; background: #f8f9fa;">
        </div>

        <!-- Información Básica -->
        <div style="flex: 1; min-width: 280px;">
            <p style="margin-top: 0; color: #555; font-size: 15px; line-height: 1.5;">
                <c:out value="${programa.descripcion}" default="Sin descripción disponible." />
            </p>

            <div style="display: flex; gap: 20px; flex-wrap: wrap; margin: 15px 0; font-size: 14px; color: #444; background: #f8f9fa; padding: 12px; border-radius: 6px; border: 1px solid #e9ecef;">
                <div><strong>Inicio:</strong> ${programa.fechaInicio}</div>
                <div><strong>Fin:</strong> ${programa.fechaFin}</div>
                <div><strong>Alta:</strong> ${programa.fechaRegistro}</div>
            </div>
            
            <!-- Categorías -->
            <div style="margin-top: 12px;">
                <strong style="font-size: 14px; color: #333; margin-right: 8px;">Categorías:</strong>
                <c:choose>
                    <c:when test="${not empty programa.getCategorias()}">
                        <c:forEach var="categoria" items="${programa.getCategorias()}">
                            <span style="font-size: 11px; background: #e9ecef; padding: 4px 8px; border-radius: 12px; border: 1px solid #ced4da; font-weight: bold; color: #495057; display: inline-block; margin-right: 5px; margin-bottom: 4px; text-transform: uppercase;">
                                <c:out value="${categoria}"/>
                            </span>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <span style="color: #888; font-style: italic; font-size: 14px;">Sin categorías asociadas</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <hr style="opacity: 0.3; margin: 20px 0;">

    <!-- CURSOS -->
    <div>
        <h3 style="margin-top: 0; color: #333; font-size: 18px; margin-bottom: 15px;">Cursos del Programa</h3>
        
        <c:choose>
            <c:when test="${not empty programa.getCursos()}">
                <div style="overflow-x: auto; border: 1px solid #dee2e6; border-radius: 6px;">
                    <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
                        <thead>
                            <tr style="background-color: #f1f3f5; border-bottom: 2px solid #dee2e6; color: #333;">
                                <th style="padding: 10px; width: 70px; text-align: center;">Imagen</th>
                                <th style="padding: 10px;">Nombre del Curso</th>
                                <th style="padding: 10px; width: 130px; text-align: center;">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="curso" items="${programa.getCursos()}">
                                <tr style="border-bottom: 1px solid #eee; transition: background 0.15s;"
                                    onmouseover="this.style.backgroundColor='#fcfcfc';" 
                                    onmouseout="this.style.backgroundColor='transparent';">
                                    
                                    <td style="padding: 10px; text-align: center;">
                                        <img src="ImagenServlet?nombre=${empty curso.imagen ? 'usr.png' : curso.imagen}" 
                                             alt="Curso" 
                                             style="width: 38px; height: 38px; object-fit: cover; border-radius: 50%; border: 1px solid #ccc;">
                                    </td>
                                    
                                    <td style="padding: 10px; font-weight: bold; color: #2c3e50;">
                                        ${curso.nombre}
                                    </td>
                                    
                                    <td style="padding: 10px; text-align: center;">
                                        <button type="button" onclick="verDetallesCurso('${curso.nombre}')"
                                                style="background-color: #0275d8; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: bold; transition: background 0.2s;"
                                                onmouseover="this.style.backgroundColor='#025aa5';" 
                                                onmouseout="this.style.backgroundColor='#0275d8';">
                                            Consultar Curso
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="background-color: #e2e3e5; color: #383d41; padding: 12px 15px; border-radius: 6px; border: 1px solid #d6d8db; font-size: 14px;">
                    Este programa aún no incluye cursos asociados.
                </div>
            </c:otherwise>
        </c:choose>
    </div>

</div>