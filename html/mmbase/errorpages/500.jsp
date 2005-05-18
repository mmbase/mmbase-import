<%@page isErrorPage="true" import="org.mmbase.bridge.*,java.util.*" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%>
<mm:log jspvar="log">
<%
int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
String domain = request.getServerName();
if (domain.startsWith("www.")) {
  domain = domain.substring(4);
}
String webmasterEmail = "webmaster@"+domain;

// prepare error details
StringBuffer msg = new StringBuffer();
{

	msg.append("Headers\n\n");
    // request properties
    Enumeration en = request.getHeaderNames();
    while (en.hasMoreElements()) {
        String name = (String) en.nextElement();
        msg.append(name+": "+request.getHeader(name)+"\n");
    }
    msg.append("\n");
    msg.append("method: ").append(request.getMethod()).append("\n");
    msg.append("querystring: ").append(request.getQueryString()).append("\n");
    msg.append("requesturl: ").append(request.getRequestURL()).append("\n");
	msg.append("mmbase version: ").append(org.mmbase.Version.get()).append("\n\n");
    

    msg.append("Parameters\n\n");
    // request parameters
    en = request.getParameterNames();
    while (en.hasMoreElements()) {
        String name = (String) en.nextElement();
        msg.append(name).append(": ").append(request.getParameter(name)).append("\n");
    }
    msg.append("\n");

    java.util.Stack stack = new java.util.Stack();

    Throwable e = exception;
     while (e != null) {
        stack.push(e);
         if (e instanceof NotFoundException) {
             status = HttpServletResponse.SC_NOT_FOUND;
             response.setStatus(status);
         }
        e = e.getCause();
     }

     msg.append("status: ").append(status).append("\n\n");
     String intro = "" + stack;
     while (! stack.isEmpty()) { 
         Throwable t = (Throwable) stack.pop();
         // add stack stacktraces
         if (t != null) {
         msg.append(t.getMessage()).append("\n");
  //         msg.append(org.mmbase.util.logging.Logging.stackTrace(t)).append("\n");
         }

    } 
}

// prepare error ticket
String ticket = System.currentTimeMillis()+"";

// write errors to mmbase log
log.error(ticket+":\n" + msg);

%>
<mm:content type="text/html"  expires="0">
<html>
<head>
  <mm:import id="title">MMBase - Error <%= status %></mm:import>
  <title><mm:write referid="title" /></title>
  <%@include file="meta.jsp" %>
</head>
<body class="basic">
  <h1><mm:write referid="title" /></h1>
  <h1><%= exception.getMessage() %></h1>
  <h2>Error ticket: <%= ticket %></h2>
  <% String referrer = request.getHeader("Referer");
     if (referrer != null) {
  %>
  <p><a href="<%= referrer %>">back</a></p>
  <% } %>
  <mm:write value="<%=msg.toString()%>" escape="p" />
  
  <hr />
  Please contact your system administrator about this.
  
</body>
</html>
</mm:content>
</mm:log>
