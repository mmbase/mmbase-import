<%@page isErrorPage="true" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%>
<mm:content type="text/html"  expires="0">
<html>
<head>
  <title>Taglib examples - ERROR-PAGE</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
  <%@ include file="menu.jsp"%>
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
  
  
</body>
</html>
</mm:content>
