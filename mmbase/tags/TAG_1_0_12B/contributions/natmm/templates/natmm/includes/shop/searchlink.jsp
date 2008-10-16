<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
PaginaHelper ph = new PaginaHelper(cloud);
%><table width="165" cellspacing="0" cellpadding="0">
	<form name="search" method="post" target="" action="javascript:searchIt();">
	<tr>
		<td width="165">
		<table width="165" cellspacing="0" cellpadding="0">
			<tr> <!-- the input box gets a default 1px top and bottom border in IE -->
			<td class="maincolor" style="vertical-align:middle;padding-left:1px;">
        <input type="text" name="search" style="width:82px;height:16px;border:0px;font:0.9em;"></td>
			<td class="maincolor" style="vertical-align:middle;text-align:center;">
        <input type="submit" value="<bean:message bundle="LEOCMS" key="shop.searchlink.search" />" class="submit_image" style="width:82px;" /></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td width="165" colspan="2"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<tr>
	<tr>
		<td class="subtitlebar" width="165" colspan="2" style="padding-right:3px;text-align:right;">
		   <bean:message bundle="LEOCMS" key="shop.searchlink.intheshop" />
		</td>
	<tr>
	</form>
</table>
<script type="text/javascript">
<%= "<!--" %>
function searchIt() {
	var href = "<mm:url page="<%= ph.createPaginaUrl("zoek",request.getContextPath()) %>" />";
	var search = document.search.elements["search"].value;
	if(search != '') {
		var hasQuote = search.indexOf('\'');
		if ((hasQuote>-1)){
			search = search.substring(0,hasQuote);
			alert("Error: Uw zoekopdracht mag geen \' bevatten.");
		}
		href += "?s=" +escape(search);
	}
  <%
  if(javax.servlet.http.HttpUtils.getRequestURL(request)!=null && javax.servlet.http.HttpUtils.getRequestURL(request).indexOf("shoppingcart")>-1) { 
			%>href += changeIt();<%
	} 
  %>
	document.location = href;
}
<%= "// -->" %>
</script>
</mm:cloud>