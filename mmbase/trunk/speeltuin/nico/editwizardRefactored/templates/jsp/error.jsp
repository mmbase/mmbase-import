<%@ page isErrorPage="true" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page import="org.mmbase.applications.editwizard.Controller"
%><%@ page import="org.mmbase.applications.editwizard.action.*"
%><%
if (exception==null) {
  exception = (Exception)request.getAttribute(Controller.ATTRKEY_EXCEPTION);
}
if (exception==null) {
  exception = new javax.servlet.jsp.JspException("dummy-exception, to test the errorpage-page");
}
Exception rootCause = null;
if (exception instanceof ServletException) {
  rootCause = (Exception)((ServletException)exception).getRootCause();
}
if (rootCause!=null) {
  rootCause = (Exception)exception.getCause();
}
%><html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>DON'T PANIC - But Something Went Wrong</title>
<link href="../style/layout/exception.css" type="text/css" rel="stylesheet">
<link href="../style/color/exception.css" type="text/css" rel="stylesheet">
</head>
<body>
<table class="head">
<tr class="headtitle"></tr>
<tr class="headsubtitle"></tr>
</table>
<h2>DON'T PANIC!</h2>
<h3>But Something Went Wrong</h3>
<p>
      An error occurred in the editwizards. This may be caused because you have insufficient rigths to make
      changes, because your edit-session expired, or because the editwizard definition has a bug.
    </p>
<p>
      When reporting the error, pass the error message (in red, below) and if so requested the expanded message
      to the responsible party.
    </p>
<p>
<a href="http://localhost:9080/mmbase-webapp/mmbase/edit/wizard/jsp/wizard.jsp?proceed=true&sessionkey=editwizard">Return Home.</a>
</p>
<h3 class="error">Error: <%=ActionUtils.escapeForHTML(exception.getMessage())%> </h3>
<small>
<h3 class="expandederror">Expanded error:</h3>
<pre><%=ActionUtils.getStackTrace(exception)%></pre>
</small>
<% if (rootCause!=null) { %>
<br/>
<h3 class="error">Error: <%=ActionUtils.escapeForHTML(rootCause.getMessage())%> </h3>
<small>
<h3 class="expandederror">Expanded error:</h3>
<pre><%=ActionUtils.getStackTrace(rootCause)%></pre>
</small>
<% } %>
</body>
</html>
