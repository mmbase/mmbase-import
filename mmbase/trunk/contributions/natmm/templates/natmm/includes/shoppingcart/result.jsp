<%
String fromEmail = NatMMConfig.getFromEmailAddress();
String toEmail = NatMMConfig.getToEmailAddress();
%>
<mm:node number="<%= paginaID %>" jspvar="thisPage">
   <%
   fromEmail = thisPage.getStringValue("titel_fra");
   toEmail = thisPage.getStringValue("titel_de");
   %>
</mm:node>
<%
String responseText = "De volgende bestelling is afkomstig van de Natuurmonumenten webwinkel.\n";
String responseTextToCustomer = "Geachte ";
String warningText = "<ul>";
boolean isValidAnswer = true;

String formMessageHref = "";

// *********** gender ****************  
responseText += "<br><br>Aanhef: ";
String answerValue = (String) session.getAttribute("qg");
if(answerValue==null) { answerValue = ""; }
if(answerValue.equals("")) {
    responseText += "niet ingevuld";
} else if(answerValue.equals("f")) {
    responseText += "Mevr.";
    responseTextToCustomer += "mevrouw ";
} else {
    responseText += "Dhr.";
    responseTextToCustomer += "meneer ";
}

String [] fields = { "Naam", "Adres+huisnr.", "Postcode", "Woonplaats", "Telefoon", "E-mail adres" };
String memberIDMessage = "";
String emailAddressCustomer = "";
for(int i=0; i<fields.length; i++) {

   responseText += "<br><br>" + fields[i] + ": ";
   if (i!=0) responseTextToCustomer += "<br>" + fields[i] + ": ";
   answerValue = (String) session.getAttribute("q" + i);
   if(answerValue==null) { answerValue = ""; }
   if(answerValue.equals("")) {
      answerValue = "niet ingevuld";
      if(i!=4) {
        isValidAnswer = false;
        warningText += "<li>" + fields[i] + "</li>";
      }
   } else if(i==5 && !com.cfdev.mail.verify.EmailVerifier.validateEmailAddressSyntax(answerValue)) {
     isValidAnswer = false;
     warningText += "<li>" + answerValue +  " is geen geldig emailadres</li>";
   
   } else if(i==2 ) {
      memberIDMessage = SubscribeForm.getZipCodeMessage(answerValue.toUpperCase().replaceAll(" ",""));
      if(!"".equals(memberIDMessage)) {
       isValidAnswer = false;
       warningText += ResourceBundle.getBundle("ApplicationResources").getString(memberIDMessage) + "</li>";
      }
    }
   
   responseText += answerValue;
   responseTextToCustomer += answerValue;

   if (i == 5) emailAddressCustomer = answerValue;
   if (i == 0) { 
      responseTextToCustomer += ",<br><br>Bedankt voor uw bestelling!<br>Hieronder volgen de gegevens van uw bestelling.<br>"; 
   }
}
warningText += "</ul>";

responseText += "<br><br>Lidmaatschapsnr.: ";
responseTextToCustomer += "<br>Lidmaatschapsnr.: ";

if(memberId.equals("")) {
  responseText += "niet ingevuld";
  responseTextToCustomer += "niet ingevuld";
} else {
  responseText += memberId;
  responseTextToCustomer += memberId;
}

responseText += "<br><br>Gift: ";
responseTextToCustomer += "<br>Gift: ";

if(donationStr.equals("")) {
    responseText += "geen gift";
    responseTextToCustomer += "geen gift";
    
} else {
  responseText +=  "&euro; " + nf.format(((double) Integer.parseInt(donationStr) )/100);
  responseTextToCustomer +=  "&euro; " + nf.format(((double) Integer.parseInt(donationStr) )/100);
}

