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

  Stacktrace:
  <% java.util.Stack stack = new java.util.Stack();
     Throwable e = exception;
     while (e != null) {
        stack.push(e);
        e = e.getCause();
     }
        String intro = "";
     while (! stack.isEmpty()) { 
  Throwable t = (Throwable) stack.pop();
  %>
  
  <h2><%= intro + "" + t.getClass().getName() + " : " + t.getMessage() %></h2>
  <pre>
    <%= org.mmbase.util.logging.Logging.stackTrace(t) %>
  </pre>
  <% 

     intro = "Wrapped in: ";   } %>
  <p class="navigate">Continue <a href="<%=response.encodeURL("search_node.jsp")%>"><span class="next"></span><span class="alt">[->]</span></a></p>
  
  <hr />
  Please contact your system administrator about this.
  
</body>
</html>
</mm:content>
