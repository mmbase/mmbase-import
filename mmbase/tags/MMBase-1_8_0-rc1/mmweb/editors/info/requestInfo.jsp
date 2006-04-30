<%@ page import="javax.servlet.http.*" %>
<html>
<body bgcolor="white">
<h1> Request Information </h1>
<font size="4">
<%@ page session="false" %>JSP Request Method: <%= request.getMethod() %><br>
Request URI: <%= request.getRequestURI()  %><br>
Request Protocol: <%= request.getProtocol() %><br>
Servlet path: <%= request.getServletPath() %><br>
Path info: <%= request.getPathInfo() %><br>
Path translated: <%= request.getPathTranslated() %><br>
Query string: <%= request.getQueryString() %><br>
Content length: <%= request.getContentLength() %><br>
Content type: <%= request.getContentType() %><br>
Server name: <%= request.getServerName() %><br>
Server port: <%= request.getServerPort() %><br>
Remote user: <%= request.getRemoteUser() %><br>
Remote address: <%= request.getRemoteAddr() %><br>
Remote host: <%= request.getRemoteHost() %><br>
Authorization scheme: <%= request.getAuthType() %> <br>
The browser you are using is <%= request.getHeader("User-Agent") %><br>
----<br>
Request URL: <%= HttpUtils.getRequestURL(request) %><br>

<script language="javascript" type="text/javascript">
<!--               
	var windowWidth, windowHeight;
	if (window.innerWidth) {
		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;
	} else if (document.body) {
		windowWidth = document.body.clientWidth;
		windowHeight = document.body.clientHeight;
	}
	document.write("windowHeight=");document.write(windowHeight);
	document.write("&windowWidth=");document.write(windowWidth);
//-->           
</script>

</font>
</body>
</html>
