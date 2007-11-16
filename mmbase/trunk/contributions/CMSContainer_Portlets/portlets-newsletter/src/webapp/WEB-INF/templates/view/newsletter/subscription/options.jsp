<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<c:if test="param.message > 0">
	<b><ftm:message key="${message}"</p>
</c:if>

<div class="heading">
	<h3><fmt:message key="subscription.options.title" /></h3>
</div>
<div class="content">	
	<p><fmt:message key="subscription.options.info" /></p>
</div
<br><br>
<%@include file="overview.jsp" %>

<c:choose>
	<c:when test="${isactive == true}">
		<%@include file="resume.jsp" %>
	</c:when>
	<c:otherwise>
		<%@include file="pause.jsp" %>
	</c:otherwise>
</c:choose>

<%@include file="terminate.jsp" %>
