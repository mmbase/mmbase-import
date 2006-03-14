<%@page import="nl.leocms.evenementen.forms.SubscribeAction,java.util.*" %>
<%@include file="/taglibs.jsp" %>
<%@include file="calendar.jsp"  %>
<mm:import externid="e" jspvar="nodenr" id="nodenr">-1</mm:import>
<mm:import externid="p" jspvar="parent_number" id="parent_number">-1</mm:import>
<mm:import externid="s" jspvar="snumber" id="snumber">-1</mm:import>
<mm:import externid="d" jspvar="dnumber" id="dnumber">-1</mm:import>

<mm:cloud method="http" jspvar="cloud" rank="basic user">
<mm:node number="$nodenr" jspvar="thisEvent">
<mm:node number="$parent_number" jspvar="thisParent">
<mm:node number="$snumber" jspvar="thisSubscription">
<mm:node number="$dnumber" jspvar="thisParticipant">

<html>
<head>
<title>Bevestingingsbrief Aanmelding</title>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<style rel="stylesheet" type="text/css">
	body, td {
	   color:#000000;
      background-color: #FFFFFF;
      font: 14px;
	}
   table {
      margin-left: -3px;
   }
</style>
</head>
<body style="overflow:auto;" onload="self.print();">
<% 
cal.setTime(new Date());
%>
<% for(int i = 0; i<8; i++) { %><br/><% } %>
's Graveland, <%= cal.get(Calendar.DAY_OF_MONTH) + " " + months_lcase[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR) %>
<% for(int i = 0; i<4; i++) { %><br/><% } %>
<%= thisParticipant.getStringValue("prefix") %><br/>
<%= thisParticipant.getStringValue("straatnaam") %> <%= thisParticipant.getStringValue("huisnummer") %><br/>
<%= thisParticipant.getStringValue("postcode") %> <%= thisParticipant.getStringValue("plaatsnaam") %><br/>
<%= thisParticipant.getStringValue("land") %><br/><br/>

<%= SubscribeAction.getMessage(thisEvent,thisParent,thisSubscription,thisParticipant,"", "html") %>
</body>
</html>
</mm:node>
</mm:node>
</mm:node>
</mm:node>
</mm:cloud>
