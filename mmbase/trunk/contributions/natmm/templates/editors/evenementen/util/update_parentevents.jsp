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
      <mm:listnodes type="evenement" constraints="soort='parent' AND number='210048'">
        <%
        String embargo = null;
        String verloopdatum = null;
        %>
        <mm:node jspvar="thisEvent">
          <%
          embargo = thisEvent.getStringValue("embargo");
          verloopdatum = thisEvent.getStringValue("verloopdatum");
          %>
          <mm:setfield name="embargo">0</mm:setfield>
        </mm:node>
        <mm:field name="embargo">
            <mm:compare value="<%= embargo %>" inverse="true"><%= embargo %> -- <mm:write />
              embargo for: <mm:field name="number" />-<mm:field name="titel" /> was changed<br/>
            </mm:compare>
        </mm:field>
        <mm:field name="verloopdatum">
          <mm:compare value="<%= verloopdatum %>" inverse="true">
            verloopdatum for: <mm:field name="number" />-<mm:field name="titel" /> was changed<br/>
          </mm:compare>
        </mm:field>
      </mm:listnodes>
   </body>
</html>
</mm:cloud>