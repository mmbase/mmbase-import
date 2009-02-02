<%@include file="globals.jsp" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
<mm:content type="text/html" encoding="UTF-8" expires="0">
<fmt:setBundle basename="savedformmodule" scope="request" />
<html:html xhtml="true">
<head>
   <title><fmt:message key="savedform.title.content" /></title>
   <link rel="stylesheet" type="text/css" href="../css/main.css" />
   <script src="../utils/rowhover.js" type="text/javascript"></script>
</head>
<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<mm:import externid="savedFormNodeList" jspvar="sfNodeList" vartype="List" />
<mm:import externid="headerNumbers" jspvar="headerNumbers" vartype="List" />

   <div class="content">
      <div class="tabs">
          <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="savedform.title.content" /></a>
               </div>
            </div>
         </div>
      </div>
   </div>
   <div class="editor"> 
      <div class="body">
         <mm:node number="${param.nodenumber}" notfound="skipbody">
            <p>
               <fmt:message key="savedform.savedforms.of" />
               <b><mm:nodeinfo type="guitype" /></b>:
               <mm:field name="title"/>
               
               <mm:hasrank minvalue="administrator">
		           <a href="<mm:url page="DeleteSavedForm.do" >
		                          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
		                          <mm:param name="returnurl" value="/editors/savedform/ShowSavedForm.do?nodenumber=${param.nodenumber}" />		                          
		                       </mm:url>">
		           <img src="../gfx/icons/delete.png" title="<fmt:message key="savedform.icon.deleteform" />"/></a>
		       </mm:hasrank>
		       
		       <a href="<mm:url page="DownloadSavedForm.do" >
		           <mm:param name="nodenumber"><mm:field name="number" /></mm:param>
		           <mm:param name="returnurl" value="/editors/savedform/ShowSavedForm.do?nodenumber=${param.nodenumber}" />
		           </mm:url>">   
		       <img src="../gfx/icons/excel.png" title="<fmt:message key="savedform.icon.downloadform" />"/></a>
		        		       
            </p>
            <p>
            	<mm:import externid="initreturnurl" from="parameters,request"/>         	
            	<mm:present referid="initreturnurl">
               		<a href="<mm:url page="${initreturnurl}"/>" title="<fmt:message key="locate.back" />" class="button"><fmt:message key="locate.back" /></a>
                </mm:present>
            </p>
         </mm:node>
         
         <c:if test="${not empty error}">
            <p class="error"><img src="../gfx/icons/error.png" alt="!">${error}</p>
         </c:if>
         <c:if test="${not empty message}">
            <p>${message}</p>
         </c:if>
         <c:if test="${not isAllowed}">
            <p><fmt:message key="savedform.not.allowed"/></p>
         </c:if>
         <c:if test="${empty savedFormNodeList}">
            <p><fmt:message key="savedform.no.savedforms"/></p>
         </c:if>
         </div>
         <div class="ruler_green">
            <div><fmt:message key="savedform.title.content" /></div>
         </div>
         <div class="body">
		 <table>  
			<thead>
      			<tr>        			
      			<c:forEach var="fieldNumber" items="${headerNumbers}">    
      			<c:set var="labelValue" value="-"/>
      			<mm:node number="${fieldNumber}" notfound="skipbody">  
      				<c:set var="labelValue">
      					<mm:field name="label"/>
      				</c:set>

					<%-- BEGIN: hack for element creation has to be moved to its own module --%>
      				<c:set var="description"><mm:field name="description"/></c:set>
      				<c:if test="${description == 'contenttype'}">
      					<c:set var="contenttypeNumber" value="${fieldNumber}"/>
      				</c:if>
					<%-- END: hack for element creation has to be moved to its own module --%>

      			</mm:node>				 

					<th nowrap="true"><c:out value="${labelValue}"/></th>  		
      			</c:forEach>
      			<th></th>
      			</tr>      			
			</thead> 
            <tbody class="hover">	
           	<mm:list referid="savedFormNodeList"> 
           
	            <mm:even inverse="true"><c:set var="class" value='class="swap"'/></mm:even>
	            <mm:even><c:set var="class"></c:set></mm:even>
	            <c:set var="savedformnumber">
	            	<mm:field name="number"/>
	            </c:set>
	            
	            <tr ${class}>	           
	            <c:forEach var="headernumber" items="${headerNumbers}">
	            	<c:set var="fieldvalue" value="-"/>
		            <mm:node number="${savedformnumber}" notfound="skip">	
		            	<mm:relatednodescontainer type="savedfieldvalue" role="posrel">							
							<mm:relatednodes>
		            			<c:set var="savedfieldnumber">
		            				<mm:field name="field"/>
		            			</c:set>         
		            			<c:if test="${savedfieldnumber == headernumber}">
		            				<c:set var="fieldvalue">
		            					<mm:field name="value"/>
		            				</c:set>         
		            			</c:if>
		            		</mm:relatednodes>				
						</mm:relatednodescontainer>
					</mm:node>
					<td>
						<%-- BEGIN: hack for element creation has to be moved to its own module --%>
						<c:if test="${headernumber == contenttypeNumber}"><a href="../dynamiccontent/export.jsp?id=${savedformnumber}"><fmt:message key="savedform.export" /></a></c:if> 
						<%-- END: hack for element creation has to be moved to its own module --%>
	                  	<c:out value="${fieldvalue}"/>
	                </td>
	            </c:forEach>
	           	<td>
		            <mm:hasrank minvalue="administrator">
			           <a href="<mm:url page="DeleteSavedAnswer.do" >
                         <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                         <mm:param name="returnurl" value="/editors/savedform/ShowSavedForm.do?nodenumber=${param.nodenumber}" />
                         <mm:param name="initreturnurl" value="${initreturnurl}"/>
                      </mm:url>">
			           <img src="../gfx/icons/delete.png" title="<fmt:message key="savedform.icon.deleteanswer" />"/></a>
			       	</mm:hasrank>
	            </td>	
	           	</tr>	                  
           </mm:list>
           </tbody>
         </table>
      </div>
   </div>
</mm:cloud>
</body>
</html:html>
</mm:content>