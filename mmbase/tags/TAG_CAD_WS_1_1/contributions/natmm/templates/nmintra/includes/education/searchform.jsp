<%@include file="../whiteline.jsp" %>
<table cellpadding="0" cellspacing="0" border="0" style="width:190px;" align="center">
<tr>
<td>
   <form method="POST" name="form1" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) + templateQueryString %>" onSubmit="return postIt();">
		<table width="190" height="18" border="0" cellpadding="0" cellspacing="0">
   	   <tr>
      		<td  class="light"><input type="text" name="termsearch" value="<%= (termSearchId.equals("") ? defaultSearchText : termSearchId )
                %>" onClick="if(this.value=='<%= defaultSearchText %>') { this.value=''; }" style="width:180px;" /></td>
	   	</tr>
	   </table>
      <%@include file="selectkeyword.jsp"%>
      <%@include file="selectpool.jsp"%>
      <% 
		String sProviders = searchResults(providers); 
      String providerConstraint = "educations.edutype='intern'"; 
      String providerTitle = "Interne themadag of -training";
      %>
      <%@include file="selectprovider.jsp"%>
      <% 
      providerConstraint = "educations.edutype!='intern'"; 
      providerTitle = "Opleidingsinstituut";
      %>
      <%@include file="selectprovider.jsp"%>
      <% // @include file="includes/eduselectcompetencetypes.jsp" %>
      <%-- <%@include file="selectcompetencies.jsp"%> --%> 
		<br/>
      <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
      	<tr>
          	<td>
             	<input type="button" name="submit" value="Terug" style="text-align:center;font-weight:bold;width:50px;" onClick="javascript:history.go(-1);">
      		</td>
            <td style="text-align:right;padding-right:10px;">
               <input type="button" name="submit" value="Wis" style="text-align:center;font-weight:bold;width:50px;" onClick="javascript:clearForm();">
   		   </td>
   			<td style="text-align:right;padding-right:10px;">
               <input type="submit" name="submit" value="Zoek" style="text-align:center;font-weight:bold;width:50px;">
   	   	</td>
         </tr>
      </table>
	</form>	
   <br/>
   <a href="<%= ph.createPaginaUrl("competenties",request.getContextPath()) %>" style="color:#FFFFFF;">Wat zijn competenties?</a>
</td>
</tr>
</table>
<%@include file="../whiteline.jsp" %>
<script type="text/javascript">
function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}
function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}
function clearForm() {
  document.location = "educations.jsp?p=<%= paginaID %>&h=&k=&j=&t=&u="; 
  return false; 
}
function postIt() {
	var href = document.form1.action;
	var termsearch = document.form1.elements["termsearch"].value;
   if(termsearch != '') href += "&termsearch=" + termsearch;
<% if (!keywordId.equals("")) {%>
		href += "&k=<%= keywordId %>";
<% } 
	if (!poolId.equals("")) {%>
		href += "&pool=<%= poolId %>";
<% }
   if (!providerId.equals("")) {%>
		href += "&pr=<%= providerId %>";
<% }
	if (!competenceId.equals("")) {%>
		href += "&c=<%= competenceId %>";
<% } %>	
	document.location = href;
   return false;
}
</script>
