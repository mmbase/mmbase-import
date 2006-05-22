<%@include file="/taglibs.jsp" %>
<% boolean isProduction = false; %>
<mm:cloud jspvar="cloud" method="<%= (isProduction ? "" : "http") %>" rank="<%= (isProduction ? "" : "basic user") %>">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%

String sPageRef = (String) session.getAttribute("pageref");
if(sPageRef!=null&&!sPageRef.equals(paginaID)) { // set pagerefminone to sPagRef, set pageref to paginaID
   session.setAttribute("pagerefminone",sPageRef);
}
session.setAttribute("pageref",paginaID);

if(!paginaID.equals("-1")) {
	%><mm:redirect page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" /><%
}
%>
</cache:cache>
</mm:cloud>