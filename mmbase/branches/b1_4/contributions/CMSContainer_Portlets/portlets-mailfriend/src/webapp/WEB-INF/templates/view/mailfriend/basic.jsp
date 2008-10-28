<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<cmsc:location var="cur" sitevar="site" />
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
		
			<p><fmt:message key="view.titleinfo"/></p>

			<table cellpadding="0" cellspacing="0" border="0" class="nopad">
			    <tr>
					<td></td>
					<td>
						<label class="upcase"><fmt:message key="view.link"/></label>
					</td>
				</tr>
				<tr>
					<td>
						<label for="url"><fmt:message key="view.url"/></label>
					</td>
					<td>
						<c:set var="articleUrlPath" value="http://${site.urlfragment}/content/${articleIdValue}" />
						<input type="text" name="articleUrlPath" value="${articleUrlPath}" maxlength="255" readonly="readonly"/>
						<div></div>
					</td>
				</tr>
			</table>
			
			<p><fmt:message key="view.info"/></p>
			
			<c:if test="${!empty errormessages['sendemail']}">
				<font size="1" color="red"><fmt:message key="${errormessages['sendemail']}" /></font>
			</c:if>
			
			<c:if test="${!empty errormessages['article']}">
				<font size="1" color="red"><fmt:message key="${errormessages['article']}" /></font>
			</c:if>
				
			<form name="<portlet:namespace />form" method="post"
			action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" >	
				<input type="hidden" name="articleNumber" value="${articleIdValue}"/>
				<input type="hidden" name="articleUrlPath" value="${articleUrlPath}"/>
			    <mm:node number="${elementId}" notfound="skip">	
			    <table cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td></td>
						<td>
							<label class="upcase"><fmt:message key="view.addressee"/></label>
						</td>
					</tr>
					<tr>
						<td>
							<label for="toname"><fmt:message key="view.toname"/></label>  		    
						</td>	
						<td>
							<input type="text" name="toname" value="${toname}" maxlength="255"/>
							<font size="1" color="red"><b>*</b></font> 
							<c:if test="${!empty errormessages['toname']}">
								<font size="1" color="red">	
									<br/><fmt:message key="${errormessages['toname']}" />
								</font> 
							</c:if>								 
						</td>
					</tr>
					<tr>
						<td>
							<label for="toemail"><fmt:message key="view.toemail"/></label> 		    
						</td>	
						<td>
							<input type="text" name="toemail" value="${toemail}" maxlength="255"/>
							<font size="1" color="red"><b>*</b></font> 							
							<c:if test="${!empty errormessages['toemail']}">
								<font size="1" color="red">
									<br/><fmt:message key="${errormessages['toemail']}" />
								</font> 
							</c:if>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<label class="upcase"><fmt:message key="view.sender"/></label>
						</td>
					</tr>
					<tr>
						<td>
							<label for="fromname"><fmt:message key="view.fromname"/></label>  		    
						</td>	
						<td>							 							
							<input type="text" name="fromname" value="${fromname}" maxlength="255"/>
							<font size="1" color="red"><b>*</b></font>
							<c:if test="${!empty errormessages['fromname']}">
								<font size="1" color="red">	
									<br/><fmt:message key="${errormessages['fromname']}" />
								</font> 
							</c:if>
						</td>
					</tr>
					<tr>
						<td>
							<label for="fromemail"><fmt:message key="view.fromemail"/></label>		    
						</td>	
						<td>							 							
							<input type="text" name="fromemail" value="${fromemail}" maxlength="255"/>								
							<font size="1" color="red"><b>*</b></font>
							<c:if test="${!empty errormessages['fromemail']}">
								<font size="1" color="red">
									<br/><fmt:message key="${errormessages['fromemail']}" />
								</font> 
							</c:if>								 
						</td>
					</tr>
					<tr>
						<td>
							<label for="message"><fmt:message key="view.message"/></label>  		    
						</td>	
						<td>							 
							<textarea name="message" rows="3" cols="20">${message}</textarea>		 	 
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