if(isValidAnswer) { 

  if(products!=null) { 
      %><%@include file="getbasket.jsp" %><%
      responseText += productsStr.toString();
      responseTextToCustomer += productsStr.toString();
      responseTextToCustomer += "<br><br>Met vriendelijke groet,\n<br>Natuurmonumenten Webwinkel<br>\n" +
      "<br>\nVragen over uw bestelling? Bel gratis naar 0800 023 16 66 (ma t/m do 9-21 uur en vr van 9-17 uur).<br>\n";
  }
    
  %><mm:createnode type="email" id="mail1"
        ><mm:setfield name="subject"><bean:message bundle="LEOCMS" key="shoppingcart.email_title" /></mm:setfield
        ><mm:setfield name="from"><%= fromEmail %></mm:setfield
        ><mm:setfield name="body">
        <multipart id="plaintext" type="text/plain" encoding="UTF-8">
        </multipart>
        <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
            <%= "<html>" + responseText + "</html>\n" %>
        </multipart>
        </mm:setfield
    ></mm:createnode>
    <mm:createnode type="email" id="mail2"
        ><mm:setfield name="subject"><bean:message bundle="LEOCMS" key="shoppingcart.email_title" /></mm:setfield
        ><mm:setfield name="from"><%= fromEmail %></mm:setfield
        ><mm:setfield name="body">
        <multipart id="plaintext" type="text/plain" encoding="UTF-8">
        </multipart>
        <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
            <%= "<html>" + responseTextToCustomer + "</html>\n" %>
        </multipart>
        </mm:setfield
    ></mm:createnode><%
    
    String emailAddresses = toEmail + ";"; 
    int semicolon = emailAddresses.indexOf(";");
    while(semicolon>-1) { 
        String emailAdress = emailAddresses.substring(0,semicolon);
        emailAddresses = emailAddresses.substring(semicolon+1);
        semicolon = emailAddresses.indexOf(";");
        %><mm:node referid="mail1"
            ><mm:setfield name="to"><%= emailAdress %></mm:setfield
            ><mm:field name="mail(oneshot)" 
        /></mm:node>
        
        <mm:node referid="mail2"
            ><mm:setfield name="to"><%= emailAddressCustomer %></mm:setfield
            ><mm:field name="mail(oneshot)" 
        /></mm:node>
        <%
    }
    
    formMessageHref = ph.createPaginaUrl((new RubriekHelper(cloud)).getFirstPage(subsiteID),request.getContextPath());
    session.setAttribute("totalitems","0");
    
} else { 
    String targetPage = "javascript:history.go(-1)";
    if(!"".equals(memberIDMessage)) {
      // memberid is incorrect, so redirect to shoppingcart to allow visitor to change memberid
      targetPage =  ph.createPaginaUrl("bestel",request.getContextPath());;
    }
    formMessageHref = targetPage;
} 

%>
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
  <td width="70%">
    <img src="media/trans.gif" width="1" height="11" border="0" alt=""><br>
    
    <div class="maincolor">
      <% 
      if(isValidAnswer) {
        %><bean:message bundle="LEOCMS" key="shoppingcart.order_title" /></div>
        <bean:message bundle="LEOCMS" key="shoppingcart.correct_message" /> <%= (emailAddressCustomer) %><br/><br/>
        <bean:message bundle="LEOCMS" key="shoppingcart.vragen_bestelling" /><%
      } else {
        %><bean:message bundle="LEOCMS" key="shoppingcart.order_title_incorrect" /></div>
        <bean:message bundle="LEOCMS" key="shoppingcart.incorrect_message" /> <%= warningText %><%
      } %>
      <br><br>
      <a class="maincolor_link" href="<mm:url page="<%= formMessageHref 
              %>" />"><% 
                if(isValidAnswer) {
                  %><bean:message bundle="LEOCMS" key="shoppingcart.correct_linktext" /><%
                } else {
                  %><bean:message bundle="LEOCMS" key="shoppingcart.incorrect_linktext" /><%
                } 
      %></a>
      <img src="media/trans.gif" width="10" height="1">
    </div>
  </td>
  <td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
  <td width="180" style="padding:4px;padding-top:14px;vertical-align:top;">
    <a class="maincolor_link" href="<mm:url page="<%= formMessageHref 
          %>" />"><% 
            if(isValidAnswer) {
              %><bean:message bundle="LEOCMS" key="shoppingcart.correct_linktext" /><%
            } else {
              %><bean:message bundle="LEOCMS" key="shoppingcart.incorrect_linktext" /><%
            } 
     %>&nbsp;<a class="maincolor_link" href="<mm:url page="<%= formMessageHref 
          %>" />"><img src="media/shop/back.gif" border="0" alt=""></a><br>
    <%-- @include file="relatedlinks.jsp" --%>
    <img src="media/trans.gif" height="1" width="180" border="0" alt=""><br>
  </td>
</tr>
</table>