<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<head>
	<title><fmt:message key="masspublish.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
	<script>
	function select(wizard) {
		document.location.href="ChooseWizardAction.do?parentPage=${parentPage}&wizard="+wizard;
	}
	</script>
</head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="masspublish.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
			<mm:cloud jspvar="cloud" loginpage="login.jsp">
				<cmsc:publish number="${param.number}" children="true"/>
					<c:choose>
						<c:when test="${empty errors}">
							<p>
								<fmt:message key="masspublish.intro" />
							</p>
						</c:when>
						<c:otherwise>
		<table>
         <thead>
            <tr>
               <th><fmt:message key="masspublish.failednode" /></th>
               <th><fmt:message key="masspublish.errornodes" /></th>
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