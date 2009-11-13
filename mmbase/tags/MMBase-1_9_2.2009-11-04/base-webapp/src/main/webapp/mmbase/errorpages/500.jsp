<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page isErrorPage="true" import="org.mmbase.bridge.*,java.util.*,javax.mail.*,javax.mail.internet.*"
%><% response.setStatus(500);
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0"  prefix="mm"
%>
<mm:content type="text/html"  expires="0">
<html>
<head>
  <mm:import id="title">MMBase - Error ${requestScope['javax.servlet.error.status_code']}</mm:import>
  <title><mm:write referid="title" /></title>
  <jsp:directive.include file="meta.jsp" />
  <script type="text/javascript" language="javascript">
    function show() {
    document.getElementById('error').style.display = 'block';
    document.getElementById('show').style.display = 'none';
}
    function hide() {
    document.getElementById('error').style.display = 'none';
    document.getElementById('show').style.display = 'block';
}
  </script>
</head>
<body class="basic">
  <h1><mm:escape escape="text/xml">Error ${requestScope['javax.servlet.error.status_code']} -  ${requestScope['javax.servlet.error.exception_type']}</mm:escape></h1>
  <h2><mm:escape escape="text/xml">${requestScope['javax.servlet.error.message']}</mm:escape></h2>
  <a id="show" href="javascript:show();">Show error</a>
  <div id="error" style="background-color:yellow; display: none;">
    <a href="javascript:hide();">Hide error</a>
    <pre><mm:exception exception="${requestScope['javax.servlet.error.exception']}" /></pre>
  </div>
  <hr />
  Please contact your system administrator about this.
  </body>
</html>
</mm:content>
