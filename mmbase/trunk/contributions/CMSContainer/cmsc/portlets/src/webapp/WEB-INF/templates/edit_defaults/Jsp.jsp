<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form method="post" action="<portlet:actionURL><portlet:param name="action" value="edit"/></portlet:actionURL>" target="_parent">
<fmt:message key="edit_defaults.view" />: 
<cmsc:select var="view">
	<c:forEach var="v" items="${views}">
		<cmsc:option value="${v.id}" name="${v.title}" />
	</c:forEach>
</cmsc:select><br />

<input type="submit" value="<fmt:message key="edit_defaults.save" />" /></form>
</div>