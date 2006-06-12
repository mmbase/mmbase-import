<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<script type="text/javascript">
function selectChannel(channel, path) {
	document.forms['<portlet:namespace />form'].contentchannel.value = channel;
	document.forms['<portlet:namespace />form'].contentchannelpath.value = path;
}
</script>
<div>
<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post"
	action="<portlet:actionURL><portlet:param name="action" value="edit"/></portlet:actionURL>">

<table class="editcontent">
	<tr>
		<td><fmt:message key="edit_defaults.channel" />:</td>
		<td>
		<mm:cloud>
			<mm:node number="${contentchannel}" notfound="skip">
				<mm:field name="path" id="contentchannelpath" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="contentchannel" value="${contentchannel}" />
		<input type="text" name="contentchannelpath" value="${contentchannelpath}" disabled="true" />
			<a href="<c:url value='/editors/repository/select/SelectorChannel.do?channel=${contentchannel}' />"
				target="selectchannel" onclick="openPopupWindow('selectchannel', 300, 400)"> 
				<fmt:message key="edit_defaults.channelselect" />
			</a></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.view" />:</td>
		<td><cmsc:select var="view">
			<c:forEach var="v" items="${views}">
				<cmsc:option value="${v.id}" name="${v.title}" />
			</c:forEach>
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.viewtype" />:</td>
		<td><cmsc:select var="viewtype">
			<cmsc:option value="list" message="edit_defaults.viewtype.list" />
			<cmsc:option value="detail" message="edit_defaults.viewtype.detail" />
			<cmsc:option value="oneDetail" message="edit_defaults.viewtype.oneDetail" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2">
			<h4><fmt:message key="edit_defaults.content" /></h4>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.uselifecycle" />:</td>
		<td><cmsc:select var="useLifecycle">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.orderby" />:</td>
		<td><cmsc:select var="orderby">
			<cmsc:option value="" message="edit_defaults.orderby.channelposition" />
			<cmsc:option value="title" message="edit_defaults.orderby.title" />
			<cmsc:option value="description" message="edit_defaults.orderby.description" />
			<cmsc:option value="creationdate" message="edit_defaults.orderby.creationdate" />
			<cmsc:option value="lastmodifieddate" message="edit_defaults.orderby.lastmodifieddate" />
			<cmsc:option value="embargodate" message="edit_defaults.orderby.embargodate" />
			<cmsc:option value="expirydate" message="edit_defaults.orderby.expirydate" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.direction" />:</td>
		<td><cmsc:select var="direction">
			<cmsc:option value="UP" message="edit_defaults.ascending" />
			<cmsc:option value="DOWN" message="edit_defaults.descending" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.maxelements" />:</td>
		<td><cmsc:select var="maxElements">
			<cmsc:option value="" message="edit_defaults.unlimited" />
			<cmsc:option value="1" />
			<cmsc:option value="2" />
			<cmsc:option value="3" />
			<cmsc:option value="4" />
			<cmsc:option value="5" />
			<cmsc:option value="10" />
			<cmsc:option value="15" />
			<cmsc:option value="20" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2">
			<h4><fmt:message key="edit_defaults.paging" /></h4>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.usepaging" />:</td>
		<td><cmsc:select var="usePaging">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.position" />:</td>
		<td><cmsc:select var="position">
			<cmsc:option value="top" message="edit_defaults.position.top" />
			<cmsc:option value="bottom" message="edit_defaults.position.bottom" />
			<cmsc:option value="both" message="edit_defaults.position.both" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.elementsperpage" />:</td>
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
		<td><fmt:message key="edit_defaults.numberofpages" />:</td>
		<td><cmsc:select var="showPages">
			<cmsc:option value="" message="edit_defaults.unlimited" />
			<cmsc:option value="5" />
			<cmsc:option value="10" />
			<cmsc:option value="15" />
			<cmsc:option value="20" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.pagesindex" />:</td>
		<td><cmsc:select var="pagesIndex">
			<cmsc:option value="center" message="edit_defaults.pagesindex.center" />
			<cmsc:option value="forward" message="edit_defaults.pagesindex.forward" />
			<cmsc:option value="half-full" message="edit_defaults.pagesindex.half-full" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="<fmt:message key="edit_defaults.save" />" class="button" />
		</td>
	</tr>
</table>
</form>
</div>