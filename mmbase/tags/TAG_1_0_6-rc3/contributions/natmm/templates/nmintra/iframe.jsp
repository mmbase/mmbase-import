<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" 
%><td colspan="2" rowspan="2" height="100%"><%
if(isPreview) {
   session.setAttribute("root",subsiteID);
   session.setAttribute("rubriek",rubriekId);
   session.setAttribute("page",paginaID);
}
%>
		<mm:list nodes="<%= paginaID %>" path="pagina,posrel,link" max="1">
        	<iframe src="<mm:field name="link.url"/>" title="<mm:field name="link.titel"/>" width="100%" height="100%" frameborder="0">
        		<a href="<mm:field name="link.url"/>" target="_blank"><mm:field name="link.titel"/></a>
        	</iframe>
        	<mm:import id="urlexists"/>
        </mm:list>
        <mm:notpresent referid="urlexists">Error: no url specified for page with external website template</mm:notpresent>
        <mm:remove referid="urlexists" />
</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
