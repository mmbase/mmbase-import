<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="kolombestel">
<mm:cloud method="asis">
	<mm:import externid="elementId" required="true" from="request" />	
	<c:set var="isConfirmPage">
		<c:out value="${confirm}"/>
	</c:set>		
	<mm:node number="${elementId}" notfound="skip">			   
		<h2><mm:field name="title"/></h2> 	
	</mm:node>
	<cmsc:portletmode name="edit">
      	<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
   	</cmsc:portletmode>
         	 
	<c:choose>
		<c:when test="${empty isConfirmPage}">
		
			<mm:node number="${elementId}" notfound="skip">			   
				<mm:field name="intro">
				<mm:isnotempty><p class="body"><mm:write escape="none"/></p></mm:isnotempty></mm:field>	
			</mm:node>			
			<c:if test="${not empty param.refpage}">
				<c:set var="subscribepage" value="${param.refpage}"/>
			</c:if>
			<c:if test="${!empty errormessages['subscribepage']}">
				<font size="1" color="red">	
					<br/><fmt:message key="${errormessages['subscribepage']}" />
				</font> 
			</c:if>
			<c:if test="${!empty errormessages['sendemail']}">
				<font size="1" color="red">	
					<br/><fmt:message key="${errormessages['sendemail']}" />
				</font> 
			</c:if>
			<form name="<portlet:namespace />form" method="post"
			action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" >	
				<input type="hidden" name="subscribepage" value="${subscribepage}"/>
			    <table cellpadding="0" cellspacing="0" border="0">
			    <tr><td colspan="2">&nbsp;</td></tr>
					<tr>
						<td>
							<label for="emailaddress"><fmt:message key="view.emailaddress"/></label>  		    
						</td>	
						<td>
							<input type="text" name="emailaddress" value="${emailaddress}" maxlength="255"/>
							<font size="1" color="red"><b>*</b></font> 
							<c:if test="${!empty errormessages['emailaddress']}">
								<font size="1" color="red">	
									<br/><fmt:message key="${errormessages['emailaddress']}" />
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
			</form>		
		</c:when>
		<c:otherwise>
			<mm:node number="${elementId}" notfound="skip">
				<mm:field name="confirmation">
					<mm:isnotempty><p class="body"><mm:write escape="none"/></p></mm:isnotempty>
				</mm:field>	
			</mm:node>			
			<a href="javascript:window.close();"><fmt:message key="view.closewindow"/></a>
		</c:otherwise>
	</c:choose>
	<cmsc:portletmode name="edit">
    	<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
    </cmsc:portletmode>	
</mm:cloud>
</div>


