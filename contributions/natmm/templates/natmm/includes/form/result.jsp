<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/getresponse.jsp" %>
<%
String postingStr = request.getParameter("pst");
String referer = request.getParameter("referer");
String style = request.getParameter("style");

if(referer!=null) { 
   %>
   <% if(style!=null) { %><link rel="stylesheet" type="text/css" href="<%= style %>" /><% } %>
   </head>
   <body><%
} 
%>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%
   String okSendTo = "Uw bericht is verstuurd naar";
   String okLink = "Naar de homepage";
   String okTitleDefault = "Bedankt voor uw bericht.";
   String okMessageDefault = "We zullen u zo spoedig mogelijk een reactie sturen.";
   String responseTextDefault = "E-mail verstuurd vanaf www.natuurmonumenten.nl / pagina: ";

   String noAnswer = "-";

   String warningTitle = "Uw bericht kon niet worden verstuurd om de volgende reden:";
   String warningMessage = "";
   String warningLink = "Terug naar het formulier";

   String pages_title = "";

   String defaultEmailAddress = NatMMConfig.fromEmailAddress;
   
   Vector okTitle = new Vector();
   Vector okMessage = new Vector();
   Vector emailAddress = new Vector();
   Vector formResponse = new Vector();

   boolean isValidAnswer = true;
%>
<mm:node number="<%= paginaID %>" id="this_page">
   <mm:field name="titel" jspvar="dummy" vartype="String" write="false">
      <% pages_title = dummy; %>
   </mm:field>
   <mm:related path="posrel,formulier" orderby="posrel.pos" directions="UP">
      <mm:node element="formulier" jspvar="thisForm">
         <%
            String thisFormNumber = thisForm.getStringValue("number");
            if(!thisForm.getStringValue("titel_fra").equals("")) { 
               okTitle.add(thisForm.getStringValue("titel_fra"));
            } else {
               okTitle.add(okTitleDefault);
            }
            String omschrijving_fra = thisForm.getStringValue("omschrijving_fra");
            if(omschrijving_fra!=null&&!HtmlCleaner.cleanText(omschrijving_fra,"<",">","").trim().equals("")) { 
               okMessage.add(omschrijving_fra);
            } else {
               okMessage.add(okMessageDefault);
            }
            if(!thisForm.getStringValue("emailadressen").equals("")) { 
               emailAddress.add(thisForm.getStringValue("emailadressen").trim());
            } else {
               emailAddress.add(NatMMConfig.toEmailAddress);
            }
            String responseText = "<b>" + responseTextDefault + pages_title + "</b><br>"
                  + "<br><br>" + thisForm.getStringValue("titel").toUpperCase()+ "<br>"
                  + "--------------------------------------------------------------------------<br>";
         %>
         <mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP">
            <mm:node element="formulierveld" jspvar="thisField">
            <%
               String formulierveld_number =  thisField.getStringValue("number");
               String formulierveld_label = HtmlCleaner.cleanText(thisField.getStringValue("label"),"<",">");
               String formulierveld_type =  thisField.getStringValue("type");
               boolean isRequired = thisField.getStringValue("verplicht").equals("1");
               String formulierveld_warning = "U bent vergeten '" +  formulierveld_label + "' in te vullen";
               String omschrijving = thisField.getStringValue("omschrijving");
               if(omschrijving!=null&&!HtmlCleaner.cleanText(omschrijving,"<",">","").trim().equals("")) {
                  formulierveld_warning = omschrijving;
               }
               responseText += "<br>" + formulierveld_label + ": ";

               if(formulierveld_type.equals("6")) { // *** date ***

                  String answerValue = getResponseVal("q" + thisFormNumber + "_" + formulierveld_number + "_day",postingStr);
                  if(answerValue.equals("")) {
                     responseText += noAnswer;
                     if(isRequired) {
                        isValidAnswer = false;
                        warningMessage += "<li>U bent vergeten de dag voor " + formulierveld_label + " in te vullen</li>";
                     }
                  } else {
                     responseText += answerValue;
                  }
                  answerValue = getResponseVal("q" + thisFormNumber + "_" + formulierveld_number + "_month",postingStr);
                  if(answerValue.equals("")) {
                     responseText += ", " + noAnswer;
                     if(isRequired) {
                        isValidAnswer = false;
                        warningMessage += "<li> U bent vergeten de maand voor " + formulierveld_label + " in te vullen</li>";
                     }
                  } else {
                     responseText +=  "-" + answerValue;
                  }
                  answerValue = getResponseVal("q" + thisFormNumber + "_" + formulierveld_number + "_year",postingStr);
                  if(answerValue.equals("")) {
                     responseText +=  ", " + noAnswer;
                     if(isRequired) {
                        isValidAnswer = false;
                        warningMessage += "<li>U bent vergeten het jaar voor " + formulierveld_label + " in te vullen</li>";
                     }
                  } else {
                     responseText +=  "-" + answerValue;
                  }

               } else if(formulierveld_type.equals("5")) { // *** check boxes ***
                  boolean hasSelected = false;
                  %><mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP"
                  ><mm:field name="formulierveldantwoord.number" jspvar="formulierveldantwoord_number" vartype="String" write="false"><%   
                        String answerValue = getResponseVal("q" + thisFormNumber + "_" + formulierveld_number + "_" + formulierveldantwoord_number,postingStr);
                        if(!answerValue.equals("")) {
									if (hasSelected){
										responseText += ", ";
									}
                           responseText += answerValue;
									hasSelected = true;
                        }
                     %></mm:field
                  ></mm:related><%
						String answer_else_Value = getResponseVal("q" + thisFormNumber + "_" + formulierveld_number + "_else",postingStr);
						if (!answer_else_Value.equals("")){
							if (hasSelected){
								responseText += ", ";
							}
							responseText += answer_else_Value;
							hasSelected = true;
						}
                  if(!hasSelected) {
                     responseText += noAnswer;
                     if(isRequired) {
                        isValidAnswer = false;
                        warningMessage += "<li>" + formulierveld_warning + "</li>";
                     }
                  }

               } else { // *** textarea, textline, dropdown, radio buttons ***
                  String answerValue = getResponseVal("q" + thisFormNumber + "_" + formulierveld_number,postingStr);
                  if(answerValue.equals("")) {
                     responseText += noAnswer;
                     if(isRequired) {
                        isValidAnswer = false;
                        warningMessage += "<li>" + formulierveld_warning + "</li>";
                     }
                  }
                  responseText += answerValue;
                  String useLabelAsSender = thisField.getStringValue("label_de");
                  if(useLabelAsSender!=null
                     &&useLabelAsSender.equals("1")
                     &&com.cfdev.mail.verify.EmailVerifier.validateEmailAddressSyntax(answerValue)) {
                     defaultEmailAddress = answerValue;
                  }
               }
            %></mm:node>
         </mm:related><%
         formResponse.add(responseText);
      %></mm:node>
   </mm:related>
