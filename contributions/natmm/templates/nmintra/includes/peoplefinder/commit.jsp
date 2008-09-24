<%
String date = getResponseVal("date",postingStr);
String fzText = "";
String pzText = "";
String dcText = "";
String thisPerson = "";

String messageTitle = "";
String messageBody = "";
String messageHref =  thisPage;
String messageLinktext = "terug naar '" + cloud.getNode(paginaID).getStringValue("titel") + "'";
String messageLinkParam = "";

String emailHelpText = 
    "<br><br>N.B. Op sommige computers binnen Natuurmonumenten is het niet mogelijk om direct op een link in de email te klikken."
  + "<br>Als dit bij jou het geval is moet je de volgende handelingen uitvoeren:"
  + "<br>1.open het programma Internet Explorer"
  + "<br>2.kopieer de bovenstaande link uit deze email naar de adres balk van Internet Explorer"
  + "<br>3.druk op de \"Enter\" toets";

if(date.equals("")) { // *** send an email to ask confirmation ***
    long addTime = (new Date()).getTime()/1000;
    %><mm:createnode type="medewerkers_update"
        ><mm:setfield name="externid"><%= employeeId %></mm:setfield
        ><mm:setfield name="embargo"><%= addTime %></mm:setfield
        ><mm:setfield name="firstname"><%= firstnameId %></mm:setfield
        ><mm:setfield name="initials"><%= initialsId %></mm:setfield
        ><mm:setfield name="suffix"><%= suffixId %></mm:setfield
        ><mm:setfield name="lastname"><%= lastnameId %></mm:setfield
        ><mm:setfield name="companyphone"><%= companyphoneId %></mm:setfield
        ><mm:setfield name="cellularphone"><%= cellularphoneId %></mm:setfield
        ><mm:setfield name="fax"><%= faxId %></mm:setfield
        ><mm:setfield name="email"><%= emailId %></mm:setfield
        ><mm:setfield name="job"><%= jobId %></mm:setfield
        ><mm:setfield name="omschrijving_eng"><%= omschrijving_engId %></mm:setfield
        ><mm:setfield name="omschrijving_de"><%= omschrijving_deId %></mm:setfield
        ><mm:setfield name="omschrijving_fra"><%= omschrijving_fraId %></mm:setfield
        ><mm:setfield name="omschrijving"><%= omschrijvingId %></mm:setfield
    ></mm:createnode
    ><%@include file="texts.jsp" %><%
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
        if(!dcText.equals("")) messageBody += "<br><br><br><li>Wijzigingen die na bevestiging direct worden verwerkt in \"" + specialDays + "\" en/of \"En verder\":<br>" + dcText;
        String commitLink = HttpUtils.getRequestURL(request) + templateQueryString + "&pst=|action=commit|date=" + addTime;
        %><mm:createnode type="email" id="thismail"
            ><mm:setfield name="subject">Bevestigen wijziging gegevens op de Wie-is-wie.</mm:setfield
            ><mm:setfield name="from"><%= ap.getFromEmailAddress() %></mm:setfield
            ><mm:setfield name="to"><%= emailId %></mm:setfield
            ><mm:setfield name="replyto"><%= ap.getFromEmailAddress() %></mm:setfield
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
    %><%@include file="../showmessage.jsp" %><%

} else { // *** changes are confirmed ***
    
    %><mm:list path="medewerkers_update" constraints="<%= "medewerkers_update.embargo='" + date + "'" %>" max="1"
        ><mm:node element="medewerkers_update" id="updatefound" jspvar="e"><%
            employeeId = e.getStringValue("externid");
            firstnameId = e.getStringValue("firstname");
            initialsId = e.getStringValue("initials");
            suffixId = e.getStringValue("suffix");
            lastnameId = e.getStringValue("lastname");
            companyphoneId = e.getStringValue("companyphone");
            cellularphoneId = e.getStringValue("cellularphone");
            faxId = e.getStringValue("fax");
            emailId = e.getStringValue("email");
            jobId = e.getStringValue("job");
            omschrijving_engId = e.getStringValue("omschrijving_eng"); // lokatie, regio/afdeling en functie
            omschrijving_deId = e.getStringValue("omschrijving_de");   // werkzaamheden
            omschrijving_fraId = e.getStringValue("omschrijving_fra"); // vaste vrije/werk dag(en)
            omschrijvingId = e.getStringValue("omschrijving");         // en verder
            %><mm:deletenode 
        /></mm:node
        ><%@include file="texts.jsp" %><% 

            if(!pzText.equals("")) { 
                %><mm:createnode type="email" id="thismail" 
                    ><mm:setfield name="subject"><%= "Beaufort wijzigingen voor " + thisPerson %></mm:setfield
                    ><mm:setfield name="from"><%= ap.getFromEmailAddress() %></mm:setfield
                    ><mm:setfield name="to"><%= NMIntraConfig.getDefaultPZAddress() %></mm:setfield
                    ><mm:setfield name="replyto"><%= ap.getFromEmailAddress() %></mm:setfield
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
                    ><mm:setfield name="from"><%= ap.getFromEmailAddress() %></mm:setfield
                    ><mm:setfield name="to"><%= NMIntraConfig.getDefaultFZAddress() %></mm:setfield
                    ><mm:setfield name="replyto"><%= ap.getFromEmailAddress() %></mm:setfield
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
            %><mm:node number="<%= employeeId %>" jspvar="e"><%
               if(!omschrijvingId.equals(e.getStringValue("omschrijving"))) {
                  %><mm:setfield name="omschrijving"><%= omschrijvingId %></mm:setfield><% 
               } 
               if(!omschrijvingId.equals(e.getStringValue("omschrijving_fra"))) {
                  %><mm:setfield name="omschrijving_fra"><%= omschrijving_fraId %></mm:setfield><% 
               } 
            %></mm:node><%
            
            messageTitle = "Je wijzigingen zijn bevestigd";
            messageBody = "Bedankt voor het doorgeven van je wijzigingen:<br><br><br>Je wijzigingen zijn:";
            if(!pzText.equals("")) messageBody += "<br><li>verstuurd aan de afdeling Personeelszaken (" + NMIntraConfig.getDefaultPZAddress() + ")";
            if(!fzText.equals("")) messageBody += "<br><li>verstuurd aan de afdeling Facilitaire Zaken (" + NMIntraConfig.getDefaultFZAddress() + ")";
            if(!dcText.equals("")) messageBody += "<br><li>verwerkt in \"" + specialDays + "\" en/of \"En verder\"";
            if(!pzText.equals("")||!fzText.equals("")) {
                    messageBody += "<br><br><br>Afhankelijk van de bezetting en hoeveelheid werk op deze afdelingen zullen je wijzigingen "
                    + "binnen <b>&eacute;&eacute;n tot vijf werkdagen</b> op het Intranet zichtbaar zijn."; 
            }
            %><%@include file="../showmessage.jsp" 
    %></mm:list
    ><mm:notpresent referid="updatefound"><%
        messageTitle = "Geen wijzigingen die bevestigd kunnen worden";
        messageBody = "Op dit moment bevat het Intranet geen wijzigigen die bevestigd kunnen worden. " 
                + "Als je de wijzigingen reeds hebt bevestigd heb je hiervan per email een bevestiging ontvangen. "
                + "Zo niet, dan kun je contact opnemen met <a href=\"mailto:" + ap.getFromEmailAddress() + "\">" + ap.getFromEmailAddress() + "</a>.";
        %><%@include file="../showmessage.jsp" 
    %></mm:notpresent><%
}
// keywords can be changed by anybody
if(date.equals("") && iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) {
  HashSet myKeywords = new HashSet();
  %>
  <mm:node number="<%= employeeId %>" id="e">
  <mm:related path="related,keywords">
      <mm:field name="keywords.number" jspvar="keywords_number" vartype="String" write="false">
        <% myKeywords.add(keywords_number); %>
      </mm:field>
  </mm:related>
  <mm:listnodes type="keywords" orderby="word" directions="UP">
    <mm:node id="k" />
    <mm:field name="number" jspvar="keywords_number" vartype="String" write="false">
    <% 
    String keyword = request.getParameter("keyword"+keywords_number);
    if("checked".equals(keyword)) {
      if(!myKeywords.contains(keywords_number)) {
        %><mm:createrelation source="e" destination="k" role="related" /><%
      }
    } else {
      if(myKeywords.contains(keywords_number)) {
        %>
        <mm:list nodes="<%= employeeId %>" path="medewerkers,related,keywords" constraints="<%= "keywords.number='" + keywords_number + "'" %>">
            <mm:deletenode element="related" />
        </mm:list>
        <%
      }
    } 
    %>
    </mm:field>
  </mm:listnodes>
  </mm:node>
  <%
} %>
