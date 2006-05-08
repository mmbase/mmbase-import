<% if(hasPools ) { 
   %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,shorty"
      ><mm:field name="shorty.titel" jspvar="items_name" vartype="String" write="false"><%
         rightBarTitle = items_name;
         %><%@include file="../includes/rightbartitle.jsp" 
     %></mm:field
   ></mm:list><% 
} else {
   %><%@include file="../includes/whiteline.jsp" %><%
} %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,shorty"
    ><table border="0" cellpadding="0" cellspacing="0">
<tr><td><img src="media/spacer.gif" width="20" height="10"></td>
    <td><img src="media/spacer.gif" width="230" height="10"></td>
</tr>
<tr><td><img src="media/spacer.gif" width="20" height="1"></td>
    <td><mm:node element="shorty"
    ><mm:related path="posrel,link" orderby="posrel.pos" directions="UP"
        ><a target="_blank" href="<mm:field name="link.url" />" class="menuItem"><span class="normal"><mm:field name="link.titel" /></span></a><br>
    </mm:related
    ></mm:node></td>
    </tr>
</table></mm:list>