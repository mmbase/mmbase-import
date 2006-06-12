<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div id="<portlet:namespace />list">
<portlet:renderURL var="renderUrl"/>

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">

<mm:url page="/editors/WizardInitAction.do" id="newurl" write="false" >
	<c:if test="${not empty types}">
		<c:forEach var="type" items="${types}">
			<mm:param name="contenttype" value="${type}"/>
		</c:forEach>
	</c:if>
	<c:if test="${empty types}">
		<mm:listnodes type="editwizards">
		   <mm:field name="nodepath" jspvar="nodepath" id="nodepath" vartype="String">
			  <% if (com.finalist.cmsc.repository.ContentElementUtil.isContentType(nodepath)) { %>
				<mm:param name="contenttype" value="${nodepath}"/>
			  <% } %>
		   </mm:field>
		</mm:listnodes>
	</c:if>
   <mm:param name="returnurl" value="${renderUrl}" />
   <mm:param name="popup" value="true" />
   <mm:param name="action" value="create" />
   <mm:param name="creation" value="${contentchannel}" />
</mm:url>
<a href="<mm:write referid="newurl"/>" target="cmsc_element_edit" 
   onclick="openPopupWindow('cmsc_element_edit', '750', '750')">
	New
</a>
</mm:cloud>
