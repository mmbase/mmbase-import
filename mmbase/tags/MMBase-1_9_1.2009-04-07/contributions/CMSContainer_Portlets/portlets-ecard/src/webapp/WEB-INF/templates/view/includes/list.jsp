<fmt:message key="view.ecard.intro"/> <br>
<fmt:message key="view.ecard.choosegallery"/>

<form name="<portlet:namespace />form" method="post" id="<portlet:namespace />form" action="<cmsc:renderURL/>" >

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

<mm:node number="${selgallery}" notfound="skip">
   	<h2><mm:field name="title"/> </h2>  
                 
       <mm:relatednodescontainer type="images" role="imagerel" >
       		<mm:sortorder field="imagerel.number" direction="down" />
           <mm:relatednodes orderby="imagerel.order">
              	<br>
              	<cmsc:renderURL var="imageurl">
            		<cmsc:param name="elementId" value="${_node.number}"/>
            		<cmsc:param name="selgallery" value="${selgallery}"/>
            	</cmsc:renderURL> 
              	<a href="${imageurl}"> 
              	<img src="<mm:image template="s(175)" />" 
              	alt="<mm:field name="description" escape="none"/>"/></a><br>
              	<mm:field name="title" escape="none"/><br>
           </mm:relatednodes>	
      </mm:relatednodescontainer>    
</mm:node> 
