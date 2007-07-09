<%@ page isErrorPage="true"
%><%@ page import="java.io.*,java.util.*,org.mmbase.bridge.*,org.mmbase.util.*"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="settings.jsp"
%><mm:content language="$language" type="text/html">
<mm:cloud method="asis" jspvar="cloud">
  <html>
    <head>
    <title><%=getPrompt(m, "title")%> - Error page</title>
    <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
    <link rel="icon" href="images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
  </head>
    <body>
        <mm:log jspvar="log">
<%
    String domain = request.getServerName();
    if (domain.startsWith("www.")) {
        domain = domain.substring(4);
    }
    String webmasterEmail = "webmaster@"+domain;

    // prepare error details
    String msg = "";
    {
	// request properties
	Enumeration en = request.getHeaderNames();
	while (en.hasMoreElements())
	{
	    String name = (String) en.nextElement();
	    msg += name+": "+request.getHeader(name)+"\n";
	}
	msg += "\n";
	msg += "method: "+request.getMethod()+"\n";
	msg += "querystring: "+request.getQueryString()+"\n";
	msg += "requesturl: "+request.getRequestURL()+"\n";
	msg += "\n";

	// request parameters
	en = request.getParameterNames();
	while (en.hasMoreElements())
	{
	    String name = (String) en.nextElement();
	    msg += name+": "+request.getParameter(name)+"\n";
	}
	msg += "\n";

  if (cloud.getUser() != null) {
      msg += "cloud-user: " + cloud.getUser().getIdentifier() + " / " + cloud.getUser().getRank() + "\n";
  } else {
     msg += "cloud-user: NULL\n";
 }
	msg += "\n";


	// add stack stacktrace
	if (exception instanceof BridgeException
		&& ((BridgeException)exception).getCause() != null)
	{
	    StringWriter wr = new StringWriter();
	    PrintWriter pw = new PrintWriter(wr);
	    pw.println("Exception:");
	    exception.printStackTrace(new PrintWriter(wr));
	    pw.println("\nCause:");
	    Throwable ex = ((BridgeException)exception).getCause();
	    ex.printStackTrace(new PrintWriter(wr));
	    msg += wr.toString();
	}
	else
	{
	    StringWriter wr = new StringWriter();
	    PrintWriter pw = new PrintWriter(wr);
	    pw.println("Exception:");
	    exception.printStackTrace(pw);
	    msg += wr.toString();
	}
    }

    // prepare error ticket
    java.text.DateFormat simple = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
    String ticket = simple.format(new java.util.Date());

    // write errors to mmbase log
    log.error(ticket+":\n"+msg);
%>
  <div id="content">
	<h1><%=getPrompt(m, "oops")%></h1>
	<p>
    <%=getPrompt(m, "wentwrong")%> <%= exception.getMessage() %>.
  </p>
  <p>
    <a href="<mm:url referids="language" page="index.jsp" />">terug</a>
  </p>
  <p>
    <%=getPrompt(m, "doaftererror")%>
	</p>
	<p>
	    <font color="red"><big><%= ticket %></big></font>
	</p>
	<p>
    <%=getPrompt(m, "doaftererror2")%>
	</p>
<textarea rows="100" style="width: 100%;">
<%= Encode.encode("ESCAPE_XML",msg) %>
</textarea>
   </div>
</mm:log>
    </body>
</html>
</mm:cloud>
</mm:content>