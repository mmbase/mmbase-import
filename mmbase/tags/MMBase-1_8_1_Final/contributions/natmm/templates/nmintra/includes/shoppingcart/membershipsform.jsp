<%@include file="../includes/templateheader.jsp" %>
<%@include file="../includes/calendar.jsp" %>
<%@include file="../includes/getresponse.jsp" %>
<% String memberId = request.getParameter("mi"); %>
<table width="180" cellspacing="0" cellpadding="0" align="right">
	<tr>
		<td width="180">
		<table width="180" cellspacing="0" cellpadding="0">
			<tr> <!-- the input box gets a default 1px top and bottom border in IE -->
			<td class="titlebar" style="vertical-align:middle;background-color:#5D5D5D;"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""><input type="text" name="memberid" style="width:88px;height:15px;" value="<%= memberId %>"></td>
			<td class="titlebar" width="100%" style="vertical-align:middle;text-align:center;background-color:#5D5D5D;">
				<a href="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();"
	            onclick="needToConfirm = false;" class="white">title</a></td>
			<td class="titlebar" width="0%" style="padding-right:2px;padding-top:2px;padding-bottom:2px;background-color:#5D5D5D;">
				<a href="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();"
               onclick="needToConfirm = false;">
				<img src="media/pijl_wit_op_grijs.gif" border="0" alt=""></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="180" colspan="2"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	<tr>
	<tr>
		<td class="subtitlebar" width="180" colspan="2"><div align="right">body memberships form<img src="media/spacer.gif" width="3" height="1" border="0" alt=""></div></td>
	<tr>
</table>
