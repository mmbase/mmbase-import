<form action="zoek.jsp" style="margin:0px 0px 0px 0px">
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
   	<mm:node number="search_template" notfound="skipbody">
      <mm:related path="gebruikt,pagina,posrel,rubriek1,parent,rubriek2"
         constraints="<%= "rubriek2.number = '" + rootID + "'" %>" fields="pagina.number,rubriek1.number">
         <input type="hidden" name="p" value="<mm:field name="pagina.number" />">
         <input type="hidden" name="r" value="<mm:field name="rubriek1.number" />">
      	<td width="196">
      	   <table cellspacing="0" cellpadding="0">
            	<tr>
               	<td class="footerzoektext"><input type="submit" value="ZOEKEN" style="height:16px;border:0;color:#FFFFFF;background-color:#1D1E94;text-align:left;padding-left:10px;font-weight:bold;font-size:0.9em;" /></td>
               	<td class="footerzoekbox"><input type="text" name="query" style="width:100%;height:14px;font-size:12px;border:none;" value="<%= (request.getParameter("query")==null ? "" : request.getParameter("query")) %>"></td>
               	<td class="footerzoekbox"><input type="image" src="media/submit_default.gif" alt="ZOEK" align="middle" border="0"></td>
            	</tr>
      	   </table>
      	</td>
      </mm:related>
      </mm:node>
   </tr>
   </table><%
} else {
   %><table style="line-height:90%;width:744;" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
      <tr>
      	<td style="background-color:#<%= NatMMConfig.color2[iRubriekStyle] %>;width:173px;padding-left:5px;font-size:70%;text-align:center;">
           	<mm:node number="<%= rootID %>"><mm:field name="naam_eng" /></mm:node>
      	</td>
         <td style="width:1px;"></td>
         <td class="maincolor" style="width:369px;padding-left:7px;font-size:70%;">
            <mm:node number="<%= rootID %>"><mm:field name="naam_de" /></mm:node>
      	</td>
      	<mm:node number="search_template" notfound="skipbody">
         <mm:related path="gebruikt,pagina,posrel,rubriek1,parent,rubriek2"
            constraints="<%= "rubriek2.number = '" + rootID + "'" %>" fields="pagina.number,rubriek1.number">
            <input type="hidden" name="p" value="<mm:field name="pagina.number" />">
            <input type="hidden" name="r" value="<mm:field name="rubriek1.number" />">
         	<td width="196px">
               <table cellspacing="0" cellpadding="0">
               	<tr>
                  	<td class="footerzoektext" style="background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;"><input type="submit" value="ZOEKEN" style="height:19px;border:0;color:#FFFFFF;background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;text-align:left;padding-left:10px;padding-top:1px;font-weight:bold;font-size:0.9em;" /></td>
                  	<td class="footerzoekbox" style="background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;"><input type="text" name="query" style="width:100%;height:17px;font-size:12px;border:none;" value="<%= (request.getParameter("query")==null ? "" : request.getParameter("query")) %>"></td>
                  	<td class="footerzoekbox" style="background-color:#<%= NatMMConfig.color1[iRubriekStyle] %>;"><input type="image" src="media/submit_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" alt="ZOEK" align="middle" border="0"></td>
               	</tr>
         	   </table>
         	</td>
         </mm:related>
         </mm:node>
      </tr>
      </table><%
} %>
</form>
<br/>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>