<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div style="max-width: 900px; margin: 0 auto; padding: 25px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
    
    <h2 style="margin-top: 0; color: #333; font-size: 24px;">Resultados para: <span style="color: #0275d8; font-weight: normal;">${filtroAplicado}</span></h2>
    <hr style="opacity: 0.3; margin: 15px 0 20px 0;">
    
    <!-- SECCIÓN DE CURSOS -->
    <div style="margin-bottom: 25px;">
        <h3 style="color: #444; font-size: 18px; margin-bottom: 12px; border-bottom: 2px solid #f1f3f5; padding-bottom: 6px;">Cursos</h3>
        
        <c:choose>
            <c:when test="${not empty listaCursos}">
                <ul style="list-style: none; padding: 0; margin: 0; border: 1px solid #dee2e6; border-radius: 6px; overflow: hidden;">
                    <c:forEach var="curso" items="${listaCursos}">
                        <li style="display: flex; justify-content: space-between; align-items: center; padding: 12px 18px; border-bottom: 1px solid #eee; transition: background 0.15s; background-color: #fff;"
                            onmouseover="this.style.backgroundColor='#fcfcfc';" 
                            onmouseout="this.style.backgroundColor='#fff';">
                            
                            <span style="font-weight: bold; color: #2c3e50; font-size: 15px;">
                                📘 ${curso}
                            </span>
                            
                            <button type="button" onclick="verDetallesCurso('${curso}')"
                                    style="background-color: #0275d8; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: bold; transition: background 0.2s;"
                                    onmouseover="this.style.backgroundColor='#025aa5';" 
                                    onmouseout="this.style.backgroundColor='#0275d8';">
                                Ver Curso
                            </button>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <div style="background-color: #f8f9fa; color: #666; padding: 10px 15px; border-radius: 6px; border: 1px solid #e9ecef; font-size: 14px;">
                    No hay cursos registrados para esta selección.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
        
    <!-- SECCIÓN DE PROGRAMAS -->
    <div>
        <h3 style="color: #444; font-size: 18px; margin-bottom: 12px; border-bottom: 2px solid #f1f3f5; padding-bottom: 6px;">Programas de Formación</h3>
        
        <c:choose>
            <c:when test="${not empty listaProgramas}">
                <ul style="list-style: none; padding: 0; margin: 0; border: 1px solid #dee2e6; border-radius: 6px; overflow: hidden;">
                    <c:forEach var="programa" items="${listaProgramas}">
                        <li style="display: flex; justify-content: space-between; align-items: center; padding: 12px 18px; border-bottom: 1px solid #eee; transition: background 0.15s; background-color: #fff;"
                            onmouseover="this.style.backgroundColor='#fcfcfc';" 
                            onmouseout="this.style.backgroundColor='#fff';">
                            
                            <span style="font-weight: bold; color: #2c3e50; font-size: 15px;">
                                🎓 ${programa}
                            </span>
                            
                            <button type="button" onclick="cargarDetallePrograma('${programa}')"
                                    style="background-color: #5cb85c; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: bold; transition: background 0.2s;"
                                    onmouseover="this.style.backgroundColor='#4cae4c';" 
                                    onmouseout="this.style.backgroundColor='#5cb85c';">
                                Ver Programa
                            </button>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <div style="background-color: #f8f9fa; color: #666; padding: 10px 15px; border-radius: 6px; border: 1px solid #e9ecef; font-size: 14px;">
                    No hay programas registrados para esta selección.
                </div>
            </c:otherwise>
        </c:choose>
    </div>

</div>