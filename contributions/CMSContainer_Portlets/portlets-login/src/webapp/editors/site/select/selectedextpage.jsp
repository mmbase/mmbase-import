<%@page import="com.finalist.cmsc.services.sitemanagement.SiteManagement" %>
<%@page import="java.util.ArrayList,java.util.Collections" %>
<%
String pageParam = request.getParameter("page");
String path = request.getParameter("path");
String method = request.getParameter("method");
%>
<script type="text/javascript">
	opener.<%= method %>('<%= pageParam %>', '<%= path %>');
	close();
</script>
