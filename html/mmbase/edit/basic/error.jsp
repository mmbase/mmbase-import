<%@ page isErrorPage="true" import="java.util.*" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><html>
<head>
<mm:write referid="style" />
<title>MMBase editors - Error</title>
</head>
<body>
<h1>Sorry, an error happened</h1>
<h2><%= exception.getMessage() %></h2>
Stacktrace:
<pre>
  <%= org.mmbase.util.logging.Logging.stackTrace(exception) %>
</pre>

<p>Click <a href="<%=response.encodeURL("search_node.jsp")%>">here</a> to continue.</p>
<hr />
Please contact your system administrator about this.

</body>
</html>
