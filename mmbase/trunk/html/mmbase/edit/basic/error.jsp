<%@ page isErrorPage="true" import="java.util.*" 
%><%@ include file="page_base.jsp"
%><mm:cloud method="http" sessionname="${SESSION}" jspvar="cloud">
<mm:write referid="style" />
<title>MMBase editors - Error</title>
</header>
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

<%@ include file="foot.jsp"  %>
</mm:cloud>
