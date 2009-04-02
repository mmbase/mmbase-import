<%@ tag body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${tag_op_status eq 'header'}">
   <jsp:doBody/>
</c:if>