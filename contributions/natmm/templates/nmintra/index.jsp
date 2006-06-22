<%@include file="/taglibs.jsp" %>
<% boolean isProduction = false; %>
<mm:cloud jspvar="cloud" method="<%= (isProduction ? "" : "http") %>" rank="<%= (isProduction ? "" : "basic user") %>">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%
if(!paginaID.equals("-1")) {
	%><mm:redirect page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" /><%
}
%>
</cache:cache>
</mm:cloud>