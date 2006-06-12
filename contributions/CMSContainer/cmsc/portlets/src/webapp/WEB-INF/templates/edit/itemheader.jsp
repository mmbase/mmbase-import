<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div id="element_${elementId}">
<c:if test="${empty orderby}">
	<span class="handle">DRAG HERE</span>
</c:if>

<portlet:renderURL var="renderUrl"/>
<mm:url page="/editors/WizardInitAction.do" id="editurl" write="false" >
   <mm:param name="objectnumber" value="${elementId}"/>
   <mm:param name="returnurl" value="${renderUrl}" />
   <mm:param name="popup" value="true" />
</mm:url>

<a href="<mm:write referid="editurl"/>" target="cmsc_element_edit" 
   onclick="openPopupWindow('cmsc_element_edit', '750', '750')">
	Full edit
</a>