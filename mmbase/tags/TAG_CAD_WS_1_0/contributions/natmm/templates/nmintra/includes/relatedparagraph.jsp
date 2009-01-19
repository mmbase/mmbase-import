<%@page isELIgnored="false"%>
<mm:node element="paragraaf">
<table cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td>
        <c:set var="imageNoDescription" value="true"/>
        <%@include file="../includes/relatedimage.jsp" %>
        <mm:field name="titel_zichtbaar"
			   ><mm:compare value="0" inverse="true"
			      ><div class="pageheader"><mm:field name="titel" 
		   	/></div></mm:compare
			></mm:field
		  ><mm:field name="tekst"><mm:isnotempty><span class="black"><mm:write /></span></mm:isnotempty></mm:field>
        <%@include file="../includes/attachment.jsp" %>
        <mm:related path="posrel,link" orderby="posrel.pos,link.titel" directions="UP,UP"
            ><br/>
            <a target="<mm:field name="link.target" />" href="<mm:field name="link.url" />" title="<mm:field name="link.alt_tekst" />" >
              <mm:field name="link.titel" />
            </a>
        </mm:related>
        <mm:related path="readmore,pagina" orderby="readmore.pos,pagina.titel" directions="UP,UP"
            ><br/>
            <a href="<mm:node element="pagina" jspvar="p"><%= ph.createPaginaUrl(p.getStringValue("number"),request.getContextPath()) %></mm:node>"
               title="<mm:field name="pagina.titel" />" >
                <mm:field name="readmore.readmore" /> <mm:field name="pagina.titel" />
            </a>
        </mm:related>
        <br/>
        <br/>
      </td>
   </tr>
</table>
</mm:node>