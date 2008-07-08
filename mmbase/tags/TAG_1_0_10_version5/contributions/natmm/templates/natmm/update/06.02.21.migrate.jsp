<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="java.util.*" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
   Changes made in this update:<br/>
   1. CAD2Excel<br/>
   2. ALTER TABLE `v1_inschrijvingen` CHANGE `description` `description` BLOB;<br/>
   3. set pos in evenement,posrel,bevestigings_teksten to support new functionality (costs);<br/>
   <mm:list path="evenement,posrel,bevestigings_teksten">
      <mm:node element="posrel">
         <mm:setfield name="pos">-1</mm:setfield>
      </mm:node>
   </mm:list>
   3. Support for group excursions<br/>
   <mm:node number="32996" id="sailing" />
   <mm:node number="65874" id="ledenservice" />
   <mm:node number="35841" id="natuurgebied" />
   <mm:node number="321317" id="bevestigings_tekst" />
   <mm:node number="38278" id="vertrekpunt" />
   <mm:node number="67953" id="kids_member" />
   <mm:node number="67954" id="kids_nonmember" />
   <mm:node number="589" id="nonmember" />
   <mm:node number="587" id="member" />
   <mm:node number="585" id="disabled_kids_nonmember" />
   <mm:node number="583" id="disabled_kids_member" />
   <mm:node number="581" id="disabled_member" />
   <mm:node number="321316" id="group_excursion">
      <mm:createalias>group_excursion</mm:createalias>
   </mm:node>
   <mm:node number="70594">
      <mm:createalias>booked</mm:createalias>
   </mm:node>
   <%
   Calendar cal = Calendar.getInstance();
   cal.set(2006,05,01,9,30,0); // 1 june, 9.30h
   long untillTime = cal.getTime().getTime()/1000;
   long oneDay = 24*60*60;
   String [] events = { "De Ark-Groepsexcursies Naardermeer", "De Tol-Groepsexcursies Naardermeer" };
   for(int e=0; e<events.length; e++) {
      cal.set(2006,03,01,9,30,0); // 1 april, 9.30h
      long startTime = cal.getTime().getTime()/1000;
      cal.set(2006,03,01,12,30,0); // 1 april, 12.30h
      long endTime = cal.getTime().getTime()/1000;
      %>
      <mm:remove referid="event_parent" />
      <%
      while(startTime<untillTime) {
         %><%= new Date(startTime*1000) %>-<%= new Date(endTime*1000) %><br/>
         <mm:remove referid="event_child" />
         <mm:createnode type="evenement" id="event_child">
            <mm:setfield name="titel"><%= events[e] %></mm:setfield>
            <mm:setfield name="begindatum"><%= startTime %></mm:setfield>
            <mm:setfield name="einddatum"><%= endTime %></mm:setfield>
         </mm:createnode>
         <mm:present referid="event_parent">
             <mm:node number="$event_child">
               <mm:setfield name="soort">child</mm:setfield>
             </mm:node>
             <mm:createrelation source="event_parent" destination="event_child" role="partrel" />
         </mm:present>
         <mm:notpresent referid="event_parent">
             <mm:node number="$event_child" id="event_parent">
               <mm:setfield name="soort">parent</mm:setfield>
               <mm:setfield name="omschrijving"><P>(DIT IS NOG EEN TEST INVOERING&lt; AUB NIET AANMELDEN!!!!!)</P><P>Jeanine van der Velden</P></mm:setfield>
               <mm:setfield name="max_aantal_deelnemers">25</mm:setfield>
             </mm:node>
             <mm:createrelation source="event_parent" destination="natuurgebied" role="related" />
             <mm:createrelation source="event_parent" destination="bevestigings_tekst" role="posrel"><mm:setfield name="pos">2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="sailing" role="related" />
             <mm:createrelation source="event_parent" destination="vertrekpunt" role="posrel" />
             <mm:createrelation source="event_parent" destination="group_excursion" role="posrel"><mm:setfield name="pos">18000</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="kids_member" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="kids_nonmember" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="nonmember" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="member" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="disabled_kids_nonmember" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="disabled_kids_member" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="disabled_member" role="posrel"><mm:setfield name="pos">-2</mm:setfield></mm:createrelation>
             <mm:createrelation source="event_parent" destination="ledenservice" role="readmore"><mm:setfield name="readmore">2</mm:setfield></mm:createrelation>
         </mm:notpresent>
         <%
         startTime += oneDay;
         endTime += oneDay;
      } 
   } 
   %>
   Done.
   </body>
</html>
</mm:cloud>
