<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<br>
<br>
<br>
<br>
<c:choose>
<c:when test="${not empty param.ecardId}">
	<mm:cloud method="asis">
	<mm:node number="${param.galleryId}" notfound="skip">
		<mm:field name="title" id="galleryname" write="false"/>
	</mm:node>
	<mm:node number="${param.ecardId}" notfound="skip">
	<c:if test="${param.mailkey eq _node.mailkey}">
		<mm:relatednodes type="images" role="posrel" searchdir="destination">
			<mm:field name="title" id="imagename" write="false"/>
			<mm:field name="number" id="imagenumber" write="false"/>
		</mm:relatednodes>
		<br>
		${galleryname}: ${imagename} <br>
		
		<mm:node referid="imagenumber">
			<cmsc:image/>		
		</mm:node>
		fromemail <mm:field name="fromemail"/> <br>
		fromname <mm:field name="fromname"/> <br>
		toemail <mm:field name="toemail"/> <br>
		toname <mm:field name="toname"/> <br>
		textbody <mm:field name="body"/> <br>
	</c:if>
	</mm:node>
	</mm:cloud>
</c:when>
<c:when test="${empty param.elementId}">
	<form name="<portlet:namespace />form" method="post" id="<portlet:namespace />form"
					action="<cmsc:renderURL/>" >

	<select name="selgallery" id="selgallery" onChange="this.form.submit()">
	<c:forEach var="elem" items="${elements}" varStatus="listStatus">
	
	<c:choose>
		<c:when test="${empty selgallery}">	
		   
			<c:set var="selgallery">${elem.id}</c:set>
			<option value="${elem.id}">
			<c:out value="${elem.title}"/>
		   	</option>
	   	</c:when>
	   	<c:when test="${selgallery eq elem.id}">
			<option value="${elem.id}" selected="selected">
			<c:out value="${elem.title}"/>
		   	</option>
		</c:when>
		<c:otherwise>
			<option value="${elem.id}">
			<c:out value="${elem.title}"/>
		   	</option>
			
		</c:otherwise>
		</c:choose>	
	 </c:forEach>
	 </select> 

	</form>

<br/>  
<%@include file="list.jsp" %>
</c:when>
<c:otherwise>

	<mm:cloud method="asis">
		<c:choose>
		<c:when test="${empty param.emailsent}">
			<mm:node number="${param.elementId}">
				<cmsc:image/>
			</mm:node>
			
			<cmsc:location var="currentPage"/>
			
			<form name="<portlet:namespace />form" method="post" id="<portlet:namespace />form"
							action="<cmsc:actionURL/>" >
			<input type="hidden" name="elementId" value="${param.elementId}"/>
			<input type="hidden" name="galleryId" value="${param.selgallery}"/>
			<input type="hidden" name="pageId" value="${currentPage.id}"/>
			<input type="hidden" name="ecardWindow" value="${ecardWindow}"/>
							
			<br><fmt:message key="view.ecard.fromName"/><input type="text" name="fromName" value="${param.fromName}"/>
			<font size="1" color="red"><b>*</b></font> 
			<c:if test="${!empty errormessages['fromName']}">
				<font size="1" color="red">	
					<br/><fmt:message key="${errormessages['fromName']}" />
				</font> 
			</c:if>	
			<br><fmt:message key="view.ecard.fromEmail"/><input type="text" name="fromEmail" value="${param.fromEmail}"/>
			<br><fmt:message key="view.ecard.toName"/><input type="text" name="toName" value="${param.toName}"/>
			<br><fmt:message key="view.ecard.toEmail"/><input type="text" name="toEmail" value="${param.toEmail}"/>
			<br><fmt:message key="view.ecard.textBody"/><textarea name="textBody">${param.textBody}</textarea>
			<c:if test="${!empty sendNewsletter && sendNewsletter==true}">
				<c:set var="checkedval" value="CHECKED"/>
			</c:if>
			<br><fmt:message key="view.ecard.sendNewsletter"/><input type="checkbox" name="sendNewsletter" ${checkedval} value="yes"/>
			<br><input type="submit" value="send"/>
			
			</form>
		</c:when>
		<c:otherwise>
			<c:out value="${confirmation}"/> <br>
			Verstuurd naar: 
			${param.toName}
			cati@finalist.com <br>
			Inhoud tekst: ${param.textBody}
            <cmsc:renderURL var="funpage"/>
			Nog een e-card verzenden? Klik <a href="${funpage }">hier.</a>
		</c:otherwise>
		</c:choose>
	</mm:cloud>
</c:otherwise>
</c:choose>
