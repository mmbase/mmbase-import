<%@ page isELIgnored="false" %>
<% 
String responseText = "Deze email is verstuurd via een formulier uit het intranet.<br><br>";
String subjectText = "";
boolean isValidAnswer = true;
String warningMessage = "U bent vergeten de volgende velden in te vullen:<ul>";
String visitorAddress = "";

int q = 0;
Vector questions = new Vector();
Vector answers = new Vector();
String username =  ""; 

%>
<mm:node number="${formnum}" jspvar="thisForm" notfound="skip">
<% 
    String formulier_number = thisForm.getStringValue("number");
    String formulier_title = thisForm.getStringValue("titel");
    subjectText = formulier_title;
    String formulier_emailaddresses = thisForm.getStringValue("emailadressen"); 
    String formulier_subjectlist = ";" + thisForm.getStringValue("titel_de") + ";";
    String formulier_confirmtitle = thisForm.getStringValue("titel_fra"); 
    String formulier_omschrijving = thisForm.getStringValue("omschrijving"); 

	  %><mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP" searchdir="destination"><% 
        
        // *** add the answers to the following questions to the subject
        boolean inSubject = false;
        %><mm:field name="posrel.pos" jspvar="position" vartype="String" write="false"><%
            inSubject = (formulier_subjectlist.indexOf(";" + position + ";")>-1); 
        %></mm:field>
        <mm:node element="formulierveld" jspvar="thisField"><% 
        
        String questions_type = thisField.getStringValue("type");
        String questions_number = thisField.getStringValue("number");
        boolean isRequired = "1".equals(thisField.getStringValue("verplicht"));
        
        // ********** check boxes ******************
        if(questions_type.equals("5")) {
            String questions_title = thisField.getStringValue("label");
            responseText += "<br>" + questions_title + ": "; 
            boolean hasSelected = false; 
            String csAnswers = "";
            %><mm:related path="posrel,formulierantwoord" orderby="posrel.pos" directions="UP" searchdir="destination"
            ><mm:field name="formulierantwoord.number" jspvar="answer_number" vartype="String" write="false"><%
                String answerValue = getResponseVal("q" + questions_number + "_" + answer_number,postingStr);
                if(!answerValue.equals("")) { 
                    hasSelected = true;
                    responseText += "<br>-&nbsp;" + answerValue;
                    if(inSubject) { subjectText += " " + answerValue; }
                }
                if(!csAnswers.equals("")) { csAnswers += ","; }
                csAnswers += answerValue;
            %></mm:field
            ></mm:related><%
            if(!hasSelected) {
                responseText += "niet ingevuld";
                if(isRequired) {
                    isValidAnswer = false;
                    warningMessage += "<li>" + questions_title + "</li>";
                }
            } 
            // extra break after checkboxes
            responseText += "<br>";
            questions.add(questions_title);
            answers.add(csAnswers);
            q++;    
        } 
        
        // ********* textarea, textline, dropdown, radio buttons **********
        if(!questions_type.equals("5")) {
          String questions_title = thisField.getStringValue("label");
          responseText += "<br>" + questions_title + ": ";
          String answerValue = getResponseVal("q" + questions_number,postingStr);
          if(answerValue.equals("")) {
              responseText += "niet ingevuld";
              if(isRequired) {
                  isValidAnswer = false;
                  warningMessage += "<li>" + questions_title + "</li>";
              }
          }
          responseText +=  answerValue;
          if(inSubject) { subjectText += " " + answerValue; }
          // *** hack: to find out email address of visitor ***
          if(questions_title.toUpperCase().indexOf("EMAIL")>-1) { visitorAddress = answerValue; }
          questions.add(questions_title);
          answers.add(answerValue);
          //// *** Jj turned of the "hack: to send email about news to specific address" ***
          //if(paginaID.equals(sWvjePageId)&&answerValue.indexOf("nieuws")>-1) {
          //  formulier_emailaddresses = NMIntraConfig.getNewsEmailAddress();
          //}
          q++;
        } 
        %></mm:node
    ></mm:related
    ><mm:createnode type="responses"
        ><mm:setfield name="title"><%= formulier_title %></mm:setfield
        ><mm:setfield name="account"><%= username %></mm:setfield
        ><mm:setfield name="responsedate"><%= (new Date()).getTime()/1000 %></mm:setfield><%
        for(int i=0; i<q; i++) { 
            %><mm:setfield name="<%= "question"+ (i+1) %>"><%= (String) questions.get(i) %></mm:setfield
            ><mm:setfield name="<%= "answer"+ (i+1) %>"><%= ((String) answers.get(i)).replace('\n',' ') %></mm:setfield><%
        }
    %></mm:createnode><%
    
    if(isValidAnswer) { 
        
        %><mm:createnode type="email" id="thismail"
              ><mm:setfield name="subject"><%= subjectText %></mm:setfield
              ><mm:setfield name="from"><%= ap.getFromEmailAddress() %></mm:setfield
              ><mm:setfield name="replyto"><%= ap.getFromEmailAddress() %></mm:setfield
              ><mm:setfield name="body">
                  <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                  </multipart>
                  <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                      <%= "<html>" + responseText + "</html>" %>
                  </multipart>
              </mm:setfield
        ></mm:createnode><%
        
        String emailAdresses = formulier_emailaddresses + ";"; 
        int semicolon = emailAdresses.indexOf(";");
        while(semicolon>-1) { 
            String emailAdress = emailAdresses.substring(0,semicolon);
            emailAdresses = emailAdresses.substring(semicolon+1);
            semicolon = emailAdresses.indexOf(";");
            %><mm:node referid="thismail"
                ><mm:setfield name="to"><%= emailAdress %></mm:setfield
                ><mm:field name="mail(oneshot)" 
            /></mm:node><%
        } 
        
        %><mm:createnode type="email" id="mailtovisitor"
              ><mm:setfield name="subject"><%= subjectText %></mm:setfield
              ><mm:setfield name="from"><%= ap.getFromEmailAddress() %></mm:setfield
              ><mm:setfield name="replyto"><%= ap.getFromEmailAddress() %></mm:setfield
              ><mm:setfield name="body">
                  <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                  </multipart>
                  <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                       <%= "<html><b>" + formulier_confirmtitle + "</b><br/>" + responseText + "</html>" %>
                  </multipart>
              </mm:setfield
         ></mm:createnode
         ><mm:node referid="mailtovisitor"
             ><mm:setfield name="to"><%= visitorAddress %></mm:setfield
             ><mm:field name="mail(oneshot)" 
         /></mm:node>
         <%
        
         String messageTitle = subjectText;
         String messageBody = "Uw formulier is per email verstuurd naar: " + formulier_emailaddresses;
         String messageHref = "";
         String messageLinktext = "naar de homepage";
         if(sPageRefMinOne!=null) {
            %><mm:node number="<%= sPageRefMinOne %>" jspvar="lastPage" notfound="skipbody"><%
               messageLinktext = "terug naar pagina \"" + lastPage.getStringValue("titel") + "\""; %>
					    <mm:list nodes="<%= sPageRefMinOne %>" path="pagina,gebruikt,paginatemplate">
                <mm:field name="paginatemplate.url" jspvar="url" vartype="String" write="false">
                  <% messageHref += url; %>
                </mm:field>	
              </mm:list>
              <% messageHref  += "?p=" + sPageRefMinOne;
	         %></mm:node><%
	       }
        String messageLinkParam = "";

        %><%@include file="../showmessage.jsp" %><%
    } else { 
        String messageTitle = subjectText;
        String messageBody = warningMessage + "</ul>";
        String messageHref = "javascript:history.go(-1)";
        String messageLinktext = "terug naar het formulier";
        String messageLinkParam = "";
    %><%@include file="../showmessage.jsp" %><% 
    } 
    %>
</mm:node>