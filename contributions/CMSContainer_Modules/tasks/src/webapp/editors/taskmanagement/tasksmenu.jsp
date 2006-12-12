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
	<mm:haspage page="/editors/taskmanagement/">
	  <mm:hasrank minvalue="basic user">
		<div class="side_block">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="tasks.title" /></div>
				<div class="header_end"></div>
			</div>
			
			<ul class="shortcuts">
               <li class="tasks">
					<a href="../taskmanagement/tasklist.jsp" target="rightpane"><fmt:message key="tasks.tasks" /></a>
				</li>
               <li class="newtask">
               <a href="<mm:url page="/editors/WizardInitAction.do">
					<mm:param name="objectnumber" value="new"/>
					<mm:param name="contenttype" value="task"/>
       				<mm:param name="returnurl" value="/nijmegen-staging/editors/taskmanagement/tasklist.jsp"/>
			   </mm:url>" target="rightpane"><fmt:message key="tasks.new" /></a>
				</li>               
         </ul>
			
			<div class="side_block_end"></div>
		</div>
	  </mm:hasrank>
	</mm:haspage>
</mm:cloud>
</body>
</html:html>
</mm:content>