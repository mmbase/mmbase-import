<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<portlet:defineObjects />

<cmsc:path var="listPath" />

<div id="breadcrumb">
	<c:forEach var="crumb" items="${listPath}" varStatus="status">
		<a href="<cmsc:link dest="${crumb.id}"/>" title="<c:out value="${crumb.title}"/>"><c:out value="${crumb.title}"/></a>
      <c:if test="${!status.last}">&raquo;</c:if>
	</c:forEach>
</div>