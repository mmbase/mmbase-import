<%@page import="java.util.*,nl.leocms.util.DoubleDateNode" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" method="http" rank="basic user">
<mm:import externid="event" jspvar="nodenr">-1</mm:import>
<mm:import externid="emailto" jspvar="toAddress">-1</mm:import>
<mm:import externid="extratekst" jspvar="extraText">-1</mm:import>
<mm:node number="<%= nodenr %>" jspvar="thisEvent" notfound="skipbody"><%
DoubleDateNode ddn = new DoubleDateNode(); 
ddn.setBegin(new Date(thisEvent.getLongValue("begindatum")*1000));
ddn.setEnd(new Date(thisEvent.getLongValue("einddatum")*1000));  

if(!toAddress.equals("-1")) { 
   String sPageURI = "/SubscribeInitAction.eb?number=" + nodenr + "&action=printsubscriptions&orderby=lastname&direction=up&showpastdates=true";
   String sPageURI2 = "/editors/evenementen/SubscribeInitAction.eb?number=" + nodenr + "&action=printsubscriptions&orderby=lastname&direction=up&showpastdates=true";
   String sPageURL = HttpUtils.getRequestURL(request).toString(); 
   sPageURL = sPageURL.substring(0,sPageURL.substring(7).indexOf("/")+7); 
   String subject = "Aanmeldingen " + thisEvent.getStringValue("titel") + " " + ddn.getReadableDate() + ", " + ddn.getReadableTime();
   String fromAddress = "website@natuurmonumenten.nl";
   %><mm:listnodes type="users" constraints="<%= "[account]='" + cloud.getUser().getIdentifier() + "'" %>" max="1" id="thisuser">
      <mm:field name="emailadres" jspvar="users_email" vartype="String" write="false">
         <% if(users_email.indexOf("@")>-1) { fromAddress = users_email; } %>
      </mm:field>
   </mm:listnodes>
   <mm:createnode type="email" id="mail">
		<mm:setfield name="from"><%= fromAddress %></mm:setfield>
		<mm:setfield name="subject"><%= subject %></mm:setfield>
		<mm:setfield name="body">
			<multipart id="plaintext" type="text/plain" encoding="UTF-8">
				Bekijk de aanmeldingen op: <%= sPageURL%><mm:url page="/editors/evenementen"/><%=sPageURI %>
                        
            <% if ((extraText != null) && (!"".equals(extraText))) { %>
            \n\nExtra opmerkingen:\n
            <%=extraText%>
            <% } %>
            
			</multipart>
			<multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
            Slecht leesbaar? Print aanmeldingen vanaf de website: <a href="<%= sPageURL%><mm:url page="/editors/evenementen"/><%=sPageURI %>">klik hier</a>
            <br/><br/>
            
            <% if ((extraText != null) && (!"".equals(extraText))) { %>
            Extra opmerkingen:<br/>
            <%=extraText%>
            <br/><br/>
            <% } %>

				<mm:include page="<%= sPageURI2 %>" />
			</multipart>
		</mm:setfield>
	</mm:createnode>
	<%
	
	String emailAdresses = toAddress.trim() + ";"; 
	int semicolon = emailAdresses.indexOf(";");
	while(semicolon>-1) { 
		String emailAdress = emailAdresses.substring(0,semicolon);
		emailAdresses = emailAdresses.substring(semicolon+1);
		semicolon = emailAdresses.indexOf(";");
		%><mm:node referid="mail"
			><mm:setfield name="to"><%= emailAdress %></mm:setfield
			><mm:field name="mail(oneshot)" 
		/></mm:node><%
	}
	
} %>
<html>
<head>
   <title>Verstuur aanmeldingen per email</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
   <div align="right"><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>
   <h4>Verstuur aanmeldingen per email</h4>
   <% if(!toAddress.equals("-1")) { %>
      De lijst met aanmeldingen is verstuurd naar: <%= toAddress %>
   <% } else { %>
      <table class="formcontent">
      <form action="">
      <input type="hidden" name="event" value="<%= nodenr %>" />
      <tr>
         <td colspan="2">
            Vul hier het email adres in van degene aan wie u de lijst met aanmeldingen voor de activiteit 
            <b>"<mm:field name="titel" />", <%= ddn.getReadableDate() %>, <%= ddn.getReadableTime() %></b> wilt versturen en klik op de verzend knop.
            <br/><br/>
            Meerdere email adressen gescheiden door puntkomma's (&nbsp;;&nbsp;)
            <br/><br/>
         </td>
      </tr>
      <tr>
         <td class="fieldname">Email adres:</td>
         <td><input type="text" name="emailto" value="" style="width:200px;" /><br/><br/></td>
      </tr>
      <tr>
         <td class="fieldname">Extra tekst:</td>
         <td><textarea cols="60" rows="10" name="extratekst" value=""></textarea><br/><br/></td>
      </tr>
      <tr>
         <td colspan="2"><input type="submit" value="verzend" style="width:100px;text-align:center;" /></td>
      </tr>
      </form>
      </table>
   <% } %>
</body>
</html>
</mm:node>
</mm:cloud>
