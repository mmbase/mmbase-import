<mm:cloud jspvar="cloud">
<%
String sPageRef = (String) session.getAttribute("pageref");
if(sPageRef!=null&&!sPageRef.equals(paginaID)) { // set pagerefminone to sPagRef, set pageref to paginaID
	session.setAttribute("pagerefminone",sPageRef);
}
session.setAttribute("pageref",paginaID);

// *** set up 4 columns
%>
<br />
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
	<td width="165"><img src="media/trans.gif" width="165" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="100%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="100%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180"><img src="media/trans.gif" width="180" height="1" border="0" alt=""></td>
</tr>
<tr>
	<td width="100%" height="100%" colspan="3">
    <jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
    </jsp:include>
  </td>
	<td rowspan="2"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td rowspan="2" align="right" style="vertical-align:top;">
		<jsp:include page="includes/shop/phonelink.jsp">
				<jsp:param name="t" value="<%= actionId %>" />
				<jsp:param name="ti" value="<%= totalitemsId %>" />
			</jsp:include></td>
	<td rowspan="2"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td rowspan="2" style="vertical-align:top;">
			<jsp:include page="includes/shoppingcart/link.jsp">
				<jsp:param name="t" value="<%= actionId %>" />
				<jsp:param name="ti" value="<%= totalitemsId %>" />
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
   <td width="180" style="vertical-align:top;padding-top:14px;">
      <jsp:include page="includes/shop/searchlink.jsp" />
      <br/>
      <%@include file="../navleft.jsp" %>
   </td>
   <td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
</mm:cloud>