<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<h3>➕ Registrar Nuevo Producto</h3>

<!-- Nota que no tiene el atributo 'action', lo manejaremos con JavaScript -->
<form id="form-registro" onsubmit="guardarProducto(event)" style="margin-top: 15px;">
    <label>Nombre:</label><br>
    <input type="text" name="nombreProd" required><br><br>
    
    <label>Precio:</label><br>
    <input type="number" step="0.01" name="precioProd" required><br><br>
    
    <button type="submit">Guardar Producto</button>
</form>

<div id="resultado-registro" style="margin-top:15px;"></div>

