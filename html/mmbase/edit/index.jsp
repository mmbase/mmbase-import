<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><mm:content type="text/html">
<html>
  <head>
    <% String url = response.encodeURL("basic/search_node.jsp"); %>
    <meta http-equiv="refresh" content="0; url=<%=url%>">
    <title>MMBase generic editors</title>
  </head>
  <body>
    <a href="<%=url %>">Redirecting to main editor page</a>
  </body>
</html>
</mm:content>