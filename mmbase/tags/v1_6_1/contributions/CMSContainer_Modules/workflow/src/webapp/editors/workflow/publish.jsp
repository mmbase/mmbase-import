<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>

<c:set var="mass" value="${param.mass == 'true'}"/>
<c:set var="resourceName" value="${mass?'masspublish':'publish'}"/>
<c:set var="isConfirmed" value="${!empty param.confirm}"/>
<c:set var="showConfirm" value="${mass && !isConfirmed}"/>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="${resourceName}.title">
	<script>
	function select(wizard) {
		document.location.href="ChooseWizardAction.do?parentPage=${parentPage}&wizard="+wizard;
	}
	</script>
</cmscedit:head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="${resourceName}.title" /></a>
                </div>
            </div>
        </div>
    </div>
	<div class="editor">
		<div class="body">

<mm:cloud jspvar="cloud" loginpage="login.jsp">
	<cmsc:publish number="${param.number}" children="${mass}" var="count" execute="${!showConfirm}"/>

	<c:choose>
		<c:when test="${empty errors}">
			<c:choose>
				<c:when test="${showConfirm}">
					<p>
						<fmt:message key="${resourceName}.intro">
							<fmt:param>${count}</fmt:param>
						</fmt:message>
						<ul class="shortcuts">
						   <li class="masspublish"> 
								<a href="?number=${param.number}&mass=true&confirm=yes"><fmt:message key="${resourceName}.publish"/></a>
							</li>
						</ul>
					</p>
				</c:when>
				<c:otherwise>
					<p>
						<fmt:message key="${resourceName}.published" >
							<fmt:param>${count}</fmt:param>
						</fmt:message>
					</p>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<table>
	         <thead>
	            <tr>
	               <th><fmt:message key="${resourceName}.failednode" /></th>
	               <th><fmt:message key="${resourceName}.errornodes" /></th>
	            </tr>
	         </thead>
	         <tbody class="hover">
              <c:forEach var='item' items='${errors}'> 
              	<c:set var="failednode" value="${item.key}" />
              	 <tr <mm:even inverse="true">class="swap"</mm:even>>
                 <td>
                    <mm:node referid="failednode">
                    	<mm:nodeinfo type="guitype"/>
               			<mm:field name="number" />
               			<mm:hasfield name="title"><mm:field name="title" /></mm:hasfield>
               			<mm:hasfield name="name"><mm:field name="name" /></mm:hasfield>
                 	</mm:node>
                 </td>
                 <td>
                   <c:forEach var='errornode' items='${item.value}'> 
                     	<mm:node referid="errornode">
	                     	<mm:nodeinfo type="guitype"/>
                 			<mm:field name="number" />
                 			<mm:hasfield name="title"><mm:field name="title" /></mm:hasfield>
                 			<mm:hasfield name="name"><mm:field name="name" /></mm:hasfield>
						</mm:node>
						<br/>
                   </c:forEach>
                 </td>
                 </tr>
              </c:forEach>
	         </tbody>
	      </table>
		</c:otherwise>
	</c:choose>
</mm:cloud>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</html:html>
</mm:content>