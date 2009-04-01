<%@ tag body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="tag_op_status" type="String"--%>

<c:if test="${tag_op_status ne 'header'}">
   <jsp:doBody/>
</c:if>