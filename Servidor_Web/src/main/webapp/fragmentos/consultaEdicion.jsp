<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Ampliamos un poco el max-width para que la foto y la tabla respiren bien lado a lado -->
<div style="max-width: 980px; margin: 0 auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
    
    <h2 style="color: #2c3e50; margin-bottom: 5px;">Edición: ${edicion.nombre}</h2>
    <h4 style="color: #7f8c8d; margin-top: 0; margin-bottom: 25px;">Curso Asociado: ${edicion.curso.nombre}</h4>
    
    <!-- SECCIÓN SUPERIOR: Imagen a la izquierda, Estudiantes a la derecha -->
    <div style="display: flex; gap: 30px; flex-wrap: wrap; align-items: flex-start;">
        
        <!-- Izquierda: Portada (Ocupa hasta 400px) -->
        <div style="flex: 1; min-width: 300px; max-width: 400px;">
            <img src="ImagenServlet?nombre=${edicion.imagen}" alt="Portada" style="width: 100%; height: 250px; object-fit: cover; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
        </div>

        <!-- Derecha: Estudiantes Aceptados (Toma el resto del espacio) -->
        <div style="flex: 1.5; min-width: 350px;">
            <h4 style="color: #2c3e50; margin-top: 0; margin-bottom: 10px; border-bottom: 2px solid #f39c12; padding-bottom: 5px;">Estudiantes Aceptados</h4>
            
            <c:choose>
                <c:when test="${not empty inscripcionesAceptadas}">
                    <!-- Contenedor con scroll para que la tabla mida exactamente lo mismo que la imagen -->
                    <div style="max-height: 215px; overflow-y: auto; border: 1px solid #ecf0f1; border-radius: 6px;">
                        <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 13px;">
                            <thead style="position: sticky; top: 0; z-index: 1;">
                                <tr style="background-color: #34495e; color: white;">
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd; width: 40px; text-align: center;">Foto</th>
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd;">Estudiante</th>
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd;">Fecha</th>
                                    <th style="padding: 8px; border-bottom: 1px solid #ddd; text-align: center;">Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ins" items="${inscripcionesAceptadas}">
                                    <tr>
                                        <td style="padding: 8px; border-bottom: 1px solid #eee; background: #fff; text-align: center;">
                                            <img src="ImagenServlet?nombre=${empty ins.estudiante.imagen ? 'usr.png' : ins.estudiante.imagen}" 
                                                 alt="Avatar" 
                                                 style="width: 35px; height: 35px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc;">
                                        </td>
                                        <td style="padding: 8px; border-bottom: 1px solid #eee; background: #fff;">
                                            <strong style="color: #2c3e50;">${ins.estudiante.nombre} ${ins.estudiante.apellido}</strong><br>
                                            <span style="font-size: 11px; color: #7f8c8d;">@${ins.estudiante.nickname}</span>
                                        </td>
                                        <td style="padding: 8px; border-bottom: 1px solid #eee; background: #fff; font-size: 12px;">
                                            ${ins.fechaInscripcion}
                                        </td>
                                        <td style="padding: 8px; border-bottom: 1px solid #eee; background: #fff; text-align: center;">
                                            <span style="background: #27ae60; color: white; padding: 3px 8px; border-radius: 10px; font-size: 11px; font-weight: bold;">
                                                Aceptada
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Caja simétrica si está vacío -->
                    <div style="background: #fdf2e9; padding: 15px; border-left: 4px solid #e67e22; border-radius: 4px; height: 215px; display: flex; align-items: center; justify-content: center;">
                        <p style="margin: 0; color: #d35400; font-size: 13px; text-align: center;">No hay estudiantes aceptados en esta edición por el momento.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <hr style="border: 1px solid #eee; margin: 25px 0;">

    <!-- SECCIÓN INFERIOR: Datos Generales y Docentes -->
    <div style="display: flex; gap: 30px; flex-wrap: wrap;">
        
        <!-- Columna de Datos de la Edición -->
        <div style="flex: 1; min-width: 250px;">
            <h3 style="border-bottom: 2px solid #3498db; padding-bottom: 5px; margin-bottom: 15px; color: #2c3e50;">Datos Generales</h3>
            <ul style="list-style: none; padding: 0; line-height: 2.2; font-size: 14px;">
                <li><strong style="color: #34495e;">Fecha de Publicación:</strong> ${edicion.fechaPublicacion}</li>
                <li><strong style="color: #34495e;">Fecha de Inicio:</strong> ${edicion.fechaInicio}</li>
                <li><strong style="color: #34495e;">Fecha de Fin:</strong> ${edicion.fechaFin}</li>
                <li><strong style="color: #34495e;">Cupo Máximo:</strong> ${empty edicion.cupo ? 'Sin límite' : edicion.cupo}</li>
            </ul>
        </div> 

        <!-- Columna de Docentes -->
        <div style="flex: 1; min-width: 300px;">
            
            <!-- Título y Botón en la misma línea (Flexbox) -->
            <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2ecc71; padding-bottom: 5px; margin-bottom: 15px;">
                <h3 style="margin: 0; color: #2c3e50;">Docentes Asignados</h3>
                
                <!-- Agregar Docente -->
                <button type="button" onclick="cargarFormularioAgregarDocente('${edicion.nombre}')" 
                    style="background-color: #3498db; color: white; border: none; padding: 6px 12px; border-radius: 4px; font-size: 12px; cursor: pointer; transition: 0.2s; font-weight: bold; margin: 0;">
                    + Agregar Docente
                </button>
            </div>
            
            <c:choose>
                <c:when test="${not empty edicion.docentes}">
                    <div style="display: flex; flex-direction: column; gap: 12px;">
                        <c:forEach var="docente" items="${edicion.docentes}">
<div style="display: flex; align-items: center; justify-content: space-between; background: #f8f9fa; padding: 10px; border-radius: 6px; border: 1px solid #e9ecef; transition: background-color 0.2s;">
                                
                                <!-- Datos del Docente a la izquierda -->
                                <div style="display: flex; align-items: center;">
                                    <c:choose>
                                        <c:when test="${not empty docente.imagen}">
                                            <img src="ImagenServlet?nombre=${docente.imagen}" alt="Foto" style="width: 45px; height: 45px; border-radius: 50%; object-fit: cover; margin-right: 15px; border: 2px solid #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                        </c:when>
                                        <c:otherwise>
                                            <div style="width: 45px; height: 45px; border-radius: 50%; background: #bdc3c7; color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 16px; margin-right: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                                ${docente.nombre.substring(0,1)}${docente.apellido.substring(0,1)}
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <div>
                                        <strong style="display: block; color: #2c3e50; font-size: 14px;">${docente.nombre} ${docente.apellido}</strong>
                                        <span style="font-size: 12px; color: #7f8c8d;">@${docente.nickname}</span>
                                    </div>
                                </div>
                                
                                <!-- Botón de Quitar a la derecha -->
                                <button type="button" onclick="confirmarQuitarDocente('${edicion.nombre}', '${docente.nickname}', '${docente.nombre} ${docente.apellido}')" 
                                        style="background: transparent; color: #e74c3c; border: 1px solid #e74c3c; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 11px; font-weight: bold; transition: 0.2s;"
                                        onmouseover="this.style.background='#e74c3c'; this.style.color='white';"
                                        onmouseout="this.style.background='transparent'; this.style.color='#e74c3c';">
                                    Quitar
                                </button>                               
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #7f8c8d; font-style: italic; background: #f8f9fa; padding: 10px; border-radius: 6px; border: 1px solid #e9ecef;">No hay docentes asignados.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>