<%@ page isErrorPage="true"%>
<%@ page import="java.io.*,java.util.*,org.mmbase.bridge.*,org.mmbase.util.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
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
    String ticket = System.currentTimeMillis()+"";

    // write errors to mmbase log
    log.error(ticket+":\n"+msg);
%>
<HTML>
    <HEAD>
	<TITLE>Error page</TITLE>
    </HEAD>
    <BODY>
	<H1>Oeps, an error has occurred!</H1>
	<P>
	    An error has occured processing your previous
	    request.  Please contact the webmaster (possibly at
	    <A href="mailto:<%= webmasterEmail %>"><%= webmasterEmail %></A>)
	    and include error ticket number:
	</P>
	<DIV align="center">
	    <FONT color="red"><BIG><%= ticket %></BIG></FONT>
	</DIV>
	<P>
	    and optionally include the following error messages.
	</P>
	<TABLE border="1"><TR><TD>
<PRE>
<%= Encode.encode("ESCAPE_XML",msg) %>
</PRE>
	</TD></TR></TABLE>
    </BODY>
</HTML>
</mm:log>
</mm:cloud>
