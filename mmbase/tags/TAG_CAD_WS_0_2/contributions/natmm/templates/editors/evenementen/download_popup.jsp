<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*,nl.leocms.util.DoubleDateNode" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" method="http" rank="basic user">
<mm:import externid="event" jspvar="nodenr">-1</mm:import>
<mm:import externid="type" jspvar="type">-1</mm:import>
<html>
<head>
   <title>Download Excel bestand</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
   <div align="right"><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>
	<jsp:useBean id="ExcelWriter" scope="session" class="nl.leocms.evenementen.stats.ExcelWriter" />
	<% String sType = "";
		String attachmentId = "";
      String sMessage = "";
      int MAX_EVENT = 25;
		if (type.equals("d")){ 
			sType = "deze activiteit";
			attachmentId = ExcelWriter.createEventDatesAttachment(cloud,nodenr); 
		} else if (type.equals("s")){
			sType = "de aanmeldingen voor deze activiteit";
			attachmentId = ExcelWriter.createEventSubscribeAttachment(cloud,nodenr); 
		} else if (type.equals("ad")) {
			sType = "de geselecteerde activiteiten";
			String sCSList = (String) session.getAttribute("sCSList");
         if(sCSList!=null) {
            int cCount = 0;
            int cPos = sCSList.indexOf(",");
            while(cPos!=-1) {
               cCount++;
               cPos = sCSList.indexOf(",",cPos+1);
            }
            if(cCount<=MAX_EVENT) { 
               attachmentId = ExcelWriter.createAllEventDatesAttachment(cloud,sCSList); 
            } else {
               sMessage = "U kunt alleen een excel bestand van alle geselecteerde activiteiten downloaden als u ten hoogste " + MAX_EVENT + " activiteiten heeft geselecteerd.";
            }
         } else {
            sMessage = "De geselectecteerde activiteiten kunnen niet gedownload worden. Neem alstublieft contact op met de webmasters.";
         }
		} else if (type.equals("as")) {
			sType = "alle aanmeldingen voor deze activiteit";
			attachmentId = ExcelWriter.createAllEventSubsribeAttachment(cloud,nodenr);
		}%>
   <h4>Download <%= sType %></h4>
   <% if(!sMessage.equals("")) { %>
      <span style="color:red"><%= sMessage %></span>
   <% } %>
	<mm:node number="<%= attachmentId %>" notfound="skipbody">
		<a href="<mm:attachment />">Klik hier om het excel bestand met <%= sType %> te downloaden</a>
	</mm:node>
</body>
</html>
</mm:cloud>
