<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<%-- Select content channel selector --%>
			<c:import url="sections/selectchannel.jsp" />

			<tr>
				<td colspan="3">
					<h4><fmt:message key="edit_defaults.content" /></h4>
				</td>
			</tr>

			<%-- Use lifecycle option--%>
			<c:import url="sections/lifecycle.jsp" />

			<%-- Archive option--%>
			<c:import url="sections/archive.jsp" />

			<%-- Order by option--%>
			<c:import url="sections/orderby.jsp" />

			<%-- Maximum number of elements --%>
			<c:import url="sections/maxelements.jsp" />

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />

		</table>
	</form>
</div>