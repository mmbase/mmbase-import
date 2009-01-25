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

			<tr>
				<td colspan="3"><h4><fmt:message key="edit_defaults.contentset"/></h4></td>
			</tr>			
			<tr>
				<td><fmt:message key="edit_defaults.channel" />:</td>
				<td align="right">
					<a href="javascript:openRepositoryWithChannel()">
						<img src="<cmsc:staticurl page='/editors/gfx/icons/preview.png'/>" alt="<fmt:message key="edit_defaults.preview"/>"/>
					</a>
					<a href="<c:url value='/editors/repository/select/SelectorChannel.do?channel=${contentchannel}' />" target="selectchannel" onclick="openPopupWindow('selectchannel', 340, 400)"> 
							<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.channelselect" />"/>
					</a>
					<a href="javascript:erase('contentchannel');erase('contentchannelpath')">
						<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
					</a>
				</td>
				<td>
					<mm:cloud>
						<mm:node number="${contentchannel}" notfound="skip">
							<mm:field name="path" id="contentchannelpath" write="false" />
						</mm:node>
					</mm:cloud>
					<input type="hidden" name="contentchannel" value="${contentchannel}" />
					<input type="text" name="contentchannelpath" value="${contentchannelpath}" disabled="true" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.viewtype" />:</td>
				<td>
					<cmsc:select var="viewtype">
						<cmsc:option value="oneDetail" message="edit_defaults.viewtype.oneDetail" />
						<cmsc:option value="list" message="edit_defaults.viewtype.list" />
						<cmsc:option value="detail" message="edit_defaults.viewtype.detail" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<h4><fmt:message key="edit_defaults.content" /></h4>
				</td>
			</tr>
			
			<%-- Use lifecycle option--%>
			<c:import url="sections/lifecycle.jsp" />
			
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.archive" />:</td>
				<td>
					<cmsc:select var="archive">
						<cmsc:option value="all" message="edit_defaults.archive.all" />
						<cmsc:option value="new" message="edit_defaults.archive.new" />
						<cmsc:option value="old" message="edit_defaults.archive.old" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.orderby" />:</td>
				<td>
					<cmsc:select var="orderby">
						<cmsc:option value="" message="edit_defaults.orderby.channelposition" />
						<cmsc:option value="title" message="edit_defaults.orderby.title" />
						<cmsc:option value="description" message="edit_defaults.orderby.description" />
						<cmsc:option value="creationdate" message="edit_defaults.orderby.creationdate" />
						<cmsc:option value="lastmodifieddate" message="edit_defaults.orderby.lastmodifieddate" />
						<cmsc:option value="publishdate" message="edit_defaults.orderby.publishdate" />
						<cmsc:option value="expiredate" message="edit_defaults.orderby.expiredate" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.direction" />:</td>
				<td>
					<cmsc:select var="direction">
						<cmsc:option value="DOWN" message="edit_defaults.descending" />
						<cmsc:option value="UP" message="edit_defaults.ascending" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.startindex" />:</td>
				<td>
					<input type="text" name="startindex" value="${startindex}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.maxelements" />:</td>
				<td>
					<input type="text" name="maxElements" value="${maxElements}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.maxDays" />:</td>
				<td>
					<input type="text" name="maxDays" value="${maxDays}" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<h4><fmt:message key="edit_defaults.paging" /></h4>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.usepaging" />:</td>
				<td>
					<cmsc:select var="usePaging" default="false">
						<cmsc:option value="true" message="edit_defaults.yes" />
						<cmsc:option value="false" message="edit_defaults.no" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.position" />:</td>
				<td>
					<cmsc:select var="position">
						<cmsc:option value="both" message="edit_defaults.position.both" />
						<cmsc:option value="top" message="edit_defaults.position.top" />
						<cmsc:option value="bottom" message="edit_defaults.position.bottom" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.elementsperpage" />:</td>
				<td>
					<cmsc:select var="elementsPerPage">
						<cmsc:option value="" message="edit_defaults.unlimited" />
						<cmsc:option value="5" />
						<cmsc:option value="10" />
						<cmsc:option value="15" />
						<cmsc:option value="20" />
						<cmsc:option value="25" />
						<cmsc:option value="50" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.numberofpages" />:</td>
				<td>
					<cmsc:select var="showPages">
						<cmsc:option value="" message="edit_defaults.unlimited" />
						<cmsc:option value="5" />
						<cmsc:option value="10" />
						<cmsc:option value="15" />
						<cmsc:option value="20" />
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.pagesindex" />:</td>
				<td>
					<cmsc:select var="pagesIndex">
						<cmsc:option value="center" message="edit_defaults.pagesindex.center" />
						<cmsc:option value="forward" message="edit_defaults.pagesindex.forward" />
						<cmsc:option value="half-full" message="edit_defaults.pagesindex.half-full" />
					</cmsc:select>
				</td>
			</tr>
			
			<%-- Click to page options --%>
			<c:import url="sections/clicktopage.jsp" />
			
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>