<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>
<%@ taglib uri="http://www.luceus.com/taglib" prefix="lm"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.elementsperpage" />:</td>
				<td><cmsc:select var="elementsPerPage">
					<cmsc:option value="" message="edit_defaults.unlimited" /> 
					<cmsc:option value="5" />
					<cmsc:option value="10" />
					<cmsc:option value="15" />
					<cmsc:option value="20" />
					<cmsc:option value="25" />
					<cmsc:option value="50" />
				</cmsc:select></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.numberofpages" />:</td>
				<td><cmsc:select var="showPages">
					<cmsc:option value="" message="edit_defaults.unlimited" /> 
					<cmsc:option value="5" />
					<cmsc:option value="10" />
					<cmsc:option value="15" />
					<cmsc:option value="20" />
				</cmsc:select></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.maxelements" />:</td>
				<td>
					<input type="text" name="maxElements" value="${maxElements}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.pagesindex" />:</td>
				<td><cmsc:select var="pagesIndex">
					<cmsc:option value="center" message="edit_defaults.pagesindex.center" />
					<cmsc:option value="forward" message="edit_defaults.pagesindex.forward" />
					<cmsc:option value="half-full" message="edit_defaults.pagesindex.half-full" />
				</cmsc:select></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.indexname" />:</td>
				<td><lm:listindexes var="indexes"/>
					<cmsc:select var="indexName">
						<c:forEach var="idx" items="${indexes}">
							<cmsc:option value="${idx}" name="${idx}" />
						</c:forEach>
					</cmsc:select>
				</td>
			</tr>

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>