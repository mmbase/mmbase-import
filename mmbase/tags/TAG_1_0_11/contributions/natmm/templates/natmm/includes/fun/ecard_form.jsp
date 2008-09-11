<%@include file="/taglibs.jsp" %>
<%
   String rubriekID = request.getParameter("r");
   String paginaID = request.getParameter("p");
   String dossierID = request.getParameter("d");
   String imgID = request.getParameter("i");
   String mainStyleColor = request.getParameter("msc");
   String sViewCard = request.getParameter("vc");
   boolean viewCard = (sViewCard!=null) && (sViewCard.equals("true"));
   String sIsIE = request.getParameter("ie");
   boolean isIE = (sIsIE!=null) && (sIsIE.equals("true"));
   String isNaardermeer = (String) request.getAttribute("isNaardermeer");
%>
<mm:import jspvar="toname" externid="toname">-</mm:import>
<mm:import jspvar="toemail" externid="toemail">-</mm:import>
<mm:import jspvar="fromname" externid="fromname">-</mm:import>
<mm:import jspvar="fromemail" externid="fromemail">-</mm:import>
<mm:import jspvar="body" externid="body">-</mm:import>
<mm:cloud jspvar="cloud">

<form action="ecard.jsp" method="post" onSubmit="return checkForm(this);">
<input type="hidden" name="actie" value="send">
<input type="hidden" name="i" value="<%=imgID%>">

<% if (isNaardermeer.equals("true")) { %>		
   <table width="420" height="332" cellspacing="0" cellpadding="3" bordercolor="#<%= mainStyleColor %>" style="border: 1px solid">
<% } else { %>
   <table width="510" height="332" cellspacing="0" cellpadding="3" bordercolor="#<%= mainStyleColor %>" style="border: 1px solid">
<% } %>

<tr><td align="center" valign="top">

