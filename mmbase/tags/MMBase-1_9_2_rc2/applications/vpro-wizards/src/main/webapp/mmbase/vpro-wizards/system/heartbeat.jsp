<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    //alleen als er wel een sessie is, geen sessie aanmaken!
    if(request.getSession(false) != null){%>
        <c:if test="${not empty param.enabled}">
            <c:set var="__heartbeat" scope="session" value="${param.enabled}" />
        </c:if>
<%}%>
