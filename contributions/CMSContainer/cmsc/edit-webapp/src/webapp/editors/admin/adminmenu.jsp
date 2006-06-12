<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<title><fmt:message key="admin.title" /></title>
	<link href="../style.css" type="text/css" rel="stylesheet"/>
</head>
<body class="leftmenu">
	
<mm:cloud jspvar="cloud" loginpage="../login.jsp">

<h2><fmt:message key="admin.title" /></h2>
<table cellpadding=1 cellspacing=0>
   <tr>
      <td>
      		<a target="rightpane" href="../usermanagement/userlist.jsp" class="leftmenu"><fmt:message key="admin.users" /></a>
      </td>
   </tr>
   <tr>
      <td>
      		<a target="rightpane" href="../WizardListAction.do?nodetype=properties" class="leftmenu"><fmt:message key="admin.settings" /></a>
      </td>
   </tr>
   <tr>
      <td>
      		<a target="rightpane" href="../WizardListAction.do?nodetype=layout" class="leftmenu"><fmt:message key="admin.layouts" /></a>
      </td>
   </tr>
   <tr>
      <td>
      		<a target="rightpane" href="../WizardListAction.do?nodetype=view" class="leftmenu"><fmt:message key="admin.views" /></a>
      </td>
   </tr>
   <tr>
      <td>
      		<a target="rightpane" href="../WizardListAction.do?nodetype=stylesheet" class="leftmenu"><fmt:message key="admin.stylesheets" /></a>
      </td>
   </tr>
   <tr>
      <td>
      		<a target="rightpane" href="../WizardListAction.do?nodetype=portletdefinition" class="leftmenu"><fmt:message key="admin.portletdefinitions" /></a>
      </td>
   </tr>
</table>

</mm:cloud>
</body>
</html:html>
</mm:content>