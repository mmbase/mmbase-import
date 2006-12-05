<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
PaginaHelper ph = new PaginaHelper(cloud);
String templatesUrl = request.getParameter("tu");
String shop_itemHref = "javascript:searchIt();document.search.target='';document.search.submit();";
%><table width="180" cellspacing="0" cellpadding="0">
	<form name="search" method="post" target="" action="javascript:searchIt();">
	<tr>
		<td width="180">
		<table width="180" cellspacing="0" cellpadding="0">
			<tr> <!-- the input box gets a default 1px top and bottom border in IE -->
			<td class="titlebar" style="vertical-align:middle"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""><input type="text" name="search" style="width:88px;height:15px;"></td>
			<td class="titlebar" width="100%" style="vertical-align:middle;text-align:center;"><a href="<%= shop_itemHref %>" class="white"><bean:message bundle="LEOCMS" key="searchlink.search" /></a></td>
			<td class="titlebar" width="0%" style="padding-right:2px;padding-top:2px;padding-bottom:2px;"><a href="<%= shop_itemHref %>"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="180" colspan="2"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	<tr>
	<tr>
		<td class="subtitlebar" width="180" colspan="2" style="padding-right:3px;text-align:right;">
		   <bean:message bundle="LEOCMS" key="searchlink.search" />
		</td>
	<tr>
	</form>
</table>
<script language="JavaScript">
<%= "<!--" %>
function searchIt() {
	var href = "<mm:url page="<%= ph.createPaginaUrl("zoek",request.getContextPath()) %>" />";
	<% if(templatesUrl.indexOf("shoppingcart")>-1) { 
			%>href += changeIt();<%
	} %>
	var search = document.search.elements["search"].value;
	if(search != '') {
		var hasQuote = search.indexOf('\'');
		if ((hasQuote>-1)){
			search = search.substring(0,hasQuote);
			alert("Error: Uw zoekopdracht mag geen \' bevatten.");
		}
		href += "&s=" +escape(search);
	}
	document.location = href;
}
<%= "// -->" %>
</script>
</mm:cloud>