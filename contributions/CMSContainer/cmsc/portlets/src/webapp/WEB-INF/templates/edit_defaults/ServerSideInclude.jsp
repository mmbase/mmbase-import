<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<h3><fmt:message key="edit_defaults.title" /></h3>	
	<form name="<portlet:namespace />form" method="post" action="<cmsc:actionURL />" target="_parent">	
		<table class="editcontent">
		
			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
			
			<tr>
				<td><fmt:message key="edit_defaults.source" />:</td>
				<td><cmsc:text var="source" /></td>
			</tr>
			
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>