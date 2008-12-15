<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<c:choose>
	<c:when test="${hassubscriptions == true}">
		<%@include file="subscription/overview.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="subscription/introduction.jsp" %>
	</c:otherwise>
</c:choose>