<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <!-- O "http://sun.com" en versiones viejas -->

<h3>📦 Catálogo de Productos Disponibles</h3>
<p>Esta tabla se generó dinámicamente consultando el Modelo Java.</p>

<table border="1" style="width:100%; margin-top:15px; border-collapse: collapse;">
    <tr style="background-color: #bdc3c7;">
        <th>ID</th>
        <th>Nombre del Producto</th>
        <th>Precio</th>
    </tr>
    <!-- Iteramos la lista que guardó el Servlet -->
    <c:forEach var="prod" items="${listaProductos}">
        <tr>
            <td>${prod.id}</td>
            <td>${prod.nombre}</td>
            <td>$${prod.precio}</td>
        </tr>
    </c:forEach>
</table>

