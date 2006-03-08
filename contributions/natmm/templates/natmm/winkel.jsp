<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<% if(rubriekExists&&pageExists) { %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<%@include file="includes/top5a_breadcrumbs.jsp" %>
</table>
<%
String sQuery = request.getParameter("query");
if(sQuery==null) { sQuery = ""; }
%>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td align="center">
	<iframe src="http://natmmww.asp4all.nl/" width="744" height="100%" scrolling="yes" frameborder="0"></iframe>
	</td></tr>
</table>
<table align="center" width="100%" height="100%">
</table>

<%@include file="includes/footer.jsp" %>
</cache:cache>
<% } %>
</mm:cloud>
