<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Busqueda</title>
    </head>
    <body>
        <h1>Resultados de busqueda:</h1>
        
        <!-- Filtro de Tipo -->
        <select id="filtroTipo">
            <option value="TODOS">Todos</option>
            <option value="curso">Cursos</option>
            <option value="programa">Programas de formación</option>
            <option value="usuario">Usuarios</option>
            <option value="docente">Docentes</option>
            <option value="estudiante">Estudiantes</option>
        </select>

        <!-- Criterio de Orden -->
        <select id="criterioOrden">
            <option value="ALFABETICO">Alfabéticamente (A-Z)</option>
            <option value="FECHA">Fecha de publicación (Más recientes primero)</option>
        </select>
        
        <table id="tablaResultados" class="table">
            <thead>
                <tr>
                    <th>Imagen</th>
                    <th>Nombre</th>
                    <th>Descripción</th>
                    <th>Fecha Publicación</th>
                    <th>Tipo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${resultadosBusqueda}">
                    <tr class="fila-resultado" 
                        data-tipo="${item.tipo}" 
                        data-nombre="${item.nombre}" 
                        data-fecha="${item.fechaPublicacion}">

                        <td>
                            <img src="ImagenServlet?nombre=${empty item.imagen ? 'usr.png' : item.imagen}" 
                                 alt="Imagen" 
                                 style="width: 40px; height: 40px; object-fit: cover; border-radius: 50%; border: 1px solid #ccc;">
                        </td>
                        
                        <td>${item.nombre}</td>
                        <td>${item.descripcion}</td>
                        <td>${item.fechaPublicacion}</td>
                        <td><span class="badge">${item.tipo}</span></td>
                        <td>
                            <button type="button" class="btn btn-sm btn-info" onclick="verDetallesBusqueda('${item.nombre}', '${item.tipo}')">
                                Ver Detalles
                            </button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        
        
        
        
        
        
        
        
    </body>
</html>
