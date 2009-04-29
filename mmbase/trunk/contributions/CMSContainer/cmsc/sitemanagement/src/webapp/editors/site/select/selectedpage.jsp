<%@page import="com.finalist.cmsc.services.sitemanagement.SiteManagement" %>
<%@page import="java.util.ArrayList,java.util.Collections" %>
<%
String related = request.getParameter("related");
String pageParam = request.getParameter("page");
String path = request.getParameter("path");
ArrayList<String> positions = new ArrayList<String>(SiteManagement.getPagePositions(pageParam));
Collections.sort(positions);
%>
<script type="text/javascript">
	var positions = new Array();
	<% for (String name : positions) { %>
       positions[positions.length] = '<%= name %>';
	<% } %>
	<% if (related!=null && "true".equals(related)) { %>
       opener.selectRelatedpage('<%= pageParam %>', '<%= path %>', positions);
	<% } else { %>
       opener.selectPage('<%= pageParam %>', '<%= path %>', positions);
	<% } %>
	close();
</script>
