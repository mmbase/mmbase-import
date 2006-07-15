<%@page import="nl.leocms.evenementen.Evenement,org.mmbase.bridge.*" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten Activiteiten Database</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
      <!-- first step correct the deelnemers categorie -->
      <% Evenement event = new Evenement(); %>
      <mm:list path="evenement,posrel,deelnemers_categorie" constraints="soort='parent' AND deelnemers_categorie.groepsactiviteit='1'">
         <mm:field name="deelnemers_categorie.number" jspvar="dce" vartype="String" write="false">
         <mm:remove referid="dce" />
         <mm:node element="deelnemers_categorie" id="dce" />
         <mm:node element="evenement" jspvar="pe">
         <%
         NodeList el = event.getSortedList(cloud,pe.getStringValue("number"));
         for(int i=0; i<el.size(); i++) {
            String sEvent = el.getNode(i).getStringValue("evenement.number");
            %>
            <mm:list nodes="<%= sEvent %>" path="evenement,posrel,inschrijvingen,posrel,deelnemers,related,deelnemers_categorie"
                  constraints="deelnemers_categorie.groepsactiviteit='1'">
               <mm:field name="related.number" jspvar="rel" vartype="String" write="false">
               <mm:remove referid="participant" />
               <mm:node element="deelnemers" id="participant" />
               <mm:remove referid="dci" />
               <mm:node element="deelnemers_categorie" jspvar="dci">
               <% if(dce!=dci.getStringValue("number")) { %>
                  <mm:deletenode number="<%= rel %>" />
                  <mm:createrelation source="participant" destination="dce" role="related" />
               <% } %>
               </mm:node>
               </mm:field>
            </mm:list>
            <%
         }
         %>
         </mm:node>
         </mm:field>
      </mm:list>
      <mm:list path="evenement,posrel,inschrijvingen,posrel,deelnemers,related,deelnemers_categorie" constraints="deelnemers_categorie.groepsactiviteit='1'">
         <mm:field name="deelnemers_categorie.aantal_per_deelnemer" id="apd" />
         <mm:node element="deelnemers">
            Updated deelnemer: <mm:field name="number" />-<mm:field name="titel" /><br/>
            <mm:setfield name="bron">1</mm:setfield>
         </mm:node>
         <mm:node element="evenement">
            Updated: <mm:field name="number" />-<mm:field name="titel" /><br/>
            <mm:setfield name="embargo">0</mm:setfield>
         </mm:node>
      </mm:list>
   </body>
</html>
</mm:cloud>