<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" action="<cmsc:actionURL />" target="_parent">	
		<table class="editcontent">
		
			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
			
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.source" />:</td>
				<td><cmsc:text var="source" /></td>
			</tr>
			
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>