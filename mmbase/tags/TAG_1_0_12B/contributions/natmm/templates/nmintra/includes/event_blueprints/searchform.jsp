<%! 
public String getSelect(Cloud cloud, Logger log, String title, int iRubriekStyle, String nodeId, NodeList related, String destination, String field, String url, String param) {
   String sSelect = 
            "<table style='width:190px;margin-bottom:3px;' border='0' cellpadding='0' cellspacing='0'>" 
         +     "<tr>"
         +        "<td class='bold'><div align='left' class='light'>&nbsp;" + title + "</div></td>"
         +     "</tr>"
         +  "</table>";
   if(!nodeId.equals("")) { // a node has been selected 
      sSelect +=
            "<table width='190' height='18' border='0' cellpadding='0' cellspacing='0'>" 
         +     "<tr>"
         +        "<td class='light'>&nbsp;" + cloud.getNode(nodeId).getStringValue(field) + "</td>"
         +     "</tr>"
         +  "</table>";
   } else {
      sSelect += 
            "<select name='menu1' style='width:180px;' onChange=\"MM_jumpMenu('document',this,0)\">"
         +     "<option value='" + url + "'>Selecteer</option>";
      int pPos = url.indexOf(param);
      if(pPos!=-1) {
         int ampPos = url.indexOf("&",pPos);
         if(ampPos==-1) {
            url = url.substring(0,pPos);
         } else {
            url = url.substring(0,pPos) + url.substring(ampPos);
         }
      } else {
         log.error("Url " + url + " does not contain param " + param );
      }
      if (related!=null){
	      for(int n=0; n<related.size(); n++) {
   	      String name = related.getNode(n).getStringValue(destination + "." + field);
      	  String number = related.getNode(n).getStringValue(destination + ".number");
         	sSelect +=  "<option value='" + url + "&" + param + "=" + number + "'>" + name + "</option>";
	      }
      }
      sSelect += 
   	      "</select>"
   	   +  "<br/>";
   } 
   return sSelect;
}

%>
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
      <%= getSelect(cloud,log,"Type activiteit",iRubriekStyle,eTypeId,eventTypes,"evenement_type","naam",searchUrl,"evt") %>
      <%= getSelect(cloud,log,"Doelgroep",iRubriekStyle,pCategorieId,participantsCategories,"deelnemers_categorie","naam",searchUrl,"pc") %>
      <%-- getSelect(cloud,log,"Leeftijd",iRubriekStyle,pAgeId,participantsAges,"deelnemers_age","name",searchUrl,"pa") --%>
      <%= getSelect(cloud,log,"Type terrein",iRubriekStyle,nReserveId,natureReserveTypes,"natuurgebieden_type","name",searchUrl,"nr") %>
      <%-- getSelect(cloud,log,"Afstand",iRubriekStyle,eDistanceId,evenementDistances,"evenement_distance","name",searchUrl,"evl") --%>
      <%-- getSelect(cloud,log,"Tijdsduur",iRubriekStyle,eDurationId,evenementDurations,"evenement_duration","name",searchUrl,"evd") --%>
      <%= getSelect(cloud,log,"Bezoekerscentrum",iRubriekStyle,departmentId,departments,"afdelingen","naam",searchUrl,"department") %>
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
</td>
</tr>
</table>
<%@include file="../whiteline.jsp" %>
<script language="JavaScript" type="text/javascript">
<%= "<!--" %>
function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}
function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}
function clearForm() {
  document.location.href = document.form1.action; 
  return false; 
}
function postIt() {
	var href = document.form1.action;
	var termsearch = document.form1.elements["termsearch"].value;
   if(termsearch != '') href += "&termsearch=" + termsearch;
<% if (!eTypeId.equals("")) {%>
		href += "&evt=<%= eTypeId %>";
<% }
	if (!pCategorieId.equals("")) {%>	
		href += "&pc=<%= pCategorieId %>";
<% }
	if (!pAgeId.equals("")) {%>		
		href += "&pa=<%= pAgeId %>";
<% }
	if (!nReserveId.equals("")) {%>		
		href += "&nr=<%= nReserveId %>";
<% }
	if (!eDistanceId.equals("")) {%>		
		href += "&evl=<%= eDistanceId %>";
<% }
	if (!eDurationId.equals("")) {%>		
		href += "&evd=<%= eDurationId %>";
<% }
	if (!departmentId.equals("")) {%>		
		href += "&department=<%= departmentId %>";
<% } %>		
	document.location = href;
   return false;
}
<%= "//-->" %>
</script>
