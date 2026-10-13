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
            
                    
            <div class="imagen">
                <img src="ImagenServlet?nombre=${empty programa.imagen ? 'usr.png' : programa.imagen}" 
                alt="Imagen del Programa" 
                style="width: 150px; height: 150px; object-fit: cover; border-radius: 4px;">
            </div>
                    

            <!-- CURSOS -->
            <div class="seccion-cursos-integrantes">
                <br><h3>Cursos del Programa</h3><br>
                <c:choose>
                    <c:when test="${not empty programa.getDtCursos()}">
                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th style="width: 80px;">Imagen</th>
                                        <th>Nombre del Curso</th>
                                        <th class="text-end">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="curso" items="${programa.getDtCursos()}">
                                        <tr>
                                            <td>
                                                <img src="ImagenServlet?nombre=${empty curso.imagen ? 'usr.png' : curso.imagen}" 
                                                     alt="Curso" 
                                                     style="width: 45px; height: 45px; object-fit: cover; border-radius: 50%; border: 1px solid #ccc;">
                                            </td>
                                            <td>
                                                <span style="font-weight: 500;">${curso.nombre}</span>
                                            </td>
                                            <td class="text-end">
                                                <button type="button" class="btn btn-sm btn-info" onclick="verDetallesCurso('${curso.nombre}')">
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
                        <div class="alerta-info">
                            Este programa aún no incluye cursos asociados.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </body>
</html>