</mm:node>
<%
if(isValidAnswer)
{  
   String formMessage = "";
   String formMessageHref = "index.jsp";
   String formMessageLinktext = okLink;

   for(int i = 0; i< 1; i++) {
      String cumulatedResponse = "";
      for(int j = 0; j< formResponse.size(); j++) {
         cumulatedResponse += "<br/><br/>" + (String) formResponse.get(j);
      }
      // ** add the referer in the plaintext part to use it for statistics **
      %><mm:remove referid="mail1" />
      <mm:createnode type="email" id="mail1">
         <mm:setfield name="from"><%= defaultEmailAddress %></mm:setfield>
         <mm:setfield name="subject"><%= pages_title %></mm:setfield>
         <mm:setfield name="replyto"><%= (String) emailAddress.get(i) %></mm:setfield>
         <mm:setfield name="mailtype">3</mm:setfield>
         <mm:setfield name="body">
             <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                  <%= session.getAttribute("form_referer") %>
             </multipart>
             <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                <%= "<html>" + cumulatedResponse + "</html>" %>
             </multipart>
          </mm:setfield>
      </mm:createnode>
      <mm:createrelation source="this_page" destination="mail1" role="related" /><%

      String thisEmailAddres = emailAddress.get(i) + ";";
      int semicolon = thisEmailAddres.indexOf(";");
      while(semicolon>-1)
      {
         String nextEmailAddress = thisEmailAddres.substring(0,semicolon);
         thisEmailAddres = thisEmailAddres.substring(semicolon+1);
         semicolon = thisEmailAddres.indexOf(";");
         %><mm:node referid="mail1">
               <mm:setfield name="to"><%= nextEmailAddress %></mm:setfield>
               <mm:field name="mail(oneshotkeep)"/>
            </mm:node><%
      }

      if(i==0) { formMessage += "<b>" + (String) okTitle.get(i) + "</b><br>" + (String) okMessage.get(i) + "<br><br>"; }
      // formMessage += okSendTo + ":  " + (String) emailAddress.get(i) + "<br><br>";
   }
  
   if(referer!=null) { 
      formMessageHref = referer;
   }

   %><%@include file="message.jsp" %><%

} else {

   String formMessage = "<b>" + warningTitle + "</b><ul>" + warningMessage + "</ul>";
   String formMessageHref = "javascript:history.go(-1)";
   String formMessageLinktext = warningLink;

   %><%@include file="message.jsp" %><%
}

if(referer!=null) { 
   %></body>
   </html><%
}
%>
</mm:cloud>
