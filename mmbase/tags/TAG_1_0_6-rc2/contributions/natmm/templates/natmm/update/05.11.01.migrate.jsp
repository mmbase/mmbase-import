<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
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
   1. set empty inschrijvingen.betaalwijze to 'Contant'<br/>
   <mm:listnodes type="inschrijvingen">
      <mm:field name="betaalwijze">
         <mm:compare value="">
            <mm:setfield name="betaalwijze">Contant</mm:setfield>
         </mm:compare>
      </mm:field>
   </mm:listnodes>
   2. costs below zero and costs lower then 100<br/>
   <mm:list nodes="" path="inschrijvingen,posrel,deelnemers" constraints="posrel.pos < 100 AND posrel.pos != 0">
      <mm:node element="posrel">
      <mm:field name="pos" jspvar="costs" vartype="Integer" write="false">
         <%
         int iCosts = costs.intValue();
         %><%= iCosts %><%
         if(iCosts==-1) { iCosts = 0; }
         if(iCosts<0) { iCosts = -iCosts; }
         if(iCosts<100) { iCosts = 100*iCosts; }
         %> := <%= iCosts %><br/>
         <mm:setfield name="pos"><%= iCosts %></mm:setfield>
      </mm:field>
      </mm:node>
   </mm:list>
   3. default costs below zero and costs lower then 100<br/>
   <mm:list nodes="" path="evenement,posrel,deelnemers_categorie" constraints="posrel.pos < 100 AND posrel.pos != 0">
      <mm:node element="posrel">
      <mm:field name="pos" jspvar="costs" vartype="Integer" write="false">
         <%
         int iCosts = costs.intValue();
         %><%= iCosts %><%
         if(iCosts==-1) { iCosts = 0; }
         if(iCosts<0) { iCosts = -iCosts; }
         if(iCosts<100) { iCosts = 100*iCosts; }
         %> := <%= iCosts %><br/>
         <mm:setfield name="pos"><%= iCosts %></mm:setfield>   
      </mm:field>
      </mm:node>
   </mm:list>
   4. create a node cash payment
   <%--
   <mm:createnode type="payment_type" id="pt">
      <mm:setfield name="naam">Contant</mm:setfield>
   </mm:createnode>
   <mm:node number="$pt">
      <mm:createalias>cash_payment</mm:createalias>
   </mm:node>
   --%>
   Issues solved:<br/>
   1. ledenservice@natuurmonumenten.nl -> denatuurin@natuurmonumenten.nl (JvdV Fri 9/16/2005 9:22 AM)<br/>
   2. only digits in lidnumber are relevant (JvdV Fri 9/16/2005 2:59 PM)<br/>
   3. changes in confirmation emails (JvdV Fri 10/7/2005 3:50 PM)<br/>
   4. email for forms is changed to "sent email, but keep it in the database" for statistics<br/>
   Done.
   </body>
</html>
</mm:cloud>
