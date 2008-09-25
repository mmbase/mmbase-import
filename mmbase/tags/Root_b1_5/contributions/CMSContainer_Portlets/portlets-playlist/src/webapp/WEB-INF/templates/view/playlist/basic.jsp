<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<div class="kolomplaylist">
<cmsc:portletmode name="edit">
  	<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
</cmsc:portletmode> 	

<mm:cloud>
    <mm:import externid="elementId" required="true" from="request" />
    <mm:node number="${elementId}" notfound="skip">
        <h2><mm:field name="title"/></h2>
        <mm:field name="body">
            <mm:isnotempty><p><mm:write escape="none"/></p></mm:isnotempty>
        </mm:field>
    </mm:node>
</mm:cloud>    

<jsp:useBean id="now" class="java.util.Date" />
<c:set var="currentYear">
	<fmt:formatDate pattern="yyyy" value="${now}" />
</c:set>

<c:set var="days">01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31</c:set>  
<c:set var="hours">00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23</c:set>  
<c:set var="months">01,02,03,04,05,06,07,08,09,10,11,12</c:set>

<form name="<portlet:namespace />form" method="post" id="<portlet:namespace />form"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" >
    <!-- selecion -->
    <div id="playkeuze">
		<span>
			<img src="<cmsc:staticurl page='/gfx/header/form/dag.gif'/>" alt="" />
			<select name="selectedDay">
				<c:forEach var="day" items="${days}"> 
					<c:remove var="selectedDayText"/>  			
				   	<c:if test="${day == selectedDay}">	
				   		<c:set var="selectedDayText">selected="selected"</c:set> 
				   	</c:if>  	         
				   	<option label="${day}" value="${day}" ${selectedDayText}>${day}</option>           
				</c:forEach>
			</select>
		</span>
		<span>
			<img src="<cmsc:staticurl page='/gfx/header/form/maand.gif'/>" alt="" />
			<select name="selectedMonth">
				<c:forEach var="month" items="${months}"> 
					<c:remove var="selectedMonthText"/>  			
				   	<c:if test="${month == selectedMonth}">	
				   		<c:set var="selectedMonthText">selected="selected"</c:set> 
				   </c:if>  	         
				   <option label="${month}" value="${month}" ${selectedMonthText}><fmt:message key="month.${month}"/></option>           
				</c:forEach>				
			</select>
		</span>
		<span>
			<img src="<cmsc:staticurl page='/gfx/header/form/jaar.gif'/>" alt="" />
			<select name="selectedYear">		
				<c:choose>
					<c:when test="${currentYear == selectedYear}">
						<option label="${currentYear - 1}" value="${currentYear - 1}">${currentYear - 1}</option>
						<option label="${currentYear}" value="${currentYear}" selected="selected">${currentYear}</option>
					</c:when>
					<c:otherwise>
						<option label="${currentYear - 1}" value="${currentYear - 1}" selected="selected">${currentYear - 1}</option>
						<option label="${currentYear}" value="${currentYear}" >${currentYear}</option>
					</c:otherwise>
				</c:choose>						
			</select>
		</span>
		<span>
			<img src="<cmsc:staticurl page='/gfx/header/form/tijd.gif'/>" alt="" />
			<select name="selectedHour">
				<c:forEach var="hour" items="${hours}">   
					<c:remove var="selectedHourText"/> 
					<c:if test="${hour == selectedHour}">	
				   		<c:set var="selectedHourText">selected="selected"</c:set> 
				   </c:if>				   	         
				   <option label="${hour}" value="${hour}" ${selectedHourText}>${hour}:00</option>           
				</c:forEach>
			</select>			
		</span>
		<span class="right">
			<input type="image" src="<cmsc:staticurl page='/gfx/header/form/bekijkplaylist.gif'/>" onclick="javascript:this.form.submit();"/>
		</span>
		<br />
		<div class="clear"></div>
	</div>
    <!-- /selection -->

    <c:if test="${empty items}">
	    <fmt:message key="playlist.unavailable"/>
	</c:if>
	<c:if test="${not empty items}">
	    <table cellpadding="0" cellspacing="0" border="0">	      
	        <c:forEach var="item" items="${items}">            
	            <tr>               
	                <td>${fn:toLowerCase(item.artist)}</td>
	                <td>${fn:toLowerCase(item.title)}</td>
	            </tr>            
	        </c:forEach>
	    </table>  
    </c:if>
</form>
<cmsc:portletmode name="edit">
	<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
</cmsc:portletmode>	
</div>