<%@ tag pageEncoding="UTF-8" body-content="empty" description="Puntaje de 0 a 5 dibujado con 5 estrellas (requerimiento 7.6)" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="valor" required="true" type="java.lang.Double" description="Puntaje de 0 a 5; admite decimales (3.5 pinta tres estrellas y media)" %>
<%@ attribute name="tamano" required="false" description="Tamaño de fuente de las estrellas, por ejemplo 16px" %>
<c:set var="puntaje" value="${empty valor ? 0 : (valor lt 0 ? 0 : (valor gt 5 ? 5 : valor))}"/>
<%-- Estrellas grises de fondo y, encima, las mismas estrellas doradas recortadas al porcentaje del puntaje --%>
<span class="estrellas" role="img" aria-label="Puntaje ${puntaje} de 5" title="${puntaje} de 5"
      style="position: relative; display: inline-block; font-size: ${empty tamano ? '18px' : tamano}; line-height: 1; color: #d5d8dc; white-space: nowrap; vertical-align: middle;"><span aria-hidden="true">&#9733;&#9733;&#9733;&#9733;&#9733;</span><span aria-hidden="true" style="position: absolute; left: 0; top: 0; overflow: hidden; width: ${puntaje * 20}%; color: #f5b301; white-space: nowrap;">&#9733;&#9733;&#9733;&#9733;&#9733;</span></span>
