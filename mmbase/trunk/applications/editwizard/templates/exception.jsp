<%@ page isErrorPage="true" %>

Something went wrong:
<p>
<%
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