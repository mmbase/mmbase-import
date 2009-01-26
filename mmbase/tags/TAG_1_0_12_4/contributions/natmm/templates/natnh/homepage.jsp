<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<mm:import id="ishomepage" />
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" orderby="contentrel.pos" max="1">
	 <%@include file="includes/relatedarticle.jsp" %>
</mm:list>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>