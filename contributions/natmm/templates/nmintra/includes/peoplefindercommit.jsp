<%
String date = getResponseVal("date",postingStr);
String fzText = "";
String pzText = "";
String dcText = "";
String thisPerson = "";

String messageTitle = "";
String messageBody = "";
String messageHref = "smoelenboek.jsp";
String messageLinktext = "naar de homepage";
String messageLinkParam = "target=\"_top\"";    

if(date.equals("")) { // *** send an email to ask confirmation ***
    long addTime = (new Date()).getTime()/1000;
    %><mm:createnode type="empupdates"
        ><mm:setfield name="externid"><%= employeeId %></mm:setfield
        ><mm:setfield name="enrolldate"><%= addTime %></mm:setfield
        ><mm:setfield name="firstname"><%= firstnameId %></mm:setfield
        ><mm:setfield name="initials"><%= initialsId %></mm:setfield
        ><mm:setfield name="suffix"><%= suffixId %></mm:setfield
        ><mm:setfield name="lastname"><%= lastnameId %></mm:setfield
        ><mm:setfield name="companyphone"><%= companyphoneId %></mm:setfield
        ><mm:setfield name="cellularphone"><%= cellularphoneId %></mm:setfield
        ><mm:setfield name="fax"><%= faxId %></mm:setfield
        ><mm:setfield name="email"><%= emailId %></mm:setfield
        ><mm:setfield name="deptdescr"><%= deptdescrId %></mm:setfield
        ><mm:setfield name="position"><%= posdescrId %></mm:setfield
        ><mm:setfield name="progdescr"><%= progdescrId %></mm:setfield
        ><mm:setfield name="intro"><%= introupdateId %></mm:setfield
        ><mm:setfield name="description"><%= descrupdateId %></mm:setfield
    ></mm:createnode
    ><%@include file="../includes/peoplefindertexts.jsp" %><%
     if(emailId.indexOf("@")==-1) {
          messageBody = "Het emailadres wat je hebt opgegeven is geen geldig emailadres. Gebruik de onderstaande link om terug te gaan naar het formulier"
            + " en alsnog een geldig emailadres in te vullen.";
          messageHref = "javascript:history.go(-1);";
          messageLinktext = "naar het formulier";
    } else if(pzText.equals("")&&fzText.equals("")&&dcText.equals("")) {
         messageBody = "Er zijn geen wijzigingen in het formulier ingevuld. Gebruik de onderstaande link om terug te gaan naar het formulier"
            + " en de wijzigingen alsnog in te vullen.";
         messageHref = "javascript:history.go(-1);";
         messageLinktext = "naar het formulier";
    } else {
        if(!pzText.equals("")) messageBody += "<br><br><br><li>Wijzigingen die na bevestiging worden verstuurd aan de afdeling Personeelszaken:<br>" + pzText;
        if(!fzText.equals("")) messageBody += "<br><br><br><li>Wijzigingen die na bevestiging worden verstuurd aan de afdeling Facilitaire Zaken:<br>" + fzText;
        if(!dcText.equals("")) messageBody += "<br><br><br><li>Wijzigingen die na bevestiging direct worden verwerkt in \"Vaste vrije dagen\" en/of \"En verder\":<br>" + dcText;
        String commitLink = HttpUtils.getRequestURL(request) + templateQueryString + "&pst=|action=commit|date=" + addTime;
        %><mm:createnode type="email" id="thismail"
            ><mm:setfield name="subject">Bevestigen wijziging gegevens op de Wie-is-wie.</mm:setfield
            ><mm:setfield name="from"><%= defaultFromAddress %></mm:setfield
            ><mm:setfield name="to"><%= emailId %></mm:setfield
            ><mm:setfield name="replyto"><%= defaultFromAddress %></mm:setfield
            ><mm:setfield name="body">
                <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                </multipart>
                <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                    <%= "<html>" + "<br><br>Klik op de onderstaande link om de wijzigingen te bevestigen. "
                    + "Nadat je op de link geklikt hebt, duurt het maximaal een minuut voor u binnen Internet Explorer het bericht krijgt dat uw bevestiging is verwerkt.<br><br>"
                    + "<a href=\"" + commitLink + "\">" + commitLink + "</a>"
                    + emailHelpText
                    + messageBody
                    + "</html>" %>
                </multipart>
            </mm:setfield
        ></mm:createnode
        ><mm:node referid="thismail"
            ><mm:field name="mail(oneshot)" 
        /></mm:node
        ><mm:remove referid="thismail" /><%

        messageBody = "De wijzigingen zijn ontvangen. Je ontvangt een mail in je mailbox, waarmee je de zojuist verstuurde " 
            + "wijzigingen moet bevestigen.<br><br><br><b>Pas na bevestiging zullen de wijzigingen ter verwerking aan de afdelingen " 
            + "Personeelszaken en Facilitaire Zaken worden verstuurd.</b>" + messageBody;
    }

    messageTitle = "Bevestiging wijzigingen persoonsgegevens";
    %><%@include file="../includes/showmessage.jsp" %><%

} else { // *** changes are confirmed ***
    
    %><mm:list path="empupdates" constraints="<%= "empupdates.enrolldate='" + date + "'" %>" max="1"
        ><mm:node element="empupdates" id="updatefound"
            ><mm:field name="externid" jspvar="dummy" vartype="String" write="false"><% employeeId = dummy; %></mm:field
            ><mm:field name="firstname" jspvar="dummy" vartype="String" write="false"><% firstnameId = dummy; %></mm:field
            ><mm:field name="initials" jspvar="dummy" vartype="String" write="false"><% initialsId = dummy; %></mm:field
            ><mm:field name="suffix" jspvar="dummy" vartype="String" write="false"><% suffixId = dummy; %></mm:field
            ><mm:field name="lastname" jspvar="dummy" vartype="String" write="false"><% lastnameId = dummy; %></mm:field
            ><mm:field name="companyphone" jspvar="dummy" vartype="String" write="false"><% companyphoneId = dummy; %></mm:field
            ><mm:field name="cellularphone" jspvar="dummy" vartype="String" write="false"><% cellularphoneId = dummy; %></mm:field
            ><mm:field name="fax" jspvar="dummy" vartype="String" write="false"><% faxId = dummy; %></mm:field
            ><mm:field name="email" jspvar="dummy" vartype="String" write="false"><% emailId = dummy; %></mm:field
            ><mm:field name="deptdescr" jspvar="dummy" vartype="String" write="false"><% deptdescrId = dummy; %></mm:field
            ><mm:field name="posdescr" jspvar="dummy" vartype="String" write="false"><% posdescrId = dummy; %></mm:field
            ><mm:field name="progdescr" jspvar="dummy" vartype="String" write="false"><% progdescrId = dummy; %></mm:field
            ><mm:field name="intro" jspvar="dummy" vartype="String" write="false"><% introupdateId = dummy; %></mm:field
            ><mm:field name="description" jspvar="dummy" vartype="String" write="false"><% descrupdateId = dummy; %></mm:field
            ><mm:deletenode 
        /></mm:node
        ><%@include file="../includes/peoplefindertexts.jsp" %><% 

            if(!pzText.equals("")) { 
                %><mm:createnode type="email" id="thismail" 
                    ><mm:setfield name="subject"><%= "Beaufort wijzigingen voor " + thisPerson %></mm:setfield
                    ><mm:setfield name="from"><%= defaultFromAddress %></mm:setfield
                    ><mm:setfield name="to"><%= defaultPZAddress %></mm:setfield
                    ><mm:setfield name="replyto"><%= defaultFromAddress %></mm:setfield
                    ><mm:setfield name="body">
                        <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                        </multipart>
                        <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                            <%= "Beaufort wijzigingen voor " + thisPerson + "<br><br>" + pzText %>
                        </multipart>
                    </mm:setfield
                ></mm:createnode
                ><mm:node referid="thismail"
                    ><mm:field name="mail(oneshot)" 
                /></mm:node
                ><mm:remove referid="thismail" /><%
            }
            if(!fzText.equals("")) { 
                %><mm:createnode type="email" id="thismail" 
                    ><mm:setfield name="subject"><%= "Telefoonboek wijzigingen voor " + thisPerson %></mm:setfield
                    ><mm:setfield name="from"><%= defaultFromAddress %></mm:setfield
                    ><mm:setfield name="to"><%= defaultFZAddress %></mm:setfield
                    ><mm:setfield name="replyto"><%= defaultFromAddress %></mm:setfield
                    ><mm:setfield name="body">
                        <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                        </multipart>
                        <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                            <%= "Telefoonboek wijzigingen voor " + thisPerson + "<br><br>" + fzText %>
                        </multipart>
                    </mm:setfield
                ></mm:createnode
                ><mm:node referid="thismail"
                    ><mm:field name="mail(oneshot)" 
                /></mm:node
                ><mm:remove referid="thismail" /><%
            }
            %><mm:node number="<%= employeeId %>"
                ><mm:field name="omschrijving" jspvar="dummy" vartype="String" write="false"><%
                    if(!descrupdateId.equals(dummy)) {
                       %><mm:setfield name="omschrijving"><%= descrupdateId %></mm:setfield><%
                    }
                %></mm:field
            ></mm:node><%
            
            messageTitle = "Je wijzigingen zijn bevestigd";
            messageBody = "Bedankt voor het doorgeven van je wijzigingen:<br><br><br>Je wijzigingen zijn:";
            if(!pzText.equals("")) messageBody += "<br><li>verstuurd aan de afdeling Personeelszaken (" + defaultPZAddress + ")";
            if(!fzText.equals("")) messageBody += "<br><li>verstuurd aan de afdeling Facilitaire Zaken (" + defaultFZAddress + ")";
            if(!dcText.equals("")) messageBody += "<br><li>verwerkt in \"Vaste vrije dagen\" en/of \"En verder\"";
            if(!pzText.equals("")||!fzText.equals("")) {
                    messageBody += "<br><br><br>Afhankelijk van de bezetting en hoeveelheid werk op deze afdelingen zullen je wijzigingen "
                    + "binnen <b>&eacute;&eacute;n tot vijf werkdagen</b> op het Intranet zichtbaar zijn."; 
            }
            %><%@include file="../includes/showmessage.jsp" 
    %></mm:list
    ><mm:notpresent referid="updatefound"><%
        messageTitle = "Geen wijzigingen die bevestigd kunnen worden";
        messageBody = "Op dit moment bevat het Intranet geen wijzigigen die bevestigd kunnen worden. " 
                + "Als je de wijzigingen reeds hebt bevestigd heb je hiervan per email een bevestiging ontvangen. "
                + "Zo niet, dan kun je contact opnemen met <a href=\"mailto:" + defaultFromAddress + "\">" + defaultFromAddress + "</a>.";
        %><%@include file="../includes/showmessage.jsp" 
    %></mm:notpresent><%
} %>