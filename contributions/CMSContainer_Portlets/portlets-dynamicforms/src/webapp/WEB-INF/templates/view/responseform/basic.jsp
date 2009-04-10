<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="responseform">
<mm:cloud method="asis">
	<mm:import externid="elementId" required="true" from="request" />		
	<mm:node number="${elementId}" notfound="skip">	
	
	<cmsc:portletmode name="edit">
      <%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
	</cmsc:portletmode>
   		
	<h2><mm:field name="title"/></h2>
   
	<mm:field name="intro">
		<mm:isnotempty><div class="intro"><mm:write escape="none"/></div></mm:isnotempty>
	</mm:field>
   
   <mm:field name="body">
      <mm:isnotempty><div class="body"><mm:write escape="none"/></div></mm:isnotempty>
   </mm:field>
   
	</mm:node>	
	<c:set var="isConfirmPage">
		<c:out value="${confirm}"/>
	</c:set>
	<c:choose>
		<c:when test="${empty isConfirmPage}">
			<c:set var="emailerrormessage">
				<c:out value="${errormessages['sendemail']}"/>
			</c:set>
			<c:if test="${!empty emailerrormessage}">
				<font size="1" color="red"><fmt:message key="${emailerrormessage}" /></font>
			</c:if>			
			<form name="<portlet:namespace />form" method="post"
			action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" 
			enctype="multipart/form-data">	
			    <mm:node number="${elementId}" notfound="skip">	
			    <table cellpadding="0" cellspacing="0" border="0">
			    <tr><td colspan="2">&nbsp;</td></tr>		
					<mm:relatednodescontainer type="formfield" role="posrel" >							
						<mm:sortorder field="posrel.pos"/>
						<mm:relatednodes>	
						 	<c:set var="fieldtype" >
								<mm:field name="type"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
							</c:set>		
							<c:set var="fieldnumber" >
								<mm:field name="number"><mm:isnotempty><mm:write/></mm:isnotempty></mm:field>
							</c:set> 
							     	
							<c:set var="fieldidentifier" value="field_${fieldnumber}"/>		
				            
				            <c:set var="fieldvalue">
		                       		<c:out value="${requestScope[fieldidentifier]}"/>
						    </c:set>                                
							
				            <c:if test="${empty fieldvalue && fieldtype!=4 && fieldtype!=5}">				           
							<%-- use defaultvalue only for field types different than radio and checkbox. --%>				            
						        <c:set var="fieldvalue">
									<mm:field name="defaultvalue"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
								</c:set>   							
							</c:if>	
		 					<tr> 					
								<td>								
									<c:if test="${fieldtype!=3}"> 										
									 	<label for="${fieldidentifier}"><mm:field name="label" write="true"/></label>																																										
									</c:if>
								</td>
									<c:choose>
										<c:when test="${fieldtype==4}">
											<td class="radios">		
										</c:when>	
										<c:otherwise>
											<td>
										</c:otherwise>
									</c:choose>						
									<c:choose>	
										<c:when test="${fieldtype==1}">
											<input size="<mm:field name="columns" write="true"/>" name="${fieldidentifier}" 
											type="text" value="${fieldvalue}" maxlength="255"/>
										</c:when>
										<c:when test="${fieldtype==2}">
										    <c:set var="errormessagekey">
                                                <c:out value="${errormessages[fieldidentifier]}"/>
                                            </c:set>
											<textarea name="${fieldidentifier}" 
		                    						rows="<mm:field name="rows" write="true"/>"
		                    						cols="<mm:field name="columns" write="true"/>">${fieldvalue}</textarea>
						                        <c:if test="${not empty errormessagekey}">
								                  <font size="1" color="${error_style == ''?'':'red'}">
							                      <fmt:message key="${errormessagekey}" />
								                  </font>
							                    </c:if>
										</c:when>
										<c:when test="${fieldtype==3}">
											<input type="hidden" name="${fieldidentifier}" value="${fieldvalue}"/>
										</c:when>
										<c:when test="${fieldtype==4}">
											<mm:node number="${fieldnumber}" notfound="skip">							                	
							                   	<mm:relatednodescontainer type="formfieldvalue" role="posrel">						
							                   	<mm:sortorder field="posrel.pos"/>	
												<mm:relatednodes> 
													<c:set var="usedefault" >
														<mm:field name="standard"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
													</c:set>        
													<c:set var="standardvalue" >
														<mm:field name="value"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
													</c:set> 		
													<c:remove var="radiochecked"/>										
													<c:if test="${((usedefault==1) && (empty fieldvalue)) || (fieldvalue eq standardvalue)}">
								                        <c:set var="radiochecked" value="checked"/>
								                    </c:if>			                    						                   
								                    <input type="radio" name="${fieldidentifier}" value="${standardvalue}" ${radiochecked} />							                        
								                    <label for="${fieldidentifier}"><mm:field name="text" write="true"/></label><br/>	          								                    
							                    </mm:relatednodes>
							                    </mm:relatednodescontainer>								                  					               		
							            	</mm:node>
										</c:when>
										<c:when test="${fieldtype==5}">
											<mm:node number="${fieldnumber}" notfound="skip">
										 		<select name="${fieldidentifier}">
													<mm:relatednodescontainer type="formfieldvalue" role="posrel">							
													<mm:sortorder field="posrel.pos"/>
													<mm:relatednodes>  
														<c:set var="usedefault" >
															<mm:field name="standard"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
														</c:set>        
														<c:set var="standardvalue" >
															<mm:field name="value"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
														</c:set>
														<c:remove var="selectedval"/>
														<c:if test="${((usedefault==1) && (empty fieldvalue)) || (fieldvalue eq standardvalue)}">
							                        		<c:set var="selectedval" value="selected"/>
							                        	</c:if>     
														<option value="${standardvalue}" ${selectedval} ><mm:field name="text" write="true"/></option>                   
							                     	</mm:relatednodes>
							            			</mm:relatednodescontainer>
							            		</select>
							            	</mm:node>
										</c:when>
										<c:when test="${fieldtype==6}">
											<mm:node number="${fieldnumber}" notfound="skip">	
							                   	<mm:relatednodescontainer type="formfieldvalue" role="posrel">						
							                   	<mm:sortorder field="posrel.pos"/>	
												<mm:relatednodes> 													
													<c:set var="values" >
														<c:out value="${fieldvalue}"/>
													</c:set> 				
													<c:set var="usedefault" >
														<mm:field name="standard"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
													</c:set> 												
													<c:set var="standardvalue" >
														<mm:field name="value"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
													</c:set> 	
										            <c:remove var="checkedval"/>
													<c:if test="${(usedefault==1) && (empty values)}">
							                        		<c:set var="checkedval" value="CHECKED"/>
							                        </c:if> 
													<c:forTokens items="${values}" var="chkValue" delims=":">
														<c:if test="${chkValue == standardvalue}">
		                                                   <c:set var="checkedval" value="CHECKED"/>
														</c:if>
													</c:forTokens>
        										    <input type="checkbox" name="${fieldidentifier}" value="${standardvalue}"  ${checkedval} id="inputnieuwsbrief"/>						                        
								                    <label for="${fieldidentifier}"><mm:field name="text" write="true"/></label><br/>				                    
							                    </mm:relatednodes>
							                    </mm:relatednodescontainer>								                  					               		
							            	</mm:node>
									
										</c:when>
										<c:when test="${fieldtype==7}">
											<input type="file" name="${fieldidentifier}"/>
										</c:when>
									</c:choose>
									<c:set var="mandatory" >
										<mm:field name="mandatory"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
									</c:set>
									<c:if test="${mandatory == 1}">
										<font size="1" color="red"><b>*</b></font>
									</c:if>										
									<c:set var="errormessagekey" >
										<c:out value="${errormessages[fieldidentifier]}"/>
									</c:set>
									<c:if test="${!empty errormessagekey}">
										<br/><font size="1" color="red"><fmt:message key="${errormessagekey}" /></font> 
									</c:if>									
								</td>												
							</tr>			
						</mm:relatednodes>				
					</mm:relatednodescontainer>					
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
			<%
				int eleId=Integer.parseInt((String)pageContext.getAttribute("elementId"));
				net.sf.mmapps.modules.cloudprovider.CloudProvider mmprovider = net.sf.mmapps.modules.cloudprovider.CloudProviderFactory.getCloudProvider();
				org.mmbase.bridge.Cloud mmCloud = mmprovider.getCloud();
				org.mmbase.bridge.Node eleNode=mmCloud.getNode(eleId);
				String tkHtml=eleNode.getStringValue("thank_text").trim();
				request.setAttribute("tkHtml",tkHtml);
			%>
			<mm:node number="${elementId}" notfound="skip">
				<mm:field name="confirmation">
				<mm:isnotempty><div class="confirmation"><mm:write escape="none"/></div></mm:isnotempty></mm:field>	
			</mm:node>		
			${tkHtml}
		</c:otherwise>
	</c:choose>		
	
	<cmsc:portletmode name="edit">
    	<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
   </cmsc:portletmode>	
    
</mm:cloud>
</div>