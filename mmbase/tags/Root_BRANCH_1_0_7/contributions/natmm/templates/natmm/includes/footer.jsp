   </td>
</tr>
</table>
<mm:node number="search_template" notfound="skipbody">
   <mm:related path="gebruikt,pagina,posrel,rubriek1,parent,rubriek2"
      constraints="<%= "rubriek2.number = '" + subsiteID + "'" %>" fields="pagina.number,rubriek1.number">
     <mm:import id="search_page" jspvar="search_page"><mm:field name="pagina.number" /></mm:import>
     <mm:import id="search_rubriek" jspvar="search_rubriek"><mm:field name="rubriek1.number" /></mm:import>
     <form action="<%= ph.createPaginaUrl(search_page,request.getContextPath()) %>" style="margin:0px 0px 0px 0px">
   </mm:related>
</mm:node>
<%
if(iRubriekLayout==NatMMConfig.DEFAULT_LAYOUT || iRubriekLayout==NatMMConfig.DEMO_LAYOUT) { 
   %><table style="line-height:90%;width:744;" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
      <td class="footer" style="width:544px;text-align:center;">
         &copy <%= Calendar.getInstance().get(Calendar.YEAR) %> <mm:node number="root"><mm:field name="naam"/></mm:node>
         <mm:node number="footer" notfound="skipbody">
         <mm:related path="posrel,pagina" fields="pagina.number,pagina.titel" orderby="posrel.pos" directions="UP">
            <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
              &nbsp;&nbsp;|&nbsp;&nbsp; 
              <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" class="footerlinks"><mm:field name="pagina.titel" /></a>
            </mm:field>
         </mm:related>
         </mm:node>
      </td>
      <mm:present referid="search_page">
         <input type="hidden" name="offset" value="0"/>
         <input type="hidden" name="pcontentype" value="0"/>
         <td width="196">
            <table cellspacing="0" cellpadding="0">
               <tr>
                  <td class="footerzoektext"><input type="submit" value="ZOEKEN" style="height:16px;border:0;color:#FFFFFF;background-color:#1D1E94;text-align:left;padding-left:10px;font-weight:bold;font-size:0.9em;" /></td>
                  <td class="footerzoekbox"><input type="text" name="query_frm" style="width:100%;height:14px;font-size:12px;border:none;" value="<%= (request.getParameter("query_frm")==null ? "" : request.getParameter("query_frm")) %>"></td>
                  <td class="footerzoekbox"><input type="image" src="media/submit_default.gif" alt="ZOEK" align="middle" border="0"></td>
               </tr>
            </table>
         </td>
      </mm:present>
   </tr>
   </table><%
} else {
   %><table style="line-height:90%;width:744;" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
      <tr>
         <td style="background-color:#<%= NatMMConfig.color2[iRubriekStyle] %>;width:173px;padding-left:5px;font-size:70%;text-align:center;">
            <mm:node number="<%= subsiteID %>"><mm:field name="naam_eng" /></mm:node>
         </td>
         <td style="width:1px;"></td>
         <td class="maincolor" style="width:369px;padding-left:7px;font-size:70%;">
            <mm:node number="<%= subsiteID %>"><mm:field name="naam_de" /></mm:node>
         </td>
         <mm:present referid="search_page">
            <input type="hidden" name="offset" value="0"/>
            <input type="hidden" name="pcontentype" value="0"/>
            <td width="196px">
               <table cellspacing="0" cellpadding="0">
                  <tr>
                     <td class="footerzoektext" style="background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;"><input type="submit" value="ZOEKEN" style="height:19px;border:0;color:#FFFFFF;background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;text-align:left;padding-left:10px;padding-top:1px;font-weight:bold;font-size:0.9em;" /></td>
                     <td class="footerzoekbox" style="background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;"><input type="text" name="query_frm" style="width:100%;height:17px;font-size:12px;border:none;" value="<%= (request.getParameter("query_frm")==null ? "" : request.getParameter("query_frm")) %>"></td>
                     <td class="footerzoekbox" style="background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;"><input type="image" src="media/submit_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" alt="ZOEK" align="middle" border="0"></td>
                  </tr>
               </table>
            </td>
         </mm:present>
      </tr>
      </table><%
} %>
<mm:present referid="search_page">
   </form>
</mm:present>
<br/>
</div>
</body>
<% 
if(iRubriekLayout!=NatMMConfig.DEMO_LAYOUT) { 
   %>
   <%@include file="../includes/sitestatscript.jsp" %>
   <%
} %>
</html>