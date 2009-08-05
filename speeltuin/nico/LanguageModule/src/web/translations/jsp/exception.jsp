<%@ page isErrorPage="true" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page import="org.mmbase.applications.editwizard.Config"
%><% if(exception == null) exception = new javax.servlet.jsp.JspException("dummy-exception, to test the errorpage-page"); %>
<html>
<head>
    <title>DON'T PANIC - But Something Went Wrong</title>
</head>
<body>
<h2>DON'T PANIC!</h2>
<h3>But Something Went Wrong</h3>
<p>An error occurred in the editwizards. This may be caused because you have insufficient rigths to make changes,
because your edit-session expired, or because the editwizard definition has a bug.</p>
<p>When reporting the error, pass the error message (in red, below) and if so requested the expanded message to the responsible party.</p>
<%
String sessionKey = request.getParameter("sessionkey");
if (sessionKey == null) sessionKey = "editwizard";
Config ewConfig = (Config)session.getAttribute(sessionKey);
if (ewConfig!=null) {
%>
<p><a href="<mm:url page="<%=ewConfig.backPage%>" />">Return Home.</a></p>
<%
}
%>
<h3>Error: <font color="#ff0000">
<%
    /**
     * exception.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: exception.jsp,v 1.1 2003-12-06 14:24:55 nico Exp $
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
<h3>Expanded error:</h3>
<small><pre>
<mm:formatter format="escapexml">
    <%= org.mmbase.util.logging.Logging.stackTrace(exception)%>
</mm:formatter>
</pre></small>
</body></html>
