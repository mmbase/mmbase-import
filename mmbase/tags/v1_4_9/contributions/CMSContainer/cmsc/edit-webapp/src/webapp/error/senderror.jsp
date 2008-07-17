<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"  %>
<%@page import="com.finalist.cmsc.mmbase.EmailUtil"%>
<%@page import="com.finalist.cmsc.mmbase.PropertiesUtil"%>
<%@page import="com.finalist.cmsc.security.SecurityUtil"%>
<%@page import="java.io.*"%>
<%
try {
	String type = request.getParameter("messagetype");
	String ticket = request.getParameter("ticket");
	String message = request.getParameter("message");
	String email = PropertiesUtil.getProperty("mail.error.email");

	if (email != null && message != null) {
		EmailUtil.send(email, email, type + " - " + ticket, message);
	}
}
catch(Throwable exception) {
    Logger log = Logging.getLoggerInstance("ERROR-JSP");
	// add stack stacktrace
    StringWriter wr = new StringWriter();
    PrintWriter pw = new PrintWriter(wr);
    pw.println("EXCEPTION senderror:");
    exception.printStackTrace(new PrintWriter(wr));
	log.error(wr.toString());
}
%>
<html>
<head>
</head>
<body>
	Error has been submitted.
</body>
</html>