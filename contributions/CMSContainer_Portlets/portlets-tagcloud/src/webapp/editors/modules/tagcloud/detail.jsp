<%@include file="globals.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:cloud jspvar="cloud" loginpage="login.jsp">
	
	<html:html xhtml="true">
	<cmscedit:head title="tagdetail.title">
		</cmscedit:head>
	<body>
	
	<div class="tabs">
	    <!-- active TAB -->
	    <div class="tab_active">
	        <div class="body">
	            <div>
	                <a name="activetab"><fmt:message key="tagdetail.title"/></a>
	            </div>
	        </div>
	    </div>
	</div>


		
	<div class="editor">
	<div class="body">
	
	<c:if test="${param.action == 'breaklink'}">

		<mm:list path="contentelement,insrel,tag" constraints="tag.name = '${param.tag}' AND contentelement.number = ${param.number}">
			<c:set var="relnumber"><mm:field name="insrel.number"/></c:set>
			<mm:deletenode number="${relnumber}"/>
		</mm:list>
	</c:if>

	<mm:listnodes type="tag" constraints="name = '${param.tag}'">
	<p>
		<b><fmt:message key="tagdetail.tag"/>:</b> <mm:field name="name"/><br/>
		<b><fmt:message key="tagdetail.description"/>:</b> <mm:field name="description"/><br/>
	</p>
	</div>
	<div class="ruler_green">
	    <div><fmt:message key="tagdetail.linked.to"/></div>
	</div>
	<div class="body">
	<table>
	<thead>
	    <tr>
	        <th><fmt:message key="tagdetail.content.title"/></th>
	        <th><fmt:message key="tagdetail.content.type"/></th>
	    </tr>
	</thead>
	<tbody class="hover">

	
		<mm:relatednodes type="contentelement">
			<tr <mm:odd>class="swap"</mm:odd>  href="detail.jsp?tag=${tag.name}">
				
				<td>
					<a href="?action=breaklink&tag=${param.tag}&number=<mm:field name="number"/>" onclick="return confirm('<fmt:message key="tagdetail.unlink.confirm" />')"><img src="../../gfx/icons/clear.png" width="16" height="16"
                                                         title="<fmt:message key="tagdetail.unlink" />"
                                                         alt="<fmt:message key="tagdetail.unlink" />"/></a> 
					<mm:field name="title"/>
				</td>
				<td> 
					<mm:nodeinfo type="guitype" />
				</td>
			</tr>
		</mm:relatednodes>
	</tbody>
	</table>
	</mm:listnodes>
	</div>
	</div>
	</body>
	</html:html>
</mm:cloud>	
