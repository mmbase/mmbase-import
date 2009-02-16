<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" action="<cmsc:actionURL />" target="_parent">

<table class="editcontent">
	<tr>
		<td><fmt:message key="edit_defaults.source" />:</td>
		<td><cmsc:text var="source" /></td>
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