<%@ page isErrorPage="true" %>

Something went wrong:
</p>
<%
	String message = exception.getMessage();
	if (message == null){message = "but, sadly, no useful message was given.";}
	java.util.StringTokenizer lines = new java.util.StringTokenizer(message,"\n\r");
	while (lines.hasMoreElements()){
		%><%= lines.nextElement() %><br /><%
	}
%>
</p>
<a href="#" onclick="document.all['stacktrace'].style.visibility = 'visible'; return false;">For techies</a>:
</p>
<div id="stacktrace" style="visibility: hidden">
<%
	java.io.StringWriter stack = new java.io.StringWriter();
	exception.printStackTrace(new java.io.PrintWriter(stack));

	java.util.StringTokenizer stages = new java.util.StringTokenizer(stack.toString(),"\n\r");
	while (stages.hasMoreElements()){
		%><%= stages.nextElement() %><br /><%
	}
%>
</div>