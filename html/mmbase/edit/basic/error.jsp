<%@page isErrorPage="true" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><html>
<head>
<mm:import id="style_sheet" externid="mmjspeditors_style" from="cookie">mmbase.css</mm:import>
<style type="text/css">@import url(css/<mm:write referid="style_sheet" />);</style></mm:import>
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
