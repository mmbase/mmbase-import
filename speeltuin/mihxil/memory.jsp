<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
  %><%@ page import="java.util.*,org.mmbase.util.*,org.mmbase.cache.Cache" %>
<html>
<head><title>Memory</title></head>
<body>
 Size of <a href="session.jsp">session:</a> <%= SizeOf.getByteSize(session) %> byte<br />
 <%
      Runtime rt = Runtime.getRuntime();
      out.println("total memory      : " + rt.totalMemory() / (1024 * 1024) + " Mbyte<br />");
      if ("yes".equals(request.getParameter("collect"))) rt.gc();
      out.println("free memory       : " + rt.freeMemory() / (1024 * 1024) + " Mbyte<br />");
%>
<hr />
<a href="<mm:url><mm:param name="collect">yes</mm:param></mm:url>">Collect garbage</a><br />
<a href="<mm:url><mm:param name="collect">no</mm:param></mm:url>">Don't collect garbage</a><br />
<a href="<mm:url page="/" />">Home </a>

</body>
</html>
