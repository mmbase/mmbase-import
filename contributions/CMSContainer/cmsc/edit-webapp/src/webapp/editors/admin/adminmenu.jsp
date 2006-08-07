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
				<div class="title"><fmt:message key="admin.title" /></div>
				<div class="header_end"></div>
			</div>
			
			<ul class="shortcuts">
               <li class="users">
					<a href="../usermanagement/userlist.jsp" target="rightpane"><fmt:message key="admin.users" /></a>
				</li>
               <li class="properties">
					<a href="../WizardListAction.do?nodetype=properties" target="rightpane"><fmt:message key="admin.settings" /></a>
				</li>
               <li class="layouts">
					<a href="../WizardListAction.do?nodetype=layout"  target="rightpane"><fmt:message key="admin.layouts" /></a>
				</li>
               <li class="views">
					<a href="../WizardListAction.do?nodetype=view"  target="rightpane"> <fmt:message key="admin.views" /></a>
				</li>
               <li class="stylesheets">
					<a href="../WizardListAction.do?nodetype=stylesheet" target="rightpane"><fmt:message key="admin.stylesheets" /></a>
				</li>
               <li class="portletdefinitions">
					<a href="../WizardListAction.do?wizardname=singleportletdefinition" target="rightpane"><fmt:message key="admin.singleportletdefinitions" /></a>
				</li>
               <li class="portletdefinitions">
					<a href="../WizardListAction.do?wizardname=multiportletdefinition" target="rightpane"><fmt:message key="admin.multiportletdefinitions" /></a>
				</li>
               <li class="dumpdefaults">
					<a href="dumpdefaults.jsp" target="rightpane"><fmt:message key="admin.dumpdefaults" /></a>
				</li>
            <cmsc:hasfeature name="luceusmodule">
				<li class="dumpdefaults">
					<a href="fullindex.jsp" target="rightpane"><fmt:message key="admin.fullindex" /></a>
				</li>
            </cmsc:hasfeature>
				
			</ul>
			
			<div class="side_block_end"></div>
		</div>
		</mm:hasrank>
	</mm:haspage>
</mm:cloud>
</body>
</html:html>
</mm:content>