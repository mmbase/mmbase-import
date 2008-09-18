<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="org.mmbase.bridge.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">

<mm:import externid="i">-1</mm:import>
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
   <mm:list nodes="86035" path="inschrijvings_status,related,inschrijvingen" orderby="inschrijvingen.datum_inschrijving" directions="DOWN">
   <mm:first>
   <h3>Overzicht van niet bevestigde aanmeldingen</h3>
   <mm:compare referid="i" value="-1" inverse="true">
   <mm:node number="$i" jspvar="thisSubscription">
   <%
   RelationList relations = thisSubscription.getRelations("related",cloud.getNodeManager("inschrijvings_status"), "destination");
   if(!relations.isEmpty()) {
      Relation  thisRelation = relations.getRelation(0);
      thisRelation.delete();
      Node thisStatus = cloud.getNode("confirmed");
      if(thisStatus!=null) {
         thisSubscription.createRelation(thisStatus,cloud.getRelationManager("related")).commit();
         %><div style="width:100%;text-align:center;color:red;">Status verandert in bevestigd.</div><%
      }
   }
   %>
   </mm:node>
   </mm:compare>
   <table>
      <tr>
         <td style="vertical-align:top;">Naam</td>
         <td style="vertical-align:top;">Email</td>
         <td style="vertical-align:top;">Telefoonnummer</td>
         <td style="vertical-align:top;">Evenement</td>
         <td style="vertical-align:top;">Datum</td>
         <td style="vertical-align:top;">Deelnemers</td>
         <td style="vertical-align:top;">Aangemeld via</td>
         <td style="vertical-align:top;padding-right:10px;">Aanmeld datum</td>
         <td style="vertical-align:top;padding-right:10px;">Bevestig</td>
      </tr>
   </mm:first>
   <mm:node element="inschrijvingen" id="inschrijving">
      <mm:import id="deelnemers" reset="true"
         ><mm:relatednodes type="deelnemers"
               ><mm:first inverse="true">, </mm:first
               ><mm:field name="bron" 
               /> <mm:related path="related,deelnemers_categorie"><mm:field name="deelnemers_categorie.naam" /></mm:related
         ></mm:relatednodes
      ></mm:import>
      <mm:relatednodes type="deelnemers" max="1">
      <tr>
         <td style="vertical-align:top;"><mm:field name="titel" /></td>
         <td style="vertical-align:top;"><mm:field name="email" /></td>
         <td style="vertical-align:top;"><nobr><mm:field name="privatephone" /></nobr></td>
         <mm:related path="posrel,inschrijvingen,posrel,evenement">
            <td style="vertical-align:top;"><mm:field name="evenement.titel" /></td>
            <td style="vertical-align:top;"><mm:field name="evenement.begindatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="dd-MM-yyyy hh:mm" /></mm:field></td>
         </mm:related>
         <td style="vertical-align:top;"><mm:write referid="deelnemers" /></td>
         <td style="vertical-align:top;"><mm:field node="inschrijving" name="ticket_office" /></td>
         <td style="vertical-align:top;padding-right:10px;"><mm:field name="creatiedatum" jspvar="cdate" vartype="String" write="false"><mm:time time="<%= cdate %>" format="dd-MM-yyyy hh:mm" /></mm:field></td>
         <td style="vertical-align:top;padding-right:10px;"><a href="not_confirmed_subscriptions.jsp?i=<mm:field node="inschrijving" name="number" />">bevestig</a></td>
      </tr>
      </mm:relatednodes>
   </mm:node>
   <mm:last>
   </table>
   </mm:last>
   </mm:list>
   </body>
</html>
</mm:cloud>