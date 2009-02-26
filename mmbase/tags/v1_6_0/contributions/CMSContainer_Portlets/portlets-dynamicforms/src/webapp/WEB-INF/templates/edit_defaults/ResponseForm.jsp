<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent"  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">
		
			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<%-- Content element selector --%>
			<c:import url="sections/selectelement.jsp" />
  
  			<%-- Nobody seems to know what this snipped is for. If you need it, please uncomment it.  
  			
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.window" />:</td>
				<td>
					<cmsc:select var="parentwindow">
						<c:forEach var="position" items="${thispagepositions}">
							<cmsc:option value="${position}" />
						</c:forEach>
					</cmsc:select>
				</td> 
			</tr>
			
			--%>	

			<%-- Click to page options --%>
			<c:import url="sections/clicktopage.jsp" />
			
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>