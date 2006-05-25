<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>

<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<%
String thisArticle = "";
if(!articleId.equals("-1")) {
	thisArticle = "artikel.number = '" + articleId + "'";
}
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="<%= thisArticle %>">
	<%@include file="includes/relatedarticle.jsp" %>
</mm:list>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>