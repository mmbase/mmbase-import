<table class="dotline"><tr><td height="3"></td></tr></table>
<table cellspacing="0" cellpadding="0" border="0" valign="top">
	<tr>
		<td style="padding-top:0px;padding-bottom:2px;padding-right:7px;"><a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+thisOffset,request.getContextPath()) %>"><img src="../media/arrowleft_default.gif" border="0"></a></td>
		<td><mm:field name="titel_fra" jspvar="sReadMore" vartype="String" write="false"
				><% if(sReadMore==null || sReadMore.equals("")) {  sReadMore = "MEER NIEUWS"; } 
				%><a href="<%= ph.createItemUrl(artikelID, paginaID,"offset="+thisOffset,request.getContextPath()) %>" class="hover" style="font-size:85%;line-height:90%;"><%= sReadMore %></a></mm:field
		></td>
	</tr>
</table>
<table class="dotline" style="margin-top:0px;margin-bottom:10px;"><tr><td height="3"></td></tr></table>
