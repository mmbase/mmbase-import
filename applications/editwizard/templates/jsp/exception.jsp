<%@ page isErrorPage="true" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<% if(exception == null) exception = new javax.servlet.jsp.JspException("dummy-exception, to test the errorpage-page"); %>
<html>
<head>
    <title>Something wend wrong</title>
</head>
<body>
<h2>Something went wrong:</h2>
<h3><font color="#ff0000">
<%
    /**
     * exception.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: exception.jsp,v 1.3 2002-07-18 11:29:57 eduard Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */
    String message = exception.getMessage();
    if (message == null) {
        message = exception.toString();
    }
    java.util.StringTokenizer lines = new java.util.StringTokenizer(message,"\n\r");
    
    // only show 1 line, otherwise we still could get very difficult and compilcated messages
    if(lines.hasMoreElements()) {
        out.println(org.mmbase.util.Encode.encode("ESCAPE_HTML", lines.nextToken()));
    }
%>
</font></h3>
<% if(lines.hasMoreElements()) { %>
<small>(exception was mulitple lines, but timmed to one line)</small>
<% } %>
<h3>For techies:</h3>
<small><pre>
<mm:formatter format="escapexml">
    <%= org.mmbase.util.logging.Logging.stackTrace(exception)%>
</mm:formatter> 
</pre></small>
</body></html>