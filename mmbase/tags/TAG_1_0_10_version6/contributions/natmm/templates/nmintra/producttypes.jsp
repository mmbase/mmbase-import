<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" 

%><%@include file="includes/header.jsp" %>
<td><table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <%-- <td><img src="media/rdcorner.gif" style="filter:alpha(opacity=75)"></td> --%>
        <td class="transperant" style="width:100%;"><img src="media/spacer.gif" width="1" height="6"><br>
        <div align="right"><span class="pageheader"><span class="dark"><mm:node number="<%= paginaID %>">Het dienstenpakket van de afdeling <mm:field name="titel"/></mm:node
            ></span></span>
			</div></td>
        <td class="transperant"><img src="media/spacer.gif" width="10" height="28"></td>
    </tr>
</table></td>
<% 
if(!printPage) {
   %>
   <td>
      <% 
      String rightBarTitle = "";
      %><%@include file="includes/rightbartitle.jsp" 
      %>
   </td>
   <%
} %>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0">
    <tr><td style="padding:10px;padding-top:18px;">
    <%@include file="includes/back_print.jsp" %>
    <%
    if(!articleId.equals("-1")) { 
        %><mm:list nodes="<%= articleId %>" path="artikel"
            ><%@include file="includes/relatedarticle.jsp"
        %></mm:list><%
    
    } else {
        
        String thisPage = ph.createPaginaUrl(paginaID,request.getContextPath());
        
        
        %><%@include file="includes/producttypes/prodlocspecs.jsp" %>
        <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel"
            orderby="contentrel.pos" directions="UP" fields="artikel.number,artikel.titel"
            ><mm:first><div class="pagesubheader" style="margin-top:10px;">Klik hier voor informatie over:</div></mm:first
            ><div style="margin-top:10px;"><li><a href="producttypes.jsp<%= templateQueryString %>&article=<mm:field name="artikel.number" 
                />"><mm:field name="artikel.titel" /></a></div>
        </mm:list>
        <%@include file="includes/producttypes/producttypes.jsp" %><% 
    } 
    %><%@include file="includes/pageowner.jsp" 
    %></td>
</tr>
</table>
</div>
</td>
<%

// *********************************** right bar *******************************
if(!printPage) {
  %><td><img src="media/spacer.gif" width="10" height="1"></td><%
} %>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
