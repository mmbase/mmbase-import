<%@include file="globals.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:cloud jspvar="cloud" loginpage="login.jsp">

	<c:if test="${param.action == 'delete'}">
		<mm:deletenode number="${param.number}" deleterelations="true"/>
	</c:if>
	<c:if test="${param.action == 'save'}">
		<mm:node number="${param.number}">
			<mm:setfield name="name">${param.name}</mm:setfield>
			<mm:setfield name="description">${param.description}</mm:setfield>
		</mm:node>
	</c:if>
	<c:if test="${param.action == 'merge'}">
		<mm:listnodes type="insrel" constraints="dnumber = ${param.target2}">
			<mm:setfield name="dnumber">${param.target1}</mm:setfield>
		</mm:listnodes>
		<mm:deletenode number="${param.target2}" deleterelations="true"/>
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
		   
		   var oldOrderBy = '${param.orderby}';
		   var oldDirection = '${param.direction}';
		   function orderBy(order) {
	   		  document.location='list.jsp?orderby='+order+((oldOrderBy == order && oldDirection != 'down')?'&direction=down':'');
		   }
		   
		   var mergeTarget1;
		   function merge(target) {
		   		if(mergeTarget1 == undefined) {
		   			mergeTarget1 = target;
		   			document.getElementById('img_'+target).src = '../../gfx/icons/merge_selected.png';
		   		}
		   		else if(mergeTarget1 == target) {
		   			document.getElementById('img_'+mergeTarget1).src = '../../gfx/icons/merge.png';
		   			mergeTarget1 = undefined;
		   		}
		   		else if(confirm('<fmt:message key="tagcloud.merge.confirm" />')) {
		   			document.getElementById('merge_orderBy').value = oldOrderBy;
		   			document.getElementById('merge_direction').value = oldDirection;
		   			document.getElementById('merge_target1').value = mergeTarget1;
		   			document.getElementById('merge_target2').value = target;
		   			document.getElementById('merge_form').submit();
		   		}
		   		else {
		   			document.getElementById('img_'+mergeTarget1).src = '../../gfx/icons/merge.png';
		   			mergeTarget1 = undefined;
		   		}
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
	        <th><a href="javascript:orderBy('name')"><fmt:message key="taglist.tag"/></a></th>
	        <th><a href="javascript:orderBy('description')"><fmt:message key="taglist.description"/></a></th>
	        <th><a href="javascript:orderBy('count')"><fmt:message key="taglist.count"/></a></th>
	    </tr>
	</thead>
	<tbody class="hover">
		<form id="merge_form">
			<input type="hidden" name="action" value="merge"/>
			<input type="hidden" id="merge_orderBy" name="orderby"/>
			<input type="hidden" id="merge_direction" name="direction"/>
			<input type="hidden" id="merge_target1" name="target1"/>
			<input type="hidden" id="merge_target2" name="target2"/>
		</form>
	
		<cmsc-tc:getTags var="tags" orderby="${param.orderby}" direction="${param.direction}"/>
		<c:forEach var="tag" items="${tags}" varStatus="status">
			<tr <c:if test="${status.count%2==1}">class="swap"</c:if>  href="detail.jsp?number=${tag.number}">
				<td width="40">
					<a href="javascript:merge('${tag.number}');"><img id="img_${tag.number}" src="../../gfx/icons/merge.png" width="16" height="16"
                                         title="<fmt:message key="tagcloud.merge" />"
                                         alt="<fmt:message key="tagcloud.merge" />"/></a>
					<a href="?action=delete&number=${tag.number}&orderby=${param.orderby}&direction=${param.direction}" onclick="return confirm('<fmt:message key="tagcloud.delete.confirm" />')"><img src="../../gfx/icons/delete.png" width="16" height="16"
                                                         title="<fmt:message key="tagcloud.delete" />"
                                                         alt="<fmt:message key="tagcloud.delete" />"/></a>
				</td>
				<td onMouseDown="objClick(this);" style="text-transform: capitalize">
                    ${tag.name}</td>
				<td onMouseDown="objClick(this);">
				    <c:set var="description" value="${tag.description}"/>
				    <c:if test="${fn:length(description) > 90}">
				        <c:set var="description">${fn:substring(description,0,89)}...</c:set>
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