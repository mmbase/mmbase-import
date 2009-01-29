<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title>Clean icaches</title>
	</head>
	<body>
<mm:cloud jspvar="cloud" rank="basic user" method="http">

<mm:listnodes type="icaches">
   Found <mm:size/> icaches.<br/> 
</mm:listnodes>
   
Cleaning...
	<mm:listnodes type="icaches">
		<mm:deletenode />
	</mm:listnodes>
Done.
</mm:cloud>
	</body>
</html:html>
