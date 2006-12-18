<%@include file="/taglibs.jsp" %>
<%@include file="../../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
    <td width="20%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
    <td width="60%">
    <img src="media/trans.gif" width="1" height="11" border="0" alt=""><br>
    <%  String responseText = "Articles intro";
        
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
    %><mm:list nodes="<%= paginaID %>" path="posrel,formulierveld" orderby="posrel.pos" directions="UP"
   				><mm:first><% responseText += "<ol>\n<li>"; %></mm:first
   				><mm:first inverse="true"><% responseText += "</li>\n<li>"; %></mm:first>
					<mm:node element="formulierveld" jspvar="thisQuestion"><%
   				
						String questions_number = thisQuestion.getStringValue("number"); 
						String questions_title = thisQuestion.getStringValue("label"); 
						if(numberOrdered>1) { questions_title += " (item nummer " + (i+1) + ")"; }
						String questions_type = thisQuestion.getStringValue("type");
						boolean isRequired = thisQuestion.getStringValue("verplicht").equals("1");
						
						responseText += questions_title + " : "; 
						
						if(questions_type.equals("6")) { // *** date ***
						
							responseText += "(Dag,Maand,Jaar) ";
							String answerValue = getResponseVal("q_" + thisForm + "_" + questions_number + "_" + i + "_day",postingStr);
							if(answerValue.equals("")) {
								responseText += noAnswer;
								if(isRequired) {
									isValidAnswer = false;
									warningMessage += "&#149; Dag in " + questions_title + "<br>";
								}
							} else {
								responseText += answerValue;
							}
							answerValue = getResponseVal("q_" + thisForm + "_" + questions_number + "_" + i + "_month",postingStr);
							if(answerValue.equals("")) {
								responseText += ", " + noAnswer;
								if(isRequired) {
									isValidAnswer = false;
									warningMessage += "&#149; Maand in " + questions_title + "<br>";
								}
							} else {
								responseText += ", " + answerValue;
							}
							answerValue = getResponseVal("q_" + thisForm + "_" + questions_number + "_" + i + "_year",postingStr);
							if(answerValue.equals("")) {
								responseText +=  ", " + noAnswer;
								if(isRequired) {
									isValidAnswer = false;
									warningMessage += "&#149; Jaar in " + questions_title + "<br>";
								}
							} else {
								responseText +=  ", " + answerValue;
							}
		
						} else if(questions_type.equals("5")) { // *** check boxes ***
							boolean hasSelected = false; 
							%><mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP"
								><mm:field name="formulierveldantwoord.number" jspvar="answer_number" vartype="String" write="false"><%
								String answerValue = getResponseVal("q_" + thisForm + "_" + questions_number + "_" + i + "_" + answer_number,postingStr);
								if(!answerValue.equals("")) { 
									hasSelected = true;
									responseText += "<br>&#149; " + answerValue;
								}
								%></mm:field
							></mm:related><%
							if(!hasSelected) {
								responseText += noAnswer;
								if(isRequired) {
									isValidAnswer = false;
									warningMessage += "&#149; " + questions_title + "<br>";
								}
							} 
		
						} else { // *** textarea, textline, dropdown, radio buttons ***
							String answerValue = getResponseVal("q_" + thisForm + "_" + questions_number + "_" + i,postingStr);
							if(answerValue.equals("")) {
								responseText += noAnswer;
								if(isRequired) {
									isValidAnswer = false;
									warningMessage += "&#149; " + questions_title + "<br>";
								}
							}
							responseText += answerValue;
							// *** check whether this question provides the client email address ***
							// *** the object cloud has to contain a question with alias client_email ***
							%><mm:list nodes="client_email" path="formulierveld" constraints="<%= "formulierveld.number=" + questions_number %>"><%
									clientEmail = answerValue;
							%></mm:list><%
							
							// *** check whether this question provides the client email address ***
							// *** the object cloud has to contain a question with alias client_department ***
							%><mm:list nodes="client_department" path="formulierveld" constraints="<%= "formulierveld.number=" + questions_number %>"><%
									clientDept = answerValue;
							%></mm:list><%
						} 
   				%>
					</mm:node>
					<mm:last><% responseText += "</li>\n</ol>\n"; %></mm:last
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
        formMessageHref = ph.createPaginaUrl(paginaID,request.getContextPath());
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
    
    ><divclass="colortitle"><mm:field name="titel" /></div>
    <%= formMessage %><br><br>
    <aclass="colortitle" href="<mm:url page="<%= formMessageHref 
            %>" />"><%= formMessageLinktext %></a><img src="media/trans.gif" width="10" height="1"></div>
    <td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
    <td width="180"><img src="media/trans.gif" height="1" width="180" border="0" alt=""><br>
    <table width="100%" cellspacing="0" cellpadding="0">
    <tr><td style="padding:4px;padding-top:14px;">
        <mm:import id="isfirst"
        /><a href="<mm:url page="<%= formMessageHref 
            %>" />"class="colortitle"><%= formMessageLinktext%></a>&nbsp;<a href="<mm:url page="<%= formMessageHref 
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
