<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:import externid="r" jspvar="repairId" id="repairId" /><% if(repairId==null) { repairId = ""; } %>
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
      <mm:list path="evenement" constraints="evenement.omschrijving=''" fields="evenement.number"
         ><mm:node element="evenement"
            ><mm:hasrelations inverse="true">
               <mm:field name="number" /><br/>
               <% if(repairId.equals("true")) { %><mm:deletenode /><% } %>
            </mm:hasrelations
         ></mm:node
      ></mm:list>
   </body>
</html>
</mm:cloud>