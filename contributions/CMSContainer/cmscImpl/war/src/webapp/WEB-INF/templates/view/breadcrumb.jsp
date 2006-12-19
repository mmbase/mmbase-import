<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<cmsc:path var="listPath" />
<ul id="breadcrumb">
	<c:forEach var="crumb" items="${listPath}" varStatus="status">
		<li>
            <a href="<cmsc:link dest="${crumb.id}"/>" title="<c:out value="${crumb.title}"/>"><c:out value="${crumb.title}"/></a>
            <c:if test="${!status.last}">&gt;</c:if>
		</li>
	</c:forEach>
</ul>