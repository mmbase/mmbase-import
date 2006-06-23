<script>
 function gotoURL(targ,link){ //v3.0
	eval(targ+".location='"+link+"'");
 }
</script>
<% 
String thisPage =  HttpUtils.getRequestURL(request).toString();
String otherLanguageName = "en";
String otherLanguageId = "eng";
if(language.equals(otherLanguageId)) {
	otherLanguageName = "nl";
	otherLanguageId = "nl";
}
thisPage += "?language=" + otherLanguageId; 
String queryString = request.getQueryString();
if(queryString!=null&&!queryString.equals("")) {
	thisPage += "&" + queryString; 
}
%>
<tr>
	<%= getTableCells("VAN","vh","index.jsp") %>
	<%= getTableCells(otherLanguageName.toUpperCase(),otherLanguageName,thisPage) %>
	<%= getTableCells("STATEMENT","doc","/statement") %>
	<%= getTableCells("CV","cv","/cv") %>
	<%= getTableCells("WEBWORK","webwork","/webwork") %>
	<%= getTableCells("@","contact","/contact") %>
</tr>
<tr>
	<%= getTableCells("HAM","vh","index.jsp") %>
   <td colspan="21"></td>
</tr>

