<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%@include file="includes/calendar.jsp" 

%><%@include file="includes/header.jsp" %>
<td><table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <%-- <td><img src="media/rdcorner.gif" style="filter:alpha(opacity=75)"></td> --%>
        <td class="transperant" style="width:100%;"><img src="media/spacer.gif" width="1" height="6"><br>
        <div align="right"><span class="pageheader"><span class="dark_<%= cssClassName 
                %>"><mm:node number="<%= pageId %>">Het dienstenpakket van de afdeling <mm:field name="titel"/></mm:node
            ></span></span>
			</div></td>
        <td class="transperant"><img src="media/spacer.gif" width="10" height="28"></td>
    </tr>
</table></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0">
    <tr><td style="padding:10px;padding-top:18px;"><%
    if(!postingStr.equals("|action=print")) {
        %><div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString %>&pool=<%= poolId %>&product=<%= productId %>&pst=|action=print">print</a></div><%
    } 
    if(!articleId.equals("")) { 
        %><mm:list nodes="<%= articleId %>" path="artikel"
            ><%@include file="includes/relatedarticle.jsp"
        %></mm:list><%
    
    } else {
        
        %><%@include file="includes/producttypes/prodlocspecs.jsp" %>
        <mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel"
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
<td><%-- 

*********************************** right bar *******************************
--%><img src="media/spacer.gif" width="10" height="1"></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
