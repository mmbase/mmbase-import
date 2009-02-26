<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form method="POST" name="<portlet:namespace />form" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" target="_parent">
		<table class="editcontent">
		
			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
			
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />
			
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>