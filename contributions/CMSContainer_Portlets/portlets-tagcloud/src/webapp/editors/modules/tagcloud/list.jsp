<%@include file="globals.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:cloud jspvar="cloud" loginpage="login.jsp">

	<c:if test="${param.action == 'delete'}">
		<mm:listnodes type="tag" constraints="name = '${param.tag}'">
			<c:set var="number"><mm:field name="number"/></c:set>
		</mm:listnodes>
		<c:if test="${!empty number}">
			<mm:deletenode number="${number}" deleterelations="true"/>
		</c:if>
	</c:if>

	
	<html:html xhtml="true">
	<cmscedit:head title="tagcloud.title">
		<script>
	       function objClick(el) {
		      var href = el.parentNode.getAttribute("href")+"";
		      if (href.length<10) 
		         return;
		   	  if (href.indexOf('javascript:') == 0) {
		   	  	eval(href.substring('javascript:'.length, href.length));
		   	  	return false;
		   	  }
		
		      document.location=href;
		   }
	   </script>
	</cmscedit:head>
	<body>
	
	<div class="tabs">
	    <!-- active TAB -->
	    <div class="tab_active">
	        <div class="body">
	            <div>
	                <a name="activetab"><fmt:message key="tagcloud.title"/></a>
	            </div>
	        </div>
	    </div>
	</div>
	
	<div class="editor">
	<div class="body">
	<table>
	<thead>
	    <tr>
			<th></th>
	        <th><fmt:message key="taglist.tag"/></th>
	        <th><fmt:message key="taglist.description"/></th>
	        <th><fmt:message key="taglist.count"/></th>
	    </tr>
	</thead>
	<tbody class="hover">
	
		<cmsc-tc:getTags var="tags" orderby="name"/>
		<c:forEach var="tag" items="${tags}" varStatus="status">
			<tr <c:if test="${status.count%2==1}">class="swap"</c:if>  href="detail.jsp?tag=${tag.name}">
				<td width="20">
					<a href="?action=delete&tag=${tag.name}" onclick="return confirm('<fmt:message key="tagcloud.delete.confirm" />')"><img src="../../gfx/icons/delete.png" width="16" height="16"
                                                         title="<fmt:message key="tagcloud.delete" />"
                                                         alt="<fmt:message key="tagcloud.delete" />"/></a>
				</td>
				<td onMouseDown="objClick(this);">
                    ${tag.name}</td>
				<td onMouseDown="objClick(this);">
				    <c:set var="description" value="${tag.description}"/>
				    <c:if test="${fn:length(description) > 50}">
				        <c:set var="description">${fn:substring(description,0,49)}...</c:set>
				    </c:if>
			        ${description}
				</td>				
				<td onMouseDown="objClick(this);">${tag.count}</td>
			</tr>
		</c:forEach>
	
	</tbody>
	</table>
	</div>
	</div>
	</body>
	</html:html>
</mm:cloud>	