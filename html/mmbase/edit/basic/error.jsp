<%@page isErrorPage="true" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%>
<mm:content type="text/html"  expires="0">
<html>
<head>
  <mm:import id="style_sheet" externid="mmjspeditors_style" from="cookie">mmbase.css</mm:import>
  <style type="text/css">@import url(css/<mm:write referid="style_sheet" escape="none" />);</style>
  <title>MMBase editors - Error</title>
</head>
<body class="basic">
  <h1>Sorry, an error happened</h1>
  <h2><%= exception.getMessage() %></h2>
  Stacktrace:
  <pre>
    <%= org.mmbase.util.logging.Logging.stackTrace(exception) %>
  </pre>
  <p class="navigate">Continue <a href="<%=response.encodeURL("search_node.jsp")%>"><span class="next"></span><span class="alt">[->]</span></a></p>
  
  <hr />
  Please contact your system administrator about this.
  
</body>
</html>
</mm:content>
