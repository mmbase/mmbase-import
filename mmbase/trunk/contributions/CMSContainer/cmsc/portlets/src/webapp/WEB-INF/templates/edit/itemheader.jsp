<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div id="element_${elementId}">

<portlet:renderURL var="renderUrl"/>
<mm:url page="/editors/WizardInitAction.do" id="editurl" write="false" >
   <mm:param name="objectnumber" value="${elementId}"/>
   <mm:param name="returnurl" value="${renderUrl}" />
   <mm:param name="popup" value="true" />
</mm:url>

<a href="<mm:write referid="editurl"/>" target="cmsc_element_edit" 
   onclick="openPopupWindow('cmsc_element_edit', '750', '750')" class="portal_button" style="float: left;">
	<fmt:message key="edit.fulledit" />
</a>

<c:if test="${empty orderby}">
	<span class="handle" style="float: left;"><fmt:message key="edit.drag" /></span>
</c:if>