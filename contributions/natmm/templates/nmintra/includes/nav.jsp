<% 
// *** referer is used to open navigation on a page which is not visible in the navigation ***
String tmp_pageId = pageId;
if(!refererId.equals("")) { 
   boolean pageIsVisible = false;
   %><mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" max="1" constraints="<%= "pagina.number='" + pageId + "'" %>"><%
      pageIsVisible = true;
   %></mm:list><%
   if(!pageIsVisible) { 
      %><mm:list nodes="<%= rubriekId %>" path="rubriek1,parent,rubriek2,posrel,pagina" max="1" constraints="<%= "pagina.number='" + pageId + "'" %>"><%
         pageIsVisible = true;
      %></mm:list><%
   }
   if(!pageIsVisible) { pageId = refererId; }
}
// *** page: translate alias back into number ***
%><mm:node number="<%= pageId %>" notfound="skipbody"
    ><mm:field name="number" jspvar="page_number" vartype="String" write="false"><%
        pageId = page_number; 
    %></mm:field
></mm:node><%
// *** rubriek: translate alias back into number ***
if(!rubriekId.equals("")) { 
    %><mm:node number="<%= rubriekId %>"
        ><mm:field name="number" jspvar="rubriek_number" vartype="String" write="false"><%
            rubriekId = rubriek_number; 
        %></mm:field
    ></mm:node><% 
} 
boolean bIsFirst = false;
%><div class="navlist">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td><img src="media/spacer.gif" width="1" height="527"></td>
<td>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
    <td><img src="media/spacer.gif" width="158" height="25"></td>
</tr>

<% RubriekHelper rubriekHelper = new RubriekHelper(cloud);
PaginaHelper ph = new PaginaHelper(cloud); %>
<mm:list nodes="<%= websiteId %>" path="rubriek,posrel,pagina" constraints="posrel.pos='1'"
    ><mm:field name="pagina.number" jspvar="page_number" vartype="String" write="false"><%
    if(isPreview) {
        String rubriek_number = "";
        if(pageId.equals("")) { pageId = page_number; }
        %><tr><td style="padding-left:19px;padding-bottom:7px;">
            <a href=<%= ph.createPaginaUrl(page_number, request.getRequestURI()) %> class="menuItem<mm:field name="pagina.number"><mm:compare value="<%= pageId %>">Active</mm:compare></mm:field
                    >"><mm:field name="pagina.titel" /></a>
        </td></tr><%
    }
    %></mm:field
></mm:list
><mm:list nodes="<%= websiteId %>" path="rubriek1,parent,rubriek2"
    orderby="parent.pos" directions="UP"
    ><mm:field name="rubriek2.number" jspvar="rubriek_number" vartype="String" write="false"><%
	    // *** list the rubrieks ***
   	 %><%@include file="../includes/rubriek_page.jsp" %>
	</mm:field
></mm:list></table>
        </td>
    </tr>
</table></div>
<% // *** reset pageId to original value, if referer is used ***
if(!refererId.equals("")) { pageId = tmp_pageId; }
%>