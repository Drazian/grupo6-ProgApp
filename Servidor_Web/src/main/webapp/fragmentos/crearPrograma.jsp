<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="max-width: 600px; margin: 0 auto; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
    
    <h2 style="margin-top: 0; color: #333; font-size: 24px;">Crear Programa de Formación</h2>
    <hr style="opacity: 0.3; margin: 15px 0 20px 0;">

    <form id="formCrearPrograma" enctype="multipart/form-data">
         
        <!-- Nombre -->
        <div style="margin-bottom: 15px;">
            <label for="nombre" style="display: block; font-weight: bold; margin-bottom: 5px; color: #444; font-size: 14px;">Nombre:</label>
            <input type="text" id="nombre" name="nombre" placeholder="Programita" required 
                   style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
        </div>

        <!-- Descripción -->
        <div style="margin-bottom: 15px;">
            <label for="desc" style="display: block; font-weight: bold; margin-bottom: 5px; color: #444; font-size: 14px;">Descripción:</label>
            <textarea id="desc" name="descripcion" placeholder="Ejemplito" required rows="4"
                      style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; resize: vertical; box-sizing: border-box;"></textarea>
        </div>

        <!-- Fechas (Inicio y Fin en una fila flexible) -->
        <div style="display: flex; gap: 15px; margin-bottom: 15px; flex-wrap: wrap;">
            <div style="flex: 1; min-width: 220px;">
                <label for="fechaInicio" style="display: block; font-weight: bold; margin-bottom: 5px; color: #444; font-size: 14px;">Fecha de Inicio:</label>
                <input type="date" id="fechaInicio" name="fechaInicio" required 
                       style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
            </div>
            <div style="flex: 1; min-width: 220px;">
                <label for="fechaFin" style="display: block; font-weight: bold; margin-bottom: 5px; color: #444; font-size: 14px;">Fecha de Fin:</label>
                <input type="date" id="fechaFin" name="fechaFin" required 
                       style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; box-sizing: border-box;" />
            </div>
        </div>

        <!-- Imagen del Programa y Vista Previa -->
        <div style="margin-bottom: 20px;">
            <label for="imagen" style="display: block; font-weight: bold; margin-bottom: 5px; color: #444; font-size: 14px;">Imagen del Programa:</label>
            <input type="file" id="imagen" name="imagen" accept=".jpg, .png" 
                   style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; background: #fafafa; font-size: 14px; box-sizing: border-box; margin-bottom: 12px;" />
            
            <div style="display: flex; align-items: center; gap: 15px;">
                <img id="vistaPrevia" src="imagenes/usr.png" alt="Vista previa" width="100" height="100" 
                     style="border-radius: 6px; object-fit: cover; border: 1px solid #ccc; background: #f8f9fa;" />
                <span style="font-size: 13px; color: #666;">Vista previa de la imagen seleccionada.</span>
            </div>
        </div>

        <!-- Botón de Guardar -->
        <div style="text-align: right; margin-top: 25px;">
            <button type="button" onclick="crearPrograma()"
                    style="background-color: #5cb85c; color: white; border: none; padding: 10px 22px; font-weight: bold; cursor: pointer; border-radius: 4px; font-size: 15px; transition: background 0.2s;"
                    onmouseover="this.style.backgroundColor='#4cae4c';" 
                    onmouseout="this.style.backgroundColor='#5cb85c';">
                Guardar
            </button>
        </div>

    </form>
</div>