<table width="95%" height="330" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td><img src="media/trans.gif" width="2" height="1" border="0" alt=""></td>
      <td width="240" valign="top">
         <img src="media/trans.gif" width="1" height="4" border="0" alt=""><br>
         <% if(!viewCard){%><font class="boldblack2" size="2">Vul hier uw boodschap in :</font><% } %><br>
         <img src="media/trans.gif" width="1" height="4" border="0"><br>
         <% if(viewCard){%>
            <%=body.replaceAll("\n","<br>")%>
         <%}else {%>
            <textarea cols="30" rows="16" name="body" style="background-color: #E7E7E7;border: 1px solid #E7E7E7; font: normal 11px arial; scrollbar-3dlight-color: # E7E7E7; scrollbar-arrow-color: #E7E7E7; scrollbar-base-color: #E7E7E7; scrollbar-darkshadow-color: #E7E7E7; scrollbar-face-color: #E7E7E7; scrollbar-highlight-color: #E7E7E7; scrollbar-shadow-color: #E7E7E7; scrollbar-track-color: #E7E7E7; width:200px; padding: 4px 4px 4px; height:300px;"></textarea>
         <% } %>
      </td>
      <td valign="top" align="center">
         <img src="media/ecard_stamp.jpg" alt="Groeten van <%= NatMMConfig.getCompanyName() %>!!" width="91" height="92" border="0" align="right"><br clear="all"><br><br>
         <table border="0" cellspacing="0" cellpadding="0">
            <tr><td style="font-weight:bold;" colspan="2">AAN</td></tr>
            <tr>
               <td width="150" style="background-color: #<%= mainStyleColor %>;" class="formtext">Naam</td>
               <td style="background-color: #<%= mainStyleColor %>;" class="formbox">
                  <% if(viewCard){%>
                     <%=toname%>
                  <%}else {%>
                     <input type="text" name="toname" value="" size="15" style="border:#<%= mainStyleColor %>;width:100%;height:18px;<% if(!isIE) { %>margin-top:1px;margin-bottom:1px;<% } %>">
                  <% } %>
               </td>
            </tr>
            <tr><td colspan="2"><img src="media/trans.gif" width="1" height="1" vspace="3" border="0" alt=""></td></tr><tr>
            <tr>
               <td style="background-color: #<%= mainStyleColor %>;" class="formtext"><nobr>E-mail adres &nbsp;</nobr></td>
               <td style="background-color: #<%= mainStyleColor %>;" class="formbox">
                  <% if(viewCard){%>
                     <%=toemail%>
                  <%}else {%>
                     <input type="text" name="toemail" value="" size="15" style="border:#<%= mainStyleColor %>;width:100%;height:18px;<% if(!isIE) { %>margin-top:1px;margin-bottom:1px;<% } %>">
                  <% } %>
               </td>
            </tr>
            <tr><td colspan="2"><img src="media/trans.gif" width="1" height="1" vspace="3" border="0" alt=""></td></tr><tr>
            <tr><td colspan="2"  style="font-weight:bold;">VAN</td></tr>
            <tr>
               <td style="background-color: #<%= mainStyleColor %>;" class="formtext">Naam</td>
               <td style="background-color: #<%= mainStyleColor %>;" class="formbox">
                  <% if(viewCard){%>
                     <%=fromname%>
                  <%}else {%>
                     <input type="text" name="fromname" value="" size="15" style="border:#<%= mainStyleColor %>;width:100%;height:18px;<% if(!isIE) { %>margin-top:1px;margin-bottom:1px;<% } %>">
                  <% } %>
               </td>
            </tr>
            <tr><td colspan="2"><img src="media/trans.gif" width="1" height="1" vspace="3" border="0" alt=""></td></tr><tr>
            <tr>
               <td style="background-color: #<%= mainStyleColor %>;" class="formtext">E-mail adres&nbsp;</td>
               <td style="background-color: #<%= mainStyleColor %>;" class="formbox">
                  <% if(viewCard){%>
                     <%=fromemail%>
                  <%}else {%>
                     <input type="text" name="fromemail" value="" size="15" style="border:#<%= mainStyleColor %>;width:100%;height:18px;<% if(!isIE) { %>margin-top:1px;margin-bottom:1px;<% } %>">
                  <% } %>
               </td>
            </tr>
            <tr><td colspan="2"><img src="media/trans.gif" width="1" height="1" vspace="3" border="0" alt=""></td></tr><tr>
            <tr>
               <td></td>
               <td>
                  <% if(!viewCard){%>  
                     <div align="right"><input type="submit" value="Versturen" class="submit_image" style="background-color: #<%= mainStyleColor %>;width:130;" /></div>
                  <% } %>
               </td>
            </tr>
         </table>
      </td>
   </tr>
</table>
</td></tr></table><%-- einde border tabel--%>
   <table width="518" border="0" cellspacing="0" cellpadding="0">
      <tr>
         <td width="300">
         <a href="javascript:showLayerNumber(1)" class="subnav">Omdraaien</a> 
         <span class="colortitle">|</span>
         <a href="ecard.jsp?p=<%=paginaID%>&d=<%=dossierID%>" class="subnav">Terug naar kaartoverzicht</a></td>
         <td align="right" width="218"></td>
      </tr>
   </table>
</form>
<script>
   function checkForm(thisForm) {
      var warningStr = '';
      var conf = true;
      if(thisForm.body.value=='') { 
         alert('U bent vergeten een boodschap in te vullen.'); conf = false;
      } else if (thisForm.toname.value=='') {
         alert('U bent vergeten de naam van de geadresseerde in te vullen.'); conf = false;
      } else if (thisForm.toemail.value=='') {
         alert('U bent vergeten het E-mail adres van de geadresseerde in te vullen.'); conf = false;
      } else if (thisForm.fromname.value=='') {
         alert('U bent vergeten de naam van de afzender in te vullen.'); conf = false;
      } else if (thisForm.fromemail.value=='') {
         alert('U bent vergeten het E-mail adres van de afzender in te vullen.'); conf = false;
      }
      return conf;
   }
</script>
</mm:cloud>