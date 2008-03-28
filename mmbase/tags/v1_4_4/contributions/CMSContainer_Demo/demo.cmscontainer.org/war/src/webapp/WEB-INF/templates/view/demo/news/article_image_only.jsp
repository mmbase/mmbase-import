<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:cloud>
  <mm:import externid="elementId" required="true" />
  <mm:node number="${elementId}" notfound="skip">
	<cmsc:portletmode name="edit">
		<mm:relatednodes type="contentchannel" role="creationrel">
			<mm:field name="number" write="false" jspvar="channelnumber"/>
			<cmsc:isallowededit channelNumber="${channelnumber}">
				<c:set var="edit" value="true"/>
			</cmsc:isallowededit>
		</mm:relatednodes>
	</cmsc:portletmode>
		  
	<c:if test="${edit}">
		<form name="contentportlet" method="post" 
	  		  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
	</c:if>

    <div class="content">
      <!-- top image -->      
      <cmsc-bm:linkedimages width="502" position="top" style="display: block; clear: both; padding-bottom: 20px;" />
    </div>
    
	<c:if test="${edit}">
		<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
		</form>
	</c:if>
    
  </mm:node>
</mm:cloud>
