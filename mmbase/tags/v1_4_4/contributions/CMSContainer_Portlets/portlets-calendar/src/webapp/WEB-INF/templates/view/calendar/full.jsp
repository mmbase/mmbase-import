<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cmsc:portletmode name="edit">
	<form name="contentportlet" method="post" 
  		  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
    <c:set var="edit" value="true"/>
</cmsc:portletmode>

<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">	
	<mm:node number="${elementId}" notfound="skip">			

		<c:if test="${edit}">
			<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
		</c:if>

		<%-- kop met printknop --%>
		<div class="pageheader">
			<mm:field name="title">

				<c:if test="${edit}">
					<%-- must remain on one line, or the rich edit wont work! --%>
					<h1 id="content_${elementId}_title"><mm:field name="title" /></h1>
				</c:if>
				
				<c:if test="${!edit}">
					<mm:isnotempty>
						<h1>
							<mm:write />
							<span class="printButton">
								<a href="javascript:print();" title="<fmt:message key="view.print" />"><fmt:message key="view.print" /></a>
							</span>
						</h1>
					</mm:isnotempty>
				</c:if>
				
			</mm:field>
		</div>
	
		<div class="article">
		    <%-- Introductie --%>
		    <div class="summary">
		       <p>
		       <mm:field name="intro" escape="none"/>		       
		       </p>
		     </div>
		     <div class="timeandplace">		       
			    
				<table>
					<tr>
			     	<th>Tijdstip:</th>
			     	<td><mm:field name="starttime" id="start"><mm:time time="$start" format="H:mm" /></mm:field> 			   			    		
			     		&nbsp;-&nbsp;<mm:field name="endtime" id="end"><mm:time time="$end" format="H:mm" />&nbsp;uur</mm:field> 			   
			     	</td>
			     	</tr>
			     	<tr>
			     	<th>Locatie:</th>
			     	<td><mm:field name="location"/>			     		
			     	</td>			     		
		    	 	</tr>
		    	</table> 	
			</div>
			<%--  Inhoud van agenda item --%>
			<div class="bodytext">		
				<p>						
					<cmsc-bm:linkedimages position="top-left" style="float: left; width: 170px; height:auto;" />
					<cmsc-bm:linkedimages position="top" style="display: block; clear: both; width: 340px; height:auto;" />
					<cmsc-bm:linkedimages position="top-right" style="float: right; width: 170px; height:auto;" />
					
					<mm:field name="body" escape="none"/>				
               <div style="clear:both"></div>
					
					<cmsc-bm:linkedimages position="bottom-left" style="float: left; width: 170px; height:auto;" />
					<cmsc-bm:linkedimages position="bottom" style="display: block; clear: both; width: 340px; height:auto;" />
					<cmsc-bm:linkedimages position="bottom-right" style="float: right; width: 170px; height:auto;" />
				</p>				
			</div>			
			<div class="back">
			<p>
				<cmsc:actionURL var="url"/>
				<a href="${fn:substring(url, 0, fn:indexOf(url, "/_"))}" class="readon" title="<mm:field name="title"/>">&lt; Terug naar overzicht</a>

			</p>
			</div>
			
			<div class="contentfooter">   
				<address>				                            		
	            <strong>Organisator</strong><br>
                <mm:field name="organizer" escape="none" />
                </address>
         	</div>	
         
		</div>						
		<c:if test="${edit}">
			<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
		</c:if>
	</mm:node>
</mm:cloud>
</mm:content>

