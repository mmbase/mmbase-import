<mm:cloud jspvar="cloud">
<%
// *** set up 4 columns
%>
<br />
<table width="744" border="1" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td width="180"><img src="media/trans.gif" width="180" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="100%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="100%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180"><img src="media/trans.gif" width="180" height="1" border="0" alt=""></td>
</tr>
<tr>
	<td width="100%" height="100%" colspan="3">breadcrumbs</td>
	<td rowspan="2"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td rowspan="2" align="right">
		<jsp:include page="../includes/phonelink.jsp" /></td>
	<td rowspan="2"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td rowspan="2">
			<jsp:include page="shoppingcart/link.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
			</jsp:include></td>
</tr>
<tr>
	<td class="bottom" colspan="3">
		<table width="100%" height="1" cellspacing="0" cellpadding="0">
			<tr><td width="100%" height="1" class="subtitlebar"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr>
		</table>
	</td>
</tr>
<tr>
   <td width="180">
      <jsp:include page="includes/shop/welcome.jsp">
        <jsp:param name="p" value="<%= paginaID %>" />
      </jsp:include> 
      <jsp:include page="includes/shop/searchlink.jsp" />
      <jsp:include page="includes/shop/nav.jsp" />
   </td>
   <td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
</mm:cloud>