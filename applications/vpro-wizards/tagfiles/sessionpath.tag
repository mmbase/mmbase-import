<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:forEach var="entry" items="${__path}" >
    <c:set var="name" value="${entry.name}" />
    <c:set var="url" value="${entry.url}" />
    <c:set var="session" value="${entry.key}" />
    <c:choose>
        <c:when test="${not empty url}">
            <script type="text/javascript">
                var path = document.getElementById('path');
                path.innerHTML = path.innerHTML + '/&nbsp;<a href="${url}"><spring:escapeBody javaScriptEscape="true">${name}</spring:escapeBody></a>&nbsp;';
            </script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript">
                var path = document.getElementById('path');
                path.innerHTML = path.innerHTML + '/&nbsp;<spring:escapeBody javaScriptEscape="true">${name}</spring:escapeBody>&nbsp;';
            </script>
        </c:otherwise>
    </c:choose>
</c:forEach>