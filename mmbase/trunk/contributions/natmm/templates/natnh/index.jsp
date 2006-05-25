<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%
if(!paginaID.equals("-1")) {
	%><mm:redirect page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" /><%
}
%>
</mm:cloud>