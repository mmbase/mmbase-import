<mm:node element="artikel" id="this_article">
  <mm:related path="posrel,images"
      constraints="posrel.pos='9'" orderby="images.title"
      ><div align="center"><img src="<mm:node element="images"><mm:image template="s(535)" /></mm:node
         >" alt="<mm:field name="images.title" />" border="0" ></div>
  </mm:related>
  <mm:related path="posrel1,paragraaf,posrel2,images"
      constraints="posrel2.pos='9'" orderby="images.title"
      ><div align="center"><img src="<mm:node element="images"><mm:image template="s(535)" /></mm:node
         >" alt="<mm:field name="images.title" />" border="0" ></div>
  </mm:related>
  <%@include file="../includes/relatedimage.jsp" %>
  <p>
	<mm:field name="titel_zichtbaar"
	   ><mm:compare value="0" inverse="true"
   	   ><div class="pageheader"><mm:field name="titel" 
	   /></div></mm:compare
	></mm:field
	><mm:field name="titel_fra"><div class="pagesubheader"><mm:write /></div></mm:field
    ><mm:list nodes="<%= paginaID %>" path="pagina,gebruikt,paginatemplate"
        ><mm:field name="pagina.titel_fra" jspvar="showDate" vartype="String" write="false"
        ><mm:field name="paginatemplate.url" jspvar="template" vartype="String" write="false"><%
            if(template.indexOf("info.jsp")>-1||template.indexOf("calendar.jsp")>-1) {
                %><%@include file="../includes/poolanddate.jsp" %><%
            } 
        %></mm:field
        ></mm:field
    ></mm:list
    ><mm:field name="intro"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field></p>
    <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <%@include file="../includes/relatedparagraph.jsp" %>
    </mm:related>
</mm:node>