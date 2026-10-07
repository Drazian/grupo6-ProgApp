<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Programa Detalles</title>
    </head>
    <body>
        <div class="contenedor-detalle-programa">
            <div class="encabezado-detalle">
                <button class="btn-volver" onclick="cargarProgramas()">← Volver a Programas</button>
                <h2>${programa.nombre}</h2>
            </div>

            <!-- PROGRAMA -->
            <div class="seccion-principal-programa">
                <div class="info-basica">
                    <p class="descripcion-programa">
                        <c:out value="${programa.descripcion}" default="Sin descripción disponible." />
                    </p>

                    <div class="fechas-programa">
                        <p><strong>Fecha de Inicio:</strong> ${programa.fechaInicio}</p>
                        <p><strong>Fecha de Fin:</strong> ${programa.fechaFin}</p>
                        <p><strong>Fecha de Alta:</strong> ${programa.fechaRegistro}</p>
                    </div>
                    
                    
                    <div class="categorias-programa">
                        <strong>Categorias:</strong>
                        <c:choose>
                            <c:when test="${not empty programa.getCategorias()}">
                                <c:forEach var="categoria" items="${programa.getCategorias()}">
                                    <span class="badge-categoria"><c:out value="${categoria}"/></span>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <span class="texto-vacio">Sin categorias asociadas</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    
                    
                    
                    
                    
                    
                    
                </div>
            </div>

            <!-- CURSOS -->
            <div class="seccion-cursos-integrantes">
                <br><h3>Cursos del Programa</h3><br>
                <c:choose>
                    <c:when test="${not empty programa.getDtCursos()}">
                        <ul class="lista-cursos-programa">
                            <c:forEach var="curso" items="${programa.getDtCursos()}">
                                <li class="item-curso-integrante">
                                    <span class="nombre-curso">${curso.nombre}</span>
                                    <button class="btn-detalle-curso" onclick="verDetalleCurso('${curso.nombre}')">
                                        Consultar Curso
                                    </button>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <div class="alerta-info">
                            Este programa aún no incluye cursos asociados.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </body>
</html>
