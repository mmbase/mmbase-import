<%@include file="../whiteline.jsp" %>
<table cellpadding="0" cellspacing="0" border="0" style="width:190px;" align="center">
<tr>
<td>
   <form name="form1">
      <%@include file="selectkeyword.jsp"%>
      <%@include file="selectpool.jsp"%>
      <% 
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
      <%@include file="selectcompetencies.jsp"%>
   </form>
   <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
   	<tr>
       	<td>
          	<input type="submit" name="submit" value="Terug" class="<%= cssClassName %>" style="text-align:center;font-weight:bold;width:50px;" onClick="javascript:history.go(-1);">
   		</td>
         <td style="text-align:right;padding-right:10px;">
            <input type="submit" name="submit" value="Wis" class="<%= cssClassName %>" style="text-align:center;font-weight:bold;width:50px;" onClick="javascript:clearForm();">
   		</td>
      </tr>
   </table>
   <br/>
   <a href="educations.jsp?p=competenties" style="color:#FFFFFF;">Wat zijn competenties?</a>
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
  document.location = "educations.jsp?p=<%= pageId %>&h=&k=&j=&t=&u="; 
  return false; 
}
<%= "//-->" %>
</script>






