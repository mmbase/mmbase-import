<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form method="POST" name="<portlet:namespace />form" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" target="_parent">

<table class="editcontent">
	<tr>
		<td><fmt:message key="edit_defaults.define"/>:</td>
		<mm:cloud>
			<mm:node number="${requestScope['com.finalist.cmsc.beans.om.definitionId']}" notfound="skip">
				<td>
					<input type="text" name="portletname" value="<mm:field name='title'/>" disabled="disabled"/>
				</td>
			</mm:node>
		</mm:cloud>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.view" />:</td>
		<td>
			<cmsc:select var="view">
				<c:forEach var="v" items="${views}">
					<cmsc:option value="${v.id}" name="${v.title}" />
				</c:forEach>
			</cmsc:select>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
		</td>
	</tr>
</table>
</form>
</div>
