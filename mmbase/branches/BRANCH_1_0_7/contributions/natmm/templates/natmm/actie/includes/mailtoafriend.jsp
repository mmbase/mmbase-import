<table cellspacing="0" cellpadding="0" border="0" style="padding-left:10px;width:170px;">
 <tr>
   <td><a href="mailto:"><img src="../media/email.gif" border="0"></a></td>
   <td style="padding-left:10px"><a href="mailto:?subject=<mm:field name="titel" />&body=<%= 
            HttpUtils.getRequestURL(request) + (!"".equals(request.getQueryString()) ? "?" + request.getQueryString() : "" ) %>" class="maincolor_link" style="font-size:90%;">Stuur deze pagina naar een vriend</a>
     <span class="colortxt">></span></td>
 </tr>
</table>