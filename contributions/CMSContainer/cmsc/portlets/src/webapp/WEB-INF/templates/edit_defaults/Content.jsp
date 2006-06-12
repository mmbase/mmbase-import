<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<script type="text/javascript">
function selectElement(contentelement, title) {
	document.forms['<portlet:namespace />form'].contentelement.value = contentelement;
	document.forms['<portlet:namespace />form'].contentelementtitle.value = title;
}
</script>
<div>
<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post"
	action="<portlet:actionURL><portlet:param name="action" value="edit"/></portlet:actionURL>">

<table class="editcontent">
	<tr>
		<td><fmt:message key="edit_defaults.contentelement" />:</td>
		<td>
		<mm:cloud>
			<mm:node number="${contentelement}" notfound="skip">
				<mm:field name="title" id="contentelementtitle" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="contentelement" value="${contentelement}" />
		<input type="text" name="contentelementtitle" value="${contentelementtitle}" disabled="true" />
			<a href="<c:url value='/editors/repository/select/index.jsp?contentnumber=${contentelement}' />"
				target="selectcontentelement" onclick="openPopupWindow('selectcontentelement', 900, 400)"> 
				<fmt:message key="edit_defaults.contentselect" />
			</a></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.view" />:</td>
		<td><cmsc:select var="view">
			<c:forEach var="v" items="${views}">
				<cmsc:option value="${v.id}" name="${v.title}" />
			</c:forEach>
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.uselifecycle" />:</td>
		<td><cmsc:select var="useLifecycle">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="<fmt:message key="edit_defaults.save" />" class="button" />
		</td>
	</tr>
</table>
</form>
</div>