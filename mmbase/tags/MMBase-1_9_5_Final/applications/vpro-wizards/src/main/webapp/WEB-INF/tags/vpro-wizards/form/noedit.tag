<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
    use this tag in your field tag to mark it as non-editing.
    when all fields in a container are non-editing the container buttons are not shown
--%>
<%@ tag body-content="empty"   %>
<c:if test="${not empty requestScope.___editablefields}">
    <c:set var="___editablefields" value="${requestScope.___editablefields - 1}" scope="request"/>
</c:if>
