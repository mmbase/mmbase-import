<table width="180" cellspacing="0" cellpadding="0" align="right">
	<tr>
		<td width="180">
		<table width="180" cellspacing="0" cellpadding="0">
			<tr> <!-- the input box gets a default 1px top and bottom border in IE -->
			<td class="maincolor" style="vertical-align:middle;background-color:#5D5D5D;"><img src="media/trans.gif" width="1" height="1" border="0" alt=""><input type="text" name="memberid" style="width:88px;height:15px;border:0px;" value="<%= memberId %>"></td>
			<td class="maincolor" width="100%" style="vertical-align:middle;text-align:center;background-color:#5D5D5D;">
				<a href="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();"
				class="klikpad"><b><bean:message bundle="LEOCMS" key="shoppingcart.membershipsform.i_am_member" /></b></a></td>
			<td class="maincolor" width="0%" style="padding-right:2px;padding-top:2px;padding-bottom:2px;background-color:#5D5D5D;">
				<a href="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();">
				<img src="media/shop/pijl_wit_op_grijs.gif" border="0" alt=""></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="180" colspan="2"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<tr>
	<tr>
		<td class="subtitlebar" width="180" colspan="2"><div align="right">
		<bean:message bundle="LEOCMS" key="shoppingcart.membershipsform.enter_memberid" />
		<img src="media/trans.gif" width="3" height="1" border="0" alt=""></div></td>
	<tr>
</table>