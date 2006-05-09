<%  String responseText = "Deze email is verstuurd via een formulier uit het intranet.<br><br>";
    String subjectText = "";
    boolean isValidAnswer = true;
    String warningMessage = "U bent vergeten de volgende velden in te vullen:<ul>";
    String visitorAddress = "";

    int q = 0;
    Vector questions = new Vector();
    Vector answers = new Vector();
    String username =  ""; 
%><mm:list nodes="<%= pageId %>" path="pagina,posrel,formulier" 
    fields="formulier.number,formulier.titel,formulier.emailadressen" 
    orderby="posrel.pos" directions="UP"

    ><% String formulier_number = ""; 
    %><mm:field name="formulier.number" jspvar="dummy" vartype="String" write="false"
        ><% formulier_number= dummy; 
    %></mm:field

    ><% String formulier_title = ""; 
    %><mm:field name="formulier.titel" jspvar="dummy" vartype="String" write="false"
        ><% formulier_title = dummy;
            subjectText = formulier_title;
    %></mm:field
    
    ><% String formulier_editors_note = ""; 
    %><mm:field name="formulier.emailadressen" jspvar="dummy" vartype="String" write="false"
        ><% formulier_editors_note = dummy; 
    %></mm:field
    
    ><% String formulier_copyright = ""; 
    %><mm:field name="formulier.titel_de" jspvar="dummy" vartype="String" write="false"
        ><% formulier_copyright = ";" + dummy + ";"; 
    %></mm:field

    ><% String formulier_titel_fra = ""; 
    %><mm:field name="formulier.titel_fra" jspvar="dummy" vartype="String" write="false"
        ><% formulier_titel_fra = dummy; 
    %></mm:field
	 
	 ><% String formulier_omschrijving = ""; 
    %><mm:field name="formulier.omschrijving" jspvar="dummy" vartype="String" write="false"
        ><% formulier_omschrijving = dummy; 
    %></mm:field
	 
	 ><mm:list nodes="<%= formulier_number %>" path="formulier,posrel,formulierveld"
        orderby="posrel.pos" directions="UP"

        ><% String questions_type = ""; 
        %><mm:field name="formulierveld.type" jspvar="dummy" vartype="String" write="false"
            ><% questions_type = dummy; 
        %></mm:field
        
        ><% boolean isRequired = false; 
        %><mm:field name="formulierveld.verplicht" jspvar="dummy" vartype="String" write="false"
            ><% isRequired = dummy.equals("1"); 
        %></mm:field
        
        ><% String questions_number = ""; 
        %><mm:field name="formulierveld.number" jspvar="dummy" vartype="String" write="false"
            ><% questions_number= dummy; 
        %></mm:field
        
        ><% boolean inSubject = false;
        
        // *** add the answers to the following questions to the subject ***
        %><mm:field name="posrel.pos" jspvar="position" vartype="String" write="false"><%
            if(formulier_copyright.indexOf(";" + position + ";")>-1) {
                inSubject = true; 
            }
        %></mm:field><%

        // ********** check boxes ******************
        if(questions_type.equals("5")) { 
            %><mm:field name="formulierveld.label" jspvar="questions_title" vartype="String" write="false"><%
                responseText += "<br>" + questions_title + ": "; 
                boolean hasSelected = false; 
                String csAnswers = "";
                %><mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierantwoord" orderby="posrel.pos" directions="UP"
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
                ></mm:list><%
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
            %></mm:field><% 
        } 
        
        // ********* textarea, textline, dropdown, radio buttons **********
        if(!questions_type.equals("5")) { 
            %><mm:field name="formulierveld.label" jspvar="questions_title" vartype="String" write="false"><%
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
                q++;
            %></mm:field><% 
        } 
    %></mm:list
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
                ><mm:setfield name="from"><%= defaultFromAddress %></mm:setfield
                ><mm:setfield name="replyto"><%= defaultFromAddress %></mm:setfield
                ><mm:setfield name="body">
                    <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                    </multipart>
                    <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                        <%= "<html>" + responseText + "</html>" %>
                    </multipart>
                </mm:setfield
        ></mm:createnode><%
        
        String emailAdresses = formulier_editors_note + ";"; 
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
              ><mm:setfield name="from"><%= defaultFromAddress %></mm:setfield
              ><mm:setfield name="replyto"><%= defaultFromAddress %></mm:setfield
              ><mm:setfield name="body">
                  <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                  </multipart>
                  <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                       <%= "<html><b>" + formulier_titel_fra + "</b><br/>" + formulier_omschrijving + "</html>" %>
                  </multipart>
              </mm:setfield
          ></mm:createnode
          ><mm:node referid="mailtovisitor"
             ><mm:setfield name="to"><%= visitorAddress %></mm:setfield
             ><mm:field name="mail(oneshot)" 
          /></mm:node>

		  <%
        
        String messageTitle = subjectText;
        String messageBody = "Uw formulier is per email verstuurd naar: " + formulier_editors_note;
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
        String messageLinkParam = "target=\"_top\"";

        %><%@include file="../showmessage.jsp" %><%
    } else { 
        String messageTitle = subjectText;
        String messageBody = warningMessage + "</ul>";
        String messageHref = "javascript:history.go(-1)";
        String messageLinktext = "terug naar het formulier";
        String messageLinkParam = "";
    %><%@include file="../showmessage.jsp" %><% 
    } 
%></mm:list>
