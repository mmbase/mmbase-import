<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<h3><fmt:message key="edit_defaults.title" /></h3>	
	<form method="post" name="<portlet:namespace />form" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" target="_parent">	
		<table class="editcontent">
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.source" />:</td>
				<td><cmsc:text var="source" /></td>
			</tr>
			
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />
			
			<%-- Click to page options --%>
			<c:import url="sections/clicktopage.jsp" />	
		
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>