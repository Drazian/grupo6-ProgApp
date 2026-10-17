<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="max-width: 1100px; margin: 0 auto; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
    
    <h2 style="margin-top: 0; color: #333; font-size: 24px;">Resultados de Búsqueda</h2>
    <hr style="opacity: 0.3; margin: 15px 0 20px 0;">

    <!-- Barra Superior de Filtros y Orden -->
    <div style="display: flex; gap: 15px; margin-bottom: 20px; flex-wrap: wrap; align-items: center; background: #f8f9fa; padding: 15px; border-radius: 6px; border: 1px solid #e9ecef;">
        
        <div style="flex: 1; min-width: 220px;">
            <label for="filtroTipo" style="display: block; font-weight: bold; margin-bottom: 5px; font-size: 13px; color: #555;">Filtrar por Tipo:</label>
            <select id="filtroTipo" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; background: #fff; font-size: 14px;">
                <option value="TODOS">Todos</option>
                <option value="curso">Cursos</option>
                <option value="programa">Programas de formación</option>
                <option value="usuario">Usuarios</option>
                <option value="docente">Docentes</option>
                <option value="estudiante">Estudiantes</option>
            </select>
        </div>

        <div style="flex: 1; min-width: 220px;">
            <label for="criterioOrden" style="display: block; font-weight: bold; margin-bottom: 5px; font-size: 13px; color: #555;">Ordenar por:</label>
            <select id="criterioOrden" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; background: #fff; font-size: 14px;">
                <option value="ALFABETICO">Alfabéticamente (A-Z)</option>
                <option value="FECHA">Fecha de publicación (Más recientes primero)</option>
            </select>
        </div>

    </div>
    
    <!-- Tabla de Resultados Moderna -->
    <div style="overflow-x: auto;">
        <table id="tablaResultados" style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
            <thead>
                <tr style="background-color: #f1f3f5; border-bottom: 2px solid #dee2e6; color: #333;">
                    <th style="padding: 12px; width: 60px; text-align: center;">Imagen</th>
                    <th style="padding: 12px; min-width: 180px;">Nombre</th>
                    <th style="padding: 12px; max-width: 450px;">Descripción</th>
                    <th style="padding: 12px; width: 120px;">Fecha</th>
                    <th style="padding: 12px; width: 100px; text-align: center;">Tipo</th>
                    <th style="padding: 12px; width: 110px; text-align: center;">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${resultadosBusqueda}">
                    <tr class="fila-resultado" 
                        data-tipo="${item.tipo}" 
                        data-nombre="${item.nombre}" 
                        data-fecha="${item.fechaPublicacion}"
                        style="border-bottom: 1px solid #eee; transition: background 0.15s;"
                        onmouseover="this.style.backgroundColor='#fcfcfc';" 
                        onmouseout="this.style.backgroundColor='transparent';">

                        <td style="padding: 12px 10px; text-align: center; vertical-align: top;">
                            <img src="ImagenServlet?nombre=${empty item.imagen ? 'usr.png' : item.imagen}" 
                                 alt="Imagen" 
                                 style="width: 38px; height: 38px; object-fit: cover; border-radius: 50%; border: 1px solid #ccc;">
                        </td>
                        
                        <td style="padding: 12px 10px; font-weight: bold; color: #2c3e50; vertical-align: top;">
                            ${item.nombre}
                        </td>

                        <td style="padding: 12px 10px; color: #555; max-width: 450px; vertical-align: top; word-break: break-word;" title="${item.descripcion}">
                            ${item.descripcion}
                        </td>

                        <td style="padding: 12px 10px; color: #666; font-size: 13px; vertical-align: top;">
                            ${item.fechaPublicacion}
                        </td>

                        <td style="padding: 12px 10px; text-align: center; vertical-align: top;">
                            <span style="font-size: 11px; background: #e9ecef; padding: 4px 8px; border-radius: 12px; border: 1px solid #ced4da; font-weight: bold; color: #495057; text-transform: uppercase; white-space: nowrap;">
                                ${item.tipo}
                            </span>
                        </td>

                        <td style="padding: 12px 10px; text-align: center; vertical-align: top;">
                            <button type="button" onclick="verDetallesBusqueda('${item.nombre}', '${item.tipo}')"
                                    style="background-color: #0275d8; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: bold; transition: background 0.2s; white-space: nowrap;"
                                    onmouseover="this.style.backgroundColor='#025aa5';" 
                                    onmouseout="this.style.backgroundColor='#0275d8';">
                                Ver Detalles
                            </button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</div>