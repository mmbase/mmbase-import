<%@ page isErrorPage="true" import="java.util.*" %>
<%@ page import="org.mmbase.bridge.*"%>
<%@ page import="java.io"%>
<html>
<head>
<title>News error</title>
<link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>

<h1>News error page</h1>


<b>Oops! an error occurred.</b> Is the MyNews application installed?<BR>
<HR>
<%= exception.getMessage() %><BR>
<HR>

<PRE>
<H2>cause</H2>
<% PrinwWriter pw = new PrintWriter(out);
    exception.printStackTrace(pw) ;
%>
</PRE>
<% if (exception instanceof BridgeException) { %>
<H2>Root cause</H2>
<PRE>
<% PrinwWriter pw2 = new PrintWriter(out);
    ((BridgeException)exception).getCause().printStackTrace(pw2) ;
%>

</PRE>
<% } %>
</table>
</body>
</html>
