<%
String sWarning = request.getParameter("warning");
if (sWarning!=null&&sWarning.equals("true")) {
	%>
	<jsp:include page="usermanagement/changepassword.jsp?status=gracelogin" />
	<%	
} else {
	%>
	<%@include file="news.html" %>
	<% 
} %>

