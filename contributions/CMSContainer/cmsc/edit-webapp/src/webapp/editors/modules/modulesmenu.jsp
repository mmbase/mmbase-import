<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<title><fmt:message key="dashboard.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../utils/transparent_png.js" ></script>
</head>

<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">

	<mm:haspage page="/editors/admin/">
		<mm:hasrank minvalue="administrator">
		<div class="side_block">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="modules.title" /></div>
				<div class="header_end"></div>
			</div>
			
			<ul class="shortcuts">
            <mm:haspage page="/editors/modules/customermenu.jsp">
					<jsp:include page="/editors/modules/customermenu.jsp"/>
            </mm:haspage>
            <mm:haspage page="/editors/resources/reactionsearch.jsp">
                <li class="reactions">
                	<a href="<mm:url page="../resources/ReactionInitAction.do"/>" target="rightpane"><fmt:message key="modules.reactions" /></a>
                </li>
            </mm:haspage>
            <mm:haspage page="/editors/versioning/modules.jsp">
                <li class="versioning">
                	<a href="<mm:url page="../versioning/modules.jsp"/>" target="rightpane"><fmt:message key="modules.versioning" /></a>
                </li>
            </mm:haspage>
            <mm:hasrank minvalue="administrator">
	            <cmsc:hasfeature name="luceusmodule">
					<li class="luceus">
						<a href="../luceus/fullindex.jsp" target="rightpane"><fmt:message key="modules.fullindex" /></a>
					</li>
	            </cmsc:hasfeature>
	            <cmsc:hasfeature name="spidermodule">
	               <li class="luceus">
	                  <a href="../nijmegen/spiders.jsp" target="rightpane">Nijmegen indexering GNS etc.</a>
	               </li>
	            </cmsc:hasfeature>
					<mm:haspage page="/editors/publish-remote">
	               <li class="advancedpublish">
	                  <c:url var="publishUrl" value="/editors/publish-remote/index.jsp"/>
	                  <a href="${publishUrl}" target="rightpane"><fmt:message key="modules.publish" /></a>
	               </li>
	            </mm:haspage>
	            <mm:haspage page="/editors/workflow">
	               <li class="workflow">
	                  <c:url var="workflowUrl" value="/editors/workflow/admin/WorkflowAdminAction.do"/>
	                  <a href="${workflowUrl}" target="rightpane"><fmt:message key="modules.workflow" /></a>
	               </li>
	            </mm:haspage>
	            <mm:haspage page="/editors/knownvisitor-ntlm">
	               <li class="visitor">
	                  <c:url var="visitorNtlmUrl" value="/editors/knownvisitor-ntlm/index.jsp"/>
	                  <a href="${visitorNtlmUrl}" target="rightpane"><fmt:message key="modules.knowvisitor-html" /></a>
	               </li>
	            </mm:haspage>
	            <mm:haspage page="/editors/messageoftheday">
	               <li class="messageoftheday">
	                  <c:url var="messageofthedayUrl" value="/editors/messageoftheday/index.jsp"/>
	                  <a href="${messageofthedayUrl}" target="rightpane"><fmt:message key="modules.messageoftheday" /></a>
	               </li>
	            </mm:haspage>
	         </mm:hasrank>
            <mm:haspage page="/editors/egemmail">
               <li class="egem">
                  <a href="../egemmail/search.jsp" target="rightpane"><fmt:message key="modules.egemmail.export" /></a>
               </li>
               <li class="egem">
                  <a href="<cmsc:property key="egemmail.beheer.path"/>" target="_blank"><fmt:message key="modules.egemmail.admin" /></a>
               </li>
            </mm:haspage>
         </ul>
			
			<div class="side_block_end"></div>
		</div>
		</mm:hasrank>
	</mm:haspage>
</mm:cloud>
</body>
</html:html>
</mm:content>