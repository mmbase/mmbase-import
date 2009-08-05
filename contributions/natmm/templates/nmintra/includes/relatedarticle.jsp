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
  <mm:field name="titel_zichtbaar"
	   ><mm:compare value="0" inverse="true"
   	   ><div class="pageheader"><mm:field name="titel" 
	   /></div></mm:compare
	></mm:field
	><mm:field name="titel_fra"><div class="pagesubheader"><mm:write /></div></mm:field
    ><mm:list nodes="<%= paginaID %>" path="pagina,gebruikt,paginatemplate"
        ><mm:field name="pagina.titel_fra" jspvar="showDate" vartype="String" write="false"
        ><mm:field name="paginatemplate.url" jspvar="template" vartype="String" write="false"><%
            if(template.indexOf("homepage.jsp")>-1||template.indexOf("info.jsp")>-1||template.indexOf("calendar.jsp")>-1) {
                %><%@include file="../includes/poolanddate.jsp" %><%
            } 
        %></mm:field
        ></mm:field
    ></mm:list>
    <mm:related path="readmore,medewerkers" orderby="readmore.pos" directions="UP">
      <mm:first>
        <table border="0" cellpadding="0" cellspacing="0">
      </mm:first>
        <tr>
          <td style="text-align:right;padding-right:10px;">
            <mm:node element="medewerkers" jspvar="thisEmployee">
              <mm:import id="employee_detail" reset="true"><%=
                ph.createPaginaUrl("wieiswie",request.getContextPath()) + "?employee=" + thisEmployee.getStringValue("number")
              %></mm:import>
              <mm:relatednodes type="images">
                <a href="<mm:write referid="employee_detail" />">
                  <img src="<mm:image template="s(50)" />" border="0" >
                </a>
              </mm:relatednodes>
            </mm:node>
          </td>
          <td>
            <mm:field name="readmore.readmore">
              <mm:isnotempty>
                  <mm:write />&nbsp;|
              </mm:isnotempty>
            </mm:field>
            <a href="<mm:write referid="employee_detail" />">
              <span style="text-decoration:underline;" class="dark"><mm:field name="medewerkers.titel"/></span>
            </a>
            <mm:field name="readmore.pos" jspvar="ispresent" vartype="String" write="false">
                  &nbsp;|&nbsp;<%= ("1".equals("ispresent") ? "aanwezig" : "afwezig") %>
            </mm:field>
          </td>
        </tr>
      <mm:last>
         </table>
         <br/>
      </mm:last>
    </mm:related>
    <mm:field name="intro"><mm:isnotempty><span class="black"><mm:write /></span><br/></mm:isnotempty></mm:field>
    <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <mm:first><br/></mm:first>
      <%@include file="../includes/relatedparagraph.jsp" %>
    </mm:related>
</mm:node>