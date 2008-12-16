<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<script src="<cmsc:staticurl page='/js/pollcookies.js' />" type="text/javascript"></script>
<!-- poll --> 
<mm:cloud method="asis">
	<mm:import externid="elementId" required="true" from="request" />		

	<c:set var="cookieName" value="poll${elementId}"/>
	<c:forEach items="${pageContext.request.cookies}" var="cookieItem">
		<c:if test="${cookieItem.name == cookieName}">
    		<c:set var="existsCookie" value="true" />			
    	</c:if>
    </c:forEach>   
    
    <cmsc:portletmode name="edit">
      	<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
   	</cmsc:portletmode> 	

	<c:choose>
	 	<c:when test="${empty existsCookie}"> 
			<form name="<portlet:namespace />form" method="post" id="<portlet:namespace />form"
				action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" >
					<div id="poll">						
						<mm:node number="${elementId}" notfound="skip">		
							<mm:relatednodes type="images">
								<mm:first>
									<img src="<mm:image template="s(80x60)"/>" alt="<mm:field name="title" />" class="foto"/>
								</mm:first>
							</mm:relatednodes>
							<span class="vraag"><mm:field name="question" write="true"/></span>							
							<mm:relatednodescontainer type="pollchoice" role="posrel">							
								<mm:relatednodes>
									<div> 
										<input type="radio" class="radio" name="pollChoiceNumber" value="<mm:field name="number" write="true" />" />
										<label for="<mm:field name="number" write="true" />"><mm:field name="answer" write="true"/></label>
									</div>							
								</mm:relatednodes>				
							</mm:relatednodescontainer>
						</mm:node>								    
						<div class="buttons">
							<input type="image" name="vote" id="vote" src="<cmsc:staticurl page='/gfx/knop/stem.gif'/>" onClick='vote(this.form,"${cookieName}");' /> 						
						</div>
					</div>	
			</form>		
		</c:when>
	 	<c:otherwise> 
	 	<form action="#" id="pollform">
	 	<div id="poll">		 		
			<mm:node number="${elementId}" notfound="skip">						
				<mm:relatednodes type="images">
					<mm:first>
						<img src="<mm:image template="s(80x60)"/>" alt="<mm:field name="title" />" class="foto"/>
					</mm:first>
				</mm:relatednodes>
				<span class="vraag"><mm:field name="question" write="true"/></span>			
				<mm:relatednodescontainer type="pollchoice" role="posrel">							
					<mm:relatednodes>
						<div><b><mm:field name="counter" write="true"/> : </b> <mm:field name="answer" write="true"/></div>					
					</mm:relatednodes>				
				</mm:relatednodescontainer>
			</mm:node>
			<div class="buttons">	
				<img src="<cmsc:staticurl page='/gfx/knop/tussenstand.gif'/>" alt="<fmt:message key="view.results"/>"/>
			</div>			
		</div>
		</form>
		</c:otherwise>
	</c:choose>
	<cmsc:portletmode name="edit">
    	<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
    </cmsc:portletmode>	
</mm:cloud>
<!-- /.poll -->
