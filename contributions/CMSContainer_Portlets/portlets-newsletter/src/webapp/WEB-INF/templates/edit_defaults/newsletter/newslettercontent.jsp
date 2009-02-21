<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<%-- Order by option--%>
			<c:import url="sections/orderby.jsp" />

			<%-- Paging --%>
			<c:import url="sections/paging.jsp" />

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>