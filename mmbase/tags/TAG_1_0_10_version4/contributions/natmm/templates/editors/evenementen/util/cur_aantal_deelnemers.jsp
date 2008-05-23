<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:import externid="r" jspvar="repairId" id="repairId" /><% if(repairId==null) { repairId = ""; } %>
<mm:import externid="e" jspvar="eventId" id="eventId" /><% if(eventId==null) { eventId = ""; } %>
<%
boolean showdetails = false;
if(!eventId.equals("")) {
   showdetails = true;
} 
%>
<html>
   <head>
   <link rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten Activiteiten Database</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
   <table>
      <tr><td>e=...</td><td>show details on event e<td></tr>
      <tr><td>r=true</td><td>repair inconsistencies on evenement.cur_aantal_deelnemers and deelnemers.bron<td></tr>
   </table>
   <b>Checking deelnemers with incorrect bron</b><br/>
   <mm:listnodes type="deelnemers" constraints="bron <= 0">
       deelnemers=<mm:field name="number" />, <mm:field name="bron" /><br/>
       <% if(repairId.equals("true")) { %><mm:setfield name="bron">1</mm:setfield><% } %>
   </mm:listnodes>
   <b>Checking evenementen with incorrect cur_aantal_deelnemers</b><br/>
   <% 
   String eventsConstraint = "cur_aantal_deelnemers!='0'";
   if(!eventId.equals("")) { eventsConstraint = "number='" + eventId + "'"; }
   %>
   <mm:listnodes type="evenement" constraints="<%= eventsConstraint %>">
      <% int iCurPart = 0; %>
      <mm:relatednodes type="inschrijvingen">
         <% if(showdetails) { %>inschrijvingen=<mm:field name="number" /><br/><% } %>
         <mm:relatednodes type="deelnemers" jspvar="thisPar">
            <% if(showdetails) { %>deelnemers=<mm:field name="number" />, <mm:field name="bron" /><br/><% } %>
            <% iCurPart += thisPar.getIntValue("bron"); %>
         </mm:relatednodes>
      </mm:relatednodes>
      <mm:field name="cur_aantal_deelnemers" jspvar="curSet" vartype="Integer" write="false">
      <% if(showdetails || iCurPart!=curSet.intValue()) { %>
         node=<mm:field name="number"/>, cur_aantal=<%= iCurPart %>, cur_set=<%= curSet %><br/>
        <% if(repairId.equals("true")) { %><mm:setfield name="cur_aantal_deelnemers"><%= "" + iCurPart %></mm:setfield><% } %>
      <% } %>
      </mm:field>
   </mm:listnodes>
   </body>
</html>
</mm:cloud>