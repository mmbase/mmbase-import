<table cellspacing="0" cellpadding="0" width="744" align="center" border="0" valign="top">
   <tr><td style="text-align:center;color:828282;font-size:70%;">
      <table class="dotline"><tr><td height="3"></td></tr></table>
      &copy <%= Calendar.getInstance().get(Calendar.YEAR) %> Natuurmonumenten
      <mm:node number="<%= subsiteID %>">
         <mm:field name="naam_eng"><mm:isnotempty>&nbsp;&nbsp;|&nbsp;&nbsp;<mm:write/></mm:isnotempty></mm:field>
         <mm:field name="naam_de"><mm:isnotempty>&nbsp;&nbsp;|&nbsp;&nbsp;<mm:write/></mm:isnotempty></mm:field>
      </mm:node>
      <mm:node number="footer_a6a9" notfound="skipbody">
         <mm:related path="posrel,pagina" fields="pagina.number,pagina.titel" orderby="posrel.pos" directions="UP">   
      	   &nbsp;&nbsp;|&nbsp;&nbsp; 
            <mm:field name="pagina.number" jspvar="pageNumber" vartype="String" write="false">
      	      <a href="<%= ph.createPaginaUrl(pageNumber,request.getContextPath()) %>" class="hover" style="color:828282;"><mm:field name="pagina.titel" /></a>
            </mm:field>
         </mm:related>    
      </mm:node>
   </td></tr>
</table>
