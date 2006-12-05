<%@include file="/taglibs.jsp" %>
<%@include file="../../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="contentrel.pos=14"
><mm:node element="artikel"
><table width="100%" cellspacing="0" cellpadding="0">
<tr>
    <td width="20%"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
    <td width="60%">
    <img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
    <%  String responseText = "";
        %><mm:field name="intro" jspvar="articles_intro" vartype="String" write="false"
                ><% responseText = articles_intro;
        %></mm:field><%
        
        String warningText = "<ul>";
        boolean isValidAnswer = true;
        postingStr += "$";
        
        String formMessage = "";
        String formMessageLinktext = "";
        String formMessageHref = "";
        String paragraphConstraint = "";
        
        // *********** gender ****************  
        responseText += "<br><br>Aanhef: ";
        String answerValue = (String) session.getAttribute("q0");
        if(answerValue==null) { answerValue = ""; }
        if(answerValue.equals("")) {
            responseText += "niet ingevuld";
        } else if(answerValue.equals("f")) {
            responseText += "Mevr.";
        } else {
            responseText += "Dhr.";
        }
        int questions_number =1;
    %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel,posrel,paragraaf" fields="paragraaf.omschrijving"
                    constraints="contentrel.pos=13 AND posrel.pos > 1 AND posrel.pos < 8"
                    orderby="posrel.pos" directions="UP"
        ><mm:field name="posrel.pos" jspvar="posrel2_pos" vartype="String" write="false"
        ><mm:field name="paragraaf.titel" jspvar="questions_title" vartype="String" write="false"
        ><% responseText += "<br><br>" + questions_title + ": ";
            answerValue = (String) session.getAttribute("q" + questions_number);
            if(answerValue==null) { answerValue = ""; }
            if(answerValue.equals("")) {
                answerValue = "niet ingevuld";
                if(!posrel2_pos.equals("6")) {
                    isValidAnswer = false;
                    warningText += "<li>" + questions_title + "</li>";
                }
            }
            responseText += answerValue;
            questions_number++;
        %></mm:field
        ></mm:field
    ></mm:list><%
    
    responseText += "<br><br>Lidmaatschapsnr.: ";
    if(memberId.equals("")) {
        responseText += "niet ingevuld";
    }
    responseText += memberId;
    
    responseText += "<br><br>Gift: ";
    if(donationStr.equals("")) {
        responseText += "geen gift";
    }
    responseText +=  "&euro; " + nf.format(((double) Integer.parseInt(donationStr) )/100);
    
    %><mm:field name="titel" jspvar="article_title" vartype="String" write="false"
    ><mm:field name="titel_fra" jspvar="article_titel_fra" vartype="String" write="false"
    ><%
    
    if(isValidAnswer) { 
    
        if(products!=null) { 
            %><%@include file="../includes/getbasket.jsp" %><%
            responseText += productsStr;
        }
        
        %><%-- // *** email1 code ***
        String emailAdresses = article_titel_fra + ";"; 
        int semicolon = emailAdresses.indexOf(";");
        while(semicolon>-1) { 
            String emailAdress = emailAdresses.substring(0,semicolon);
            emailAdresses = emailAdresses.substring(semicolon+1);
            semicolon = emailAdresses.indexOf(";");
            %><mm:createnode type="email"
                ><mm:setfield name="subject"><%= article_title %></mm:setfield
                ><mm:setfield name="from">shop@natuurmonumenten.nl</mm:setfield
                ><mm:setfield name="to"><%= emailAdress %></mm:setfield
                ><mm:setfield name="replyto">shop@natuurmonumenten.nl</mm:setfield
                ><mm:setfield name="body"><%= "<HTML>" + responseText + "</HTML>" %></mm:setfield
            ></mm:createnode><%
        }
        
        // ****** email2 code ***********
        --%><mm:createnode type="email" id="mail1"
                ><mm:setfield name="subject"><%= article_title %></mm:setfield
                ><mm:setfield name="from">shop@natuurmonumenten.nl</mm:setfield
                ><mm:setfield name="replyto">shop@natuurmonumenten.nl</mm:setfield
                ><mm:setfield name="body">
                <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                </multipart>
                <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                    <%= "<html>" + responseText + "</html>" %>
                </multipart>
                </mm:setfield
        ></mm:createnode><%

        String emailAdresses = article_titel_fra.trim() + ";"; 
        int semicolon = emailAdresses.indexOf(";");
        while(semicolon>-1) { 
            String emailAdress = emailAdresses.substring(0,semicolon);
            emailAdresses = emailAdresses.substring(semicolon+1);
            semicolon = emailAdresses.indexOf(";");
            %><mm:node referid="mail1"
                ><mm:setfield name="to"><%= emailAdress %></mm:setfield
                ><mm:field name="mail(oneshot)" 
            /></mm:node><%
        }
                
        paragraphConstraint = "posrel.pos = 1";
        formMessage +=  " " + article_titel_fra;
        formMessageHref = pageUrl;
        session.setAttribute("totalitems","0");
    } else { 
        paragraphConstraint = "posrel.pos = 2";
        formMessage = warningText + "</ul>";
        formMessageHref = "javascript:history.go(-1)";
    } 
    
    %></mm:field
    ></mm:field
    ><mm:related path="posrel,paragraaf" constraints="<%= paragraphConstraint %>" fields="paragraaf.titel"
        ><mm:field name="paragraaf.omschrijving" jspvar="paragraaf_body" vartype="String" write="false"
            ><% formMessage = paragraaf_body + formMessage;
        %></mm:field
        ><mm:field name="paragraaf.titel" jspvar="paragraaf_title" vartype="String" write="false"
            ><% formMessageLinktext += paragraaf_title;
        %></mm:field
    ></mm:related
    
    ><div class="subtitle"><mm:field name="titel" /></div>
    <%= formMessage %><br><br>
    <a class="subtitle" href="<mm:url page="<%= formMessageHref 
            %>" />"><%= formMessageLinktext %></a><img src="media/spacer.gif" width="10" height="1"></div>
    <td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
    <td width="180"><img src="media/spacer.gif" height="1" width="180" border="0" alt=""><br>
    <table width="100%" cellspacing="0" cellpadding="0">
    <tr><td style="padding:4px;padding-top:14px;">
        <mm:import id="isfirst"
        /><a href="<mm:url page="<%= formMessageHref 
            %>" />" class="subtitle"><%= formMessageLinktext%></a>&nbsp;<a href="<mm:url page="<%= formMessageHref 
            %>" />"><img src="media/back.gif" border="0" alt=""></a><br>
        <%@include file="../includes/relatedlinks.jsp" %>
    </td></tr>
    </table>
    </td>
</tr>
</table>
</mm:node
></mm:list>
</mm:cloud>
