<html>
<% String sWarning = request.getParameter("warning"); 
	if (sWarning!=null&&sWarning.equals("true")) { %>
<frameset columns="80,*,80" framespacing="0" frameborder="0" cols="*,*,*">
  <frame src="news.html" name="leftpane" frameborder="0" scrolling="no">
  <frame src="usermanagement/changepassword.jsp?status=gracelogin" name="middlepane" frameborder="0" scrolling="auto">
  <frame src="news.html" name="rightpane" frameborder="0" scrolling="no">
</frameset>
<%	} else {%>
	<%@include file="news.html" %>
<% } %>
</html>
