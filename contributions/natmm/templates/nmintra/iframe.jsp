<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><td colspan="2" rowspan="2"><%
if(session.getAttribute("editor")!=null) {
        session.setAttribute("website",websiteId);
        session.setAttribute("rubriek",rubriekId);
        session.setAttribute("page",pageId);
}
%><mm:list nodes="<%= pageId %>" path="pagina,posrel,link" max="1"
        ><iframe src="<mm:field name="link.url"/>" title="<mm:field name="link.titel"/>" width="100%" height="527px" frameborder="0">
        <a href="<mm:field name="link.url"/>" target="_blank"><mm:field name="link.titel"/></a>
        </iframe><mm:import id="urlexists"
/></mm:list
><mm:notpresent referid="urlexists">Error: no url specified for page with external website template</mm:notpresent
><mm:remove referid="urlexists"/>
</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
