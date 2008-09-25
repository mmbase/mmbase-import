<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div class="kolom">
<cmsc:portletmode name="edit">
   <form name="contentportlet" method="post" action="<portlet:actionURL><portlet:param name="action" value="edit"/></portlet:actionURL>">
</cmsc:portletmode>

<mm:cloud method="asis">
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">
	
		<cmsc:portletmode name="edit">
			<mm:relatednodes type="contentchannel" role="creationrel" searchdir="destination">
				<mm:field name="number" write="false" jspvar="channelnumber"/>
				<cmsc:isallowededit channelNumber="${channelnumber}">
					<c:set var="edit" value="true"/>
				</cmsc:isallowededit>
			</mm:relatednodes>
		</cmsc:portletmode>
		
		<mm:field name="title"> 
	    	<c:if test="${edit}">				
				<h2 id="content_${elementId}_title"><mm:write/></h2>
			</c:if>
			<c:if test="${!edit}">				
					<h2><mm:write/></h2>			
			</c:if>
		</mm:field>		
		<mm:field name="body">
	 		<c:if test="${edit}">
				<div id="content_${elementId}_body">     
			</c:if>						
	       		<mm:isnotempty><mm:write escape="none" /></mm:isnotempty>	
			<c:if test="${edit}">
				</div>
			</c:if>	               
	    </mm:field> 
	</mm:node>
</mm:cloud>

<cmsc:portletmode name="edit">
	</form>
	<script type="text/javascript">
		new InPlaceEditor.Local('content_${elementId}_title');
		new InPlaceEditor.Local('content_${elementId}_body', {minHeight:300, htmlarea:true, formId:'contentportlet'});
	</script>
</cmsc:portletmode>

<cmsc:portletmode name="edit">
	</form>
</cmsc:portletmode>
</div>