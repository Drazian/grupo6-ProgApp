<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="max-width: 900px; margin: 0 auto; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
    
    <h2 style="margin-top: 0; color: #333; font-size: 24px;">Agregar Curso a Programa de Formación</h2>
    <hr style="opacity: 0.3; margin: 15px 0 20px 0;">

    <form id="formAgregarCursoPrograma"> 
        
        <!-- Selección de Programa -->
        <div style="margin-bottom: 20px;">
            <label for="selectPrograma" style="display: block; font-weight: bold; margin-bottom: 8px; color: #444; font-size: 14px;">Programa de Formación:</label>
            <select id="selectPrograma" name="nombrePrograma" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; background: #fff; font-size: 14px;">
                <option value="" disabled selected>-- Seleccione un Programa --</option>
                <c:forEach var="prog" items="${programas}">
                    <option value="${prog.getNombre()}">${prog.getNombre()}</option>
                </c:forEach>
            </select>
        </div>

        <!-- Selección de Curso -->
        <div style="margin-bottom: 20px;">
            <label style="display: block; font-weight: bold; margin-bottom: 8px; color: #444; font-size: 14px;">Seleccione el Curso:</label>
            
            <!-- Input oculto para que el servlet lo reciba -->
            <input type="hidden" id="selectCurso" name="nombreCurso" required />

            <!-- Tabla de Cursos con Scroll -->
            <div style="max-height: 320px; overflow-y: auto; border: 1px solid #dee2e6; border-radius: 6px;">
                <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
                    <thead>
                        <tr style="background-color: #f1f3f5; border-bottom: 2px solid #dee2e6; color: #333; position: sticky; top: 0; z-index: 1;">
                            <th style="padding: 10px; width: 60px; text-align: center;">Imagen</th>
                            <th style="padding: 10px;">Nombre</th>
                            <th style="padding: 10px;">Instituto</th>
                            <th style="padding: 10px; width: 110px; text-align: center;">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cur" items="${cursos}">
                            <tr id="fila-curso-${cur.getNombre()}" 
                                style="border-bottom: 1px solid #eee; transition: background 0.15s;"
                                onmouseover="this.style.backgroundColor='#fcfcfc';" 
                                onmouseout="this.style.backgroundColor='transparent';">
                                
                                <td style="padding: 10px; text-align: center;">
                                    <img src="ImagenServlet?nombre=${empty cur.imagen ? 'usr.png' : cur.imagen}" 
                                         alt="Curso" 
                                         style="width: 38px; height: 38px; object-fit: cover; border-radius: 50%; border: 1px solid #ccc;">
                                </td>
                                <td style="padding: 10px; font-weight: 500; color: #2c3e50;">
                                    ${cur.getNombre()}
                                </td>
                                <td style="padding: 10px; color: #666;">
                                    ${cur.getInstituto() != null ? cur.getInstituto().getNombre() : 'Sin instituto'}
                                </td>
                                <td style="padding: 10px; text-align: center;">
                                    <button type="button" class="btn-seleccionar-curso" data-nombre="${cur.getNombre()}"
                                            style="background-color: #f8f9fa; color: #0275d8; border: 1px solid #0275d8; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px; font-weight: bold; transition: all 0.2s;"
                                            onmouseover="this.style.backgroundColor='#0275d8'; this.style.color='#fff';" 
                                            onmouseout="this.style.backgroundColor='#f8f9fa'; this.style.color='#0275d8';">
                                        Seleccionar
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Indicador de Curso Seleccionado -->
            <div style="margin-top: 12px; padding: 10px; background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 4px; display: flex; align-items: center; gap: 8px; font-size: 14px;">
                <span style="color: #555;">Curso seleccionado:</span>
                <span id="labelCursoSeleccionado" style="font-weight: bold; color: #0275d8;">Ninguno seleccionado</span>
            </div>
        </div>

        <!-- Botón de Envío -->
        <div style="text-align: right; margin-top: 25px;">
            <button type="button" onclick="agregarCursoAPrograma()"
                    style="background-color: #5cb85c; color: white; border: none; padding: 10px 22px; font-weight: bold; cursor: pointer; border-radius: 4px; font-size: 15px; transition: background 0.2s;"
                    onmouseover="this.style.backgroundColor='#4cae4c';" 
                    onmouseout="this.style.backgroundColor='#5cb85c';">
                Agregar Curso
            </button>
        </div>

    </form>
</div>