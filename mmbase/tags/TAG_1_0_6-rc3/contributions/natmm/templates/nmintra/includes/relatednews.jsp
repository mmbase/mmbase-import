<div align="center">
   <mm:node element="artikel" id="this_article">
   <mm:related path="posrel1,paragraaf,posrel2,images"  constraints="posrel2.pos='9'" orderby="images.title"
      ><img src="<mm:node element="images"><mm:image template="s(535x300)" /></mm:node
         >" alt="<mm:field name="images.title" />" border="0" >
   </mm:related>
   <p><mm:field name="titel_zichtbaar"
		   ><mm:compare value="0" inverse="true"
      		><div class="pageheader"><mm:field name="titel" 
	   	/></div></mm:compare
		></mm:field
	 ><mm:field name="titel_fra"><div class="pagesubheader" align="center"><mm:write /></div></mm:field
    ><mm:list nodes="<%= paginaID %>" path="pagina,gebruikt,paginatemplate"
        ><mm:field name="pagina.titel_fra" jspvar="showDate" vartype="String" write="false"
        ><mm:field name="paginatemplate.url" jspvar="template" vartype="String" write="false"><%
            if(template.indexOf("info.jsp")>-1||template.indexOf("calendar.jsp")>-1||template.indexOf("newsletter.jsp")>-1) {
                %><%@include file="../includes/poolanddate.jsp" %><%
            } 
        %></mm:field
        ></mm:field
    ></mm:list
    ><mm:field name="intro"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field></p>
   <% int p = 0; %>
   <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <mm:first><table border="1" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;border-color:#000000;"></mm:first>
      <% 
      if(p%3==0) { %><tr><td colspan="2" style="padding:5px;border-color:#000000;"><% } 
      if(p%3==1) { %><tr><td style="width:50%;padding:5px;border-color:#000000;"><% }
      if(p%3==2) { %><td style="width:50%;padding:5px;border-color:#000000;"><% } 
      %>
      <%@include file="../includes/relatedparagraph.jsp" %>
      <% 
      if(p%3==0) { %></td></tr><% } 
      if(p%3==1) { %></td><% } 
      if(p%3==2) { %></td></tr><% } 
      p++; %>
      <mm:last>
         <% if(p%3==2) { %><td></td></tr><% } %>
         </table>
      </mm:last>
   </mm:related>
</mm:node>
</div>