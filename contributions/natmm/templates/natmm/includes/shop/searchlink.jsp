<%@include file="/taglibs.jsp" %>
<%@include file="../../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
String templatesUrl = request.getParameter("tu");
productHref = "javascript:searchIt();document.search.target='';document.search.submit();";
%><table width="180" cellspacing="0" cellpadding="0">
	<form name="search" method="post" target="" action="javascript:searchIt();">
	<tr>
		<td width="180">
		<table width="180" cellspacing="0" cellpadding="0">
			<tr> <!-- the input box gets a default 1px top and bottom border in IE -->
			<td class="titlebar" style="vertical-align:middle"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""><input type="text" name="search" style="width:88px;height:15px;"></td>
			<td class="titlebar" width="100%" style="vertical-align:middle;text-align:center;"><a href="<%= productHref %>" class="white"><bean:message bundle="LEOCMS" key="searchlink.paragraaf.titel" /></a></td>
			<td class="titlebar" width="0%" style="padding-right:2px;padding-top:2px;padding-bottom:2px;"><a href="<%= productHref %>"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="180" colspan="2"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	<tr>
	<tr>
		<td class="subtitlebar" width="180" colspan="2"><div align="right">
		<% ResourceBundle bd = ResourceBundle.getBundle("ApplicationResources"); 
			String sParagraafOmschrijving = bd.getString("searchlink.paragraaf.omschrijving");%>
			<%= cleanText(sParagraafOmschrijving,"<",">") 
				%><img src="media/spacer.gif" width="3" height="1" border="0" alt=""></div></td>
	<tr>
	</form>
</table>
<script language="JavaScript">
<%= "<!--" %>
function searchIt() {
	var href = "<mm:url page="<%= pageUrl + "&p=zoek" %>" />";
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