<%@ page isErrorPage="true" %>

Something went wrong:
<p>
<%
    /**
     * exception.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: exception.jsp,v 1.2 2002-05-28 14:15:14 pierre Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */

    String message = exception.getMessage();
    if (message == null) {
        message = exception.toString();
    }
    java.util.StringTokenizer lines = new java.util.StringTokenizer(message,"\n\r");
    while (lines.hasMoreElements()){
        %><%= lines.nextElement() %><br /><%
    }
%>
</p>
For techies:
<pre>
    <%= org.mmbase.util.logging.Logging.stackTrace(exception)%>
</pre>
</div>