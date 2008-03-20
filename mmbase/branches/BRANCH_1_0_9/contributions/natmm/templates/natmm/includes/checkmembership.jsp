<%
String memberID = request.getParameter("memberid");
if(memberID==null) { memberID = ""; }
memberID = SubscribeForm.cleanPid(memberID);
String zipCode = request.getParameter("zipcode");
if(zipCode==null) { zipCode = ""; }
zipCode = SubscribeForm.cleanZipCode(zipCode);

String memberIDMessage = SubscribeForm.getMemberIdMessage(memberID,zipCode);
String zipCodeMessage = SubscribeForm.getZipCodeMessage(zipCode);

boolean isMember = !memberID.equals("") && (memberIDMessage.equals("") || memberIDMessage.equals("evenementen.members.nozipcode"));
if(isMember) {
   
   %>
   <%@include file="/editors/mailer/util/memberid_set.jsp" %>
   <mm:redirect page="<%= request.getServletPath() + "?" + request.getQueryString() + "&status=checked" %>" />
   <%
   
} else {

   %>
   <mm:node number="<%= paginaID %>" jspvar="thisPage">
      <td style="vertical-align:top;width:374px;padding:10px;padding-top:0px;">
      <mm:list nodes="<%=paginaID%>" path="pagina,contentrel,artikel" max="1">
         <mm:field name="artikel.number" jspvar="artikelID" vartype="String" write="false">
            <jsp:include page="includes/artikel_1_column.jsp">
               <jsp:param name="o" value="<%=artikelID%>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
            </jsp:include>
         </mm:field>
      </mm:list>
      <%
      String messageTitle = "Alleen toegankelijk voor " + NatMMConfig.getCompanyName() + " leden";
      String messageText = "Deze pagina is alleen toegankelijk voor leden van " + NatMMConfig.getCompanyName() + ".<br/>";
      String messageText2 = "";
      String submitButton = "LOG IN ALS LID";
      %><mm:relatednodes type="formulier" max="1" jspvar="thisForm"><%
         messageTitle = (thisForm.getStringValue("titel")!=null && !thisForm.getStringValue("titel").trim().equals("") ? 
                           thisForm.getStringValue("titel") : messageTitle);
         messageText  = (thisForm.getStringValue("omschrijving")!=null && !thisForm.getStringValue("omschrijving").trim().equals("") ? 
                           thisForm.getStringValue("omschrijving") : messageText);
         messageText2 = (thisForm.getStringValue("omschrijving_de")!=null && !thisForm.getStringValue("omschrijving_de").trim().equals("") ?
                           thisForm.getStringValue("omschrijving_de") : messageText2);
         submitButton = (thisForm.getStringValue("emailonderwerp")!=null && !thisForm.getStringValue("emailonderwerp").trim().equals("") ?
                           thisForm.getStringValue("emailonderwerp") : submitButton);
      %></mm:relatednodes>
      
      <span class="colortitle"><%= messageTitle %></span><br/>
      <div style="margin:9px 0px 0px 0px">
         <%= messageText %><br/>
         Vul uw postcode en lidmaatschapsnummer in. Van het lidmaatschapsnummer 
         hoeft u alleen de cijfers in te vullen. Klik vervolgens op "<%= submitButton.substring(0,1).toUpperCase() + submitButton.substring(1).toLowerCase() %>".
         <% if(!memberID.equals("") || !zipCode.equals("")) { %>
            <span class="colortitle" style="color:red;">
               <%
               if(!zipCodeMessage.equals("")) { 
                  %><bean:message bundle="LEOCMS" key="<%= zipCodeMessage %>" /><% 
               } 
               if(!memberIDMessage.equals("")) { 
                  %><bean:message bundle="LEOCMS" key="<%= memberIDMessage  %>" /><% 
               }
               %>
           </span>
         <% } %>
      </div>
      <br/>
      <a name="form"></a>
      <form name="emailform" method="post" target="" action="">
      <table border="0" style="width:100%;" cellpadding="0" cellspacing="0">
         <tr><td style="padding-left:50px;">
         <table cellspacing="0" cellpadding="0" border="0" style="width:100%;">
            <tr>
              <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;"><nobr>&nbsp;lidmaatschapsnummer&nbsp;*&nbsp;</nobr></td>
              <td class="maincolor" style="width:177px;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
                 <input type="text" name="memberid" value="<%= memberID %>" style="width:100%;border:0;">
              </td>
            </tr>
            <tr><td colspan="2" style="height:10px;"></td></tr>
            <tr>
              <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;"><nobr>&nbsp;postcode&nbsp;*&nbsp;</nobr></td>
              <td class="maincolor" style="width:177px;padding:0px;padding-right:1px;vertical-align:top;<% if(!isIE) { %>padding-top:1px;<% } %>">
                 <input type="text" name="zipcode" value="<%= zipCode %>"  style="width:100%;border:0;">
              </td>
            </tr>
            <tr>
              <td colspan="2">(*) Vul minimaal deze velden in i.v.m. een correcte afhandeling.</td>
           </tr>
           <tr>
              <td colspan="2" align="right">
                  <input type="submit" value="<%= submitButton %>" class="submit_image" style="width:165;" />
              </td>
           </tr>
         </table>
         </td></tr>
      </table>
      </form>
      <%= messageText2 %>
   </td>
   <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
      <jsp:include page="includes/navright.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="lnr" value="<%= lnRubriekID %>" />
      </jsp:include>
      <jsp:include page="includes/shorty.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="2" />
      </jsp:include>
      <img src="media/trans.gif" height="1px" width="165px;" />
   </td>
   </mm:node>
   <% 
}
%>