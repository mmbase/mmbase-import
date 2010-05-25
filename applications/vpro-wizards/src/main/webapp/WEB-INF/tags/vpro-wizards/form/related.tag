<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="relations" class="relatedview">
    <%-- 'authorized' is set in form:container --%>
    <c:if test="${authorized == 'true'}">
	    <jsp:doBody/>
    </c:if>
</div>