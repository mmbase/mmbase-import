<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="kolombestel">
<mm:cloud method="asis">
	<mm:import externid="elementId" required="true" from="request" />	
	<c:set var="isConfirmPage">
		<c:out value="${confirm}"/>
	</c:set>
	<h2><fmt:message key="view.title"/></h2> 		
	<cmsc:portletmode name="edit">
      	<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
   	</cmsc:portletmode>	
	<c:choose>
		<c:when test="${empty isConfirmPage}">
		
			<c:if test="${not empty articleNumber}">
				<c:set var="articleIdValue" value="${articleNumber}"/>
			</c:if> 
			
			<c:if test="${not empty param.articleId}">
				<c:set var="articleIdValue" value="${param.articleId}"/>
			</c:if>
			
			<mm:node number="${articleIdValue}" notfound="skip">
				<c:set var="articleTitle">
					<mm:field name="title"/>	
				</c:set>		
			</mm:node>	
		
			<mm:node number="${elementId}" notfound="skip">
				<mm:field name="intro">
				<mm:isnotempty><p><mm:write escape="none"/></p></mm:isnotempty></mm:field>	
			</mm:node>				
			
			<c:if test="${!empty errormessages['sendemail']}">
				<font size="1" color="red"><fmt:message key="${errormessages['sendemail']}" /></font>
			</c:if>
				
			<form name="<portlet:namespace />form" method="post"
			action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" >	
				<input type="hidden" name="articleNumber" value="${articleIdValue}"/>
			    <mm:node number="${elementId}" notfound="skip">	
			    <table cellpadding="0" cellspacing="0" border="0">
			    <tr><td colspan="2">&nbsp;</td></tr>
 					<tr> 					
						<td>
							<label for="labelonderwerp"><fmt:message key="view.subject"/></label>		    
						</td>	
						<td>
							${articleTitle}								
							<c:if test="${!empty errormessages['subject']}">
								<font size="1" color="red">
									<fmt:message key="${errormessages['subject']}" />
								</font>
							</c:if>							
						</td>	
					</tr>
					<tr>
						<td>
							<label for="name"><fmt:message key="view.name"/></label>  		    
						</td>	
						<td>
							<input type="text" name="name" value="${name}" maxlength="255"/>
							<font size="1" color="red"><b>*</b></font>								
							<c:if test="${!empty errormessages['name']}">
								<font size="1" color="red">
									<br/><fmt:message key="${errormessages['name']}" />
								</font> 
							</c:if>								 
						</td>
					</tr>
					<tr>
						<td>
							<label for="useremail"><fmt:message key="view.useremail"/></label>  		    
						</td>	
						<td>
							<input type="text" name="useremail" value="${useremail}" maxlength="255"/>
							<font size="1" color="red"><b>*</b></font> 								
							<c:if test="${!empty errormessages['useremail']}">
								<font size="1" color="red">
									<br/><fmt:message key="${errormessages['useremail']}" />
								</font> 
							</c:if>								 
						</td>
					</tr>
					<tr>
						<td>
							<label for="message"><fmt:message key="view.message"/></label>  		    
						</td>	
						<td>							 
							<textarea name="message" rows="5" cols="20">${message}</textarea>
							<font size="1" color="red"><b>*</b></font>								
							<c:if test="${!empty errormessages['message']}">
								<font size="1" color="red">
									<br/><fmt:message key="${errormessages['message']}" />
								</font>
							</c:if>							 	 
						</td>
					</tr>							
					<tr>
					<td></td>
				      <td>
				         <font size="1" color="red"><b>* <fmt:message key="view.mandatory"/> </b></font>
				      </td>
				   </tr>
					<tr>
						<td></td>
						<td>
							<input type="image" src="<cmsc:staticurl page='/gfx/knop/verstuur.gif'/>" id="knopverstuur" />
						</td>
					</tr>
				</table>
				</mm:node>	
			</form>		
		</c:when>
		<c:otherwise>
			<mm:node number="${elementId}" notfound="skip">
				<mm:field name="confirmation">
				<mm:isnotempty><p class="body"><mm:write escape="none"/></p></mm:isnotempty></mm:field>	
			</mm:node>			
			<a href="javascript:window.close();"><fmt:message key="view.closewindow"/></a>
		</c:otherwise>
	</c:choose>
	
	<cmsc:portletmode name="edit">
    	<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
    </cmsc:portletmode>	
		
</mm:cloud>
</div>


