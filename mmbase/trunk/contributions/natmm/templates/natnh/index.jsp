<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
	<%@include file="includes/templateheader.jsp" %>
	<mm:redirect page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" />
</mm:cloud>