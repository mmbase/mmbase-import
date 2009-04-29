<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.max_items" />:</td>
				<td><input type="text" name="param_max" value="${param_max}" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.order_by" />:</td>
				<td><cmsc:select var="param_orderBy">
					<cmsc:option value="count" message="edit_defaults.order_by_count" />
					<cmsc:option value="name" message="edit_defaults.order_by_name" />
				</cmsc:select></td>
			</tr>
			<%-- Relate to portlet options --%>
			<c:import url="sections/relatetoportlet.jsp" />

			<%-- Click to page options --%>
			<c:import url="sections/clicktopage.jsp" />
		
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>