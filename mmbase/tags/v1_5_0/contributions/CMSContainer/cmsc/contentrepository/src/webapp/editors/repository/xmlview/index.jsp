<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp" %>
<mm:cloud>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<mm:import externid="number" />

<mm:notpresent referid="number">
	<body>
		Channel parameter missing.
	</body>
</mm:notpresent>

<mm:present referid="number">
	<frameset rows="25,*" framespacing="0" border="0">
		<frame name="select" src="select.jsp?number=<mm:write referid="number"/>"  frameborder="0"/>
		<frame name="xml"  frameborder="0" src="../../empty.html"/>
	</frameset>
</mm:present>

</html:html>
</mm:cloud>