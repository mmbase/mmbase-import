<%@include file="../whiteline.jsp" %>
<table cellpadding="0" cellspacing="0" border="0" style="width:190px;" align="center">
<tr>
<td>
   <form name="form1">
      <%= getSelect(cloud,log,"Type activiteit",iRubriekStyle,eTypeId,eventTypes,"evenement_type","naam",searchUrl,"evt") %>
      <%= getSelect(cloud,log,"Doelgroep",iRubriekStyle,pCategorieId,participantsCategories,"deelnemers_categorie","naam",searchUrl,"pc") %>
      <%= getSelect(cloud,log,"Leeftijd",iRubriekStyle,pAgeId,participantsAges,"deelnemers_age","name",searchUrl,"pa") %>
      <%= getSelect(cloud,log,"Type terrein",iRubriekStyle,nReserveId,natureReserveTypes,"natuurgebieden_type","name",searchUrl,"nr") %>
      <%= getSelect(cloud,log,"Tijdsduur",iRubriekStyle,eDistanceId,evenementDistances,"evenement_distance","name",searchUrl,"evl") %>
      <%= getSelect(cloud,log,"Afstand",iRubriekStyle,eDurationId,evenementDurations,"evenement_duration","name",searchUrl,"evd") %>
      <%= getSelect(cloud,log,"Bezoekerscentrum",iRubriekStyle,departmentId,departments,"afdelingen","naam",searchUrl,"afdelingen") %>
   </form>
   <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
   	<tr>
       	<td>
          	<input type="submit" name="submit" value="Terug" style="text-align:center;font-weight:bold;width:50px;" onClick="javascript:history.go(-1);">
   		</td>
         <td style="text-align:right;padding-right:10px;">
            <input type="submit" name="submit" value="Wis" style="text-align:center;font-weight:bold;width:50px;" onClick="javascript:clearForm();">
   		</td>
      </tr>
   </table>
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
  document.location = "<%= localPath %>event_blueprints.jsp?p=<%= paginaID %>&evt=&pc=&pa=&nr=&evl=&evd=&department="; 
  return false; 
}
<%= "//-->" %>
</script>






