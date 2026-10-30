<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div style="max-width: 800px; margin: 0 auto; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
    
    <h2 style="margin-top: 0; color: #333; font-size: 24px;">Programas de Formación</h2>
    <p style="color: #666; font-size: 14px; margin-top: 5px; margin-bottom: 0;">Selecciona un programa para consultar sus datos.</p>
    <hr style="opacity: 0.3; margin: 15px 0 20px 0;">
 
    <c:choose>
        <c:when test="${not empty listaProgramas}">
            <ul style="list-style: none; padding: 0; margin: 0; border: 1px solid #dee2e6; border-radius: 6px; overflow: hidden;">
                <c:forEach var="prog" items="${listaProgramas}">
                    <li style="display: flex; justify-content: space-between; align-items: center; padding: 14px 18px; border-bottom: 1px solid #eee; transition: background 0.15s; background-color: #fff;"
                        onmouseover="this.style.backgroundColor='#fcfcfc';" 
                        onmouseout="this.style.backgroundColor='#fff';">
                        
                        <span style="font-weight: bold; color: #2c3e50; font-size: 15px;">
                            📚 ${prog.getNombre()}
                        </span>
                        
                        <button type="button" data-programa="${fn:escapeXml(prog.getNombre())}" onclick="cargarDetallePrograma(this.dataset.programa)"
                                style="background-color: #0275d8; color: white; border: none; padding: 7px 14px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: bold; transition: background 0.2s;"
                                onmouseover="this.style.backgroundColor='#025aa5';" 
                                onmouseout="this.style.backgroundColor='#0275d8';">
                            Ver detalle
                        </button>
                    </li>
                </c:forEach>
            </ul>
        </c:when>

        <c:otherwise>
            <div style="background-color: #e2e3e5; color: #383d41; padding: 12px 15px; border-radius: 6px; border: 1px solid #d6d8db; font-size: 14px;">
                No hay programas de formación registrados actualmente.
            </div>
        </c:otherwise>
    </c:choose>

</div>