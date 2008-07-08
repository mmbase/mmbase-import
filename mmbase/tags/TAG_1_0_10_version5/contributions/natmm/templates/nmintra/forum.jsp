<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" 
%><td colspan="2" rowspan="2">
<% String forumId = ""; %>
<mm:node number="<%= paginaID %>" id="this_page">
    <mm:relatednodes type="forums" max="1">
        <mm:field name="number" jspvar="dummy"  vartype="String" write="false"><% forumId = dummy; %></mm:field>
    </mm:relatednodes>
</mm:node>
<% if(forumId.equals("")) { %>
    <mm:listnodes type="forums" orderby="number" directions="DOWN" max="1" id="this_forum">
            <mm:createrelation source="this_page" destination="this_forum" role="posrel" />
            <mm:field name="number" jspvar="dummy"  vartype="String" write="false"><% forumId = dummy; %></mm:field>
    </mm:listnodes>
<% } %>
<% if(!forumId.equals("")) { %>
    <mm:node number="<%= forumId %>">
        <iframe src="/mmbob/index.jsp?forumid=<mm:field name="number" />" title="<mm:field name="title"/>" width="100%" height="527px" frameborder="0">
            <a href="/mmbob/index.jsp?forumid=<mm:field name="number"/>" target="_blank"><mm:field name="title"/></a>
        </iframe>
    </mm:node>        
<% } %>
</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
