<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas">

<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" target="_parent"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

<table class="editcontent">
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.weekend" />:</td>
		<td><cmsc:select var="weekend">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select></td>
	</tr>

	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.cols" />:</td>
		<td>
			<cmsc:text var="cols" value="2" />
		</td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.rows" />:</td>
		<td>
			<cmsc:text var="rows" value="6" />
		</td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.offset" />:</td>
		<td>
			<cmsc:text var="offset" value="0" />
		</td>
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