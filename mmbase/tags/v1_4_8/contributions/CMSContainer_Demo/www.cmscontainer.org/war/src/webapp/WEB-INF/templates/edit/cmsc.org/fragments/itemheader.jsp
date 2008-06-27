<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<mm:cloud method="asis">
<cmsc:portletmode name="edit">
   <mm:node number="${elementId}" notfound="skip">
      <mm:relatednodes type="contentchannel" role="creationrel">
         <mm:field name="number" write="false" jspvar="channelnumber"/>
         <cmsc:isallowededit channelNumber="${channelnumber}">
            <c:set var="edit" value="true"/>
         </cmsc:isallowededit>
      </mm:relatednodes>
   </mm:node>
</cmsc:portletmode>
</mm:cloud>

<div id="element_${elementId}">
<c:if test="${edit}">
<cmsc:renderURL var="renderUrl"/>
<mm:url page="/editors/WizardInitAction.do" id="editurl" write="false" >
   <mm:param name="objectnumber" value="${elementId}"/>
   <mm:param name="returnurl" value="${renderUrl}" />
   <mm:param name="popup" value="true" />
</mm:url>

<a href="<mm:write referid="editurl"/>" target="cmsc_element_edit" 
   onclick="openPopupWindow('cmsc_element_edit', '750', '550')" class="portal_button" style="float: left;">
	<cmsc:editorMessage key="edit.fulledit" />
</a>
<br/>
</c:if>
