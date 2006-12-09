<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas">

<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" target="_parent"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

<table class="editcontent">
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.component" />:</td>
		<td><cmsc:select var="component">
			<c:forEach var="c" items="${components}">
				<cmsc:option value="${c.name}" name="${c.name}" />
			</c:forEach>
		</cmsc:select></td>
	</tr>

	<tr>
		<td colspan="3">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
		</td>
	</tr>
</table>
</form>
</div>