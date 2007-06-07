<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="dashboard.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
</head>
<body>

<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:import externid="nodenumber"/>
<form method="post">
	<input type="text" name="nodenumber" value=""/>
	<input type="submit" value="index"/>
</form>

<mm:present referid="nodenumber">
<mm:node referid="nodenumber" jspvar="node">
<%
	com.finalist.cmsc.module.luceusmodule.LuceusModule luceusModule 
		= (com.finalist.cmsc.module.luceusmodule.LuceusModule)
		 org.mmbase.module.Module.getModule("luceusmodule");
	if (luceusModule != null) {
		luceusModule.updateSecondaryContentIndex(node.getNumber());
	}
%>
nodenumber:	<mm:write referid="nodenumber" /><br />
indexed
</mm:node>
</mm:present>
</mm:cloud>

</body>
</html:html>
</mm:content>