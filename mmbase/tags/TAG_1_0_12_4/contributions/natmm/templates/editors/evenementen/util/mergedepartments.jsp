<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:import externid="s" jspvar="sourceId" id="sourceId">-1</mm:import>
<mm:import externid="d" jspvar="destId" id="destId">-1</mm:import>
<%! static void copyField(Node source, Node destination, String field) {
      if(destination.getStringValue(field).equals("")) {
         destination.setStringValue(field,source.getStringValue(field));
      }
   }
%>
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
   <mm:node number="$sourceId" id="source" jspvar="source">
   <mm:node number="$destId" id="dest" jspvar="dest">
      <% copyField(source,dest,"naam");
         copyField(source,dest,"bron");
         copyField(source,dest,"type");
         copyField(source,dest,"titel_zichtbaar");
         copyField(source,dest,"telefoonnummer");
         copyField(source,dest,"faxnummer");
         copyField(source,dest,"metatags");
         copyField(source,dest,"email");
         copyField(source,dest,"bezoekadres");
         copyField(source,dest,"bezoekadres_postcode");
         copyField(source,dest,"land");
         copyField(source,dest,"postbus");
         copyField(source,dest,"postbus_postcode");
         copyField(source,dest,"plaatsnaam");
         copyField(source,dest,"status");
         copyField(source,dest,"externid");
         copyField(source,dest,"email");
         copyField(source,dest,"importstatus");
         dest.commit();
      %>
      <mm:list nodes="$sourceId" path="afdelingen,readmore,evenement">
            <mm:node element="evenement" id="dest_evenement"/>
            <mm:field name="readmore.readmore" jspvar="rm" vartype="String" write="false">
            <mm:field name="readmore.readmore2" jspvar="rm2" vartype="String" write="false">
            <mm:createrelation source="dest" destination="dest_evenement" role="readmore">
                     <mm:setfield name="readmore"><%= rm %></mm:setfield>
                     <mm:setfield name="readmore2"><%= rm2 %></mm:setfield>
            </mm:createrelation>
            </mm:field>
            </mm:field>
            <mm:deletenode element="readmore" />
      </mm:list>
      <mm:list nodes="$sourceId" path="afdelingen1,readmore,afdelingen2" searchdir="destination">
            <mm:node element="afdelingen2" id="dest_afd"/>
            <mm:field name="readmore.readmore" jspvar="rm" vartype="String" write="false">
            <mm:field name="readmore.readmore2" jspvar="rm2" vartype="String" write="false">
            <mm:createrelation source="dest" destination="dest_afd" role="readmore">
                     <mm:setfield name="readmore"><%= rm %></mm:setfield>
                     <mm:setfield name="readmore2"><%= rm2 %></mm:setfield>
            </mm:createrelation>
            </mm:field>
            </mm:field>
            <mm:deletenode element="readmore" />
      </mm:list>
      <mm:list nodes="$sourceId" path="afdelingen1,readmore,afdelingen2" searchdir="source">
            <mm:node element="afdelingen2" id="dest_afd"/>
            <mm:field name="readmore.readmore" jspvar="rm" vartype="String" write="false">
            <mm:field name="readmore.readmore2" jspvar="rm2" vartype="String" write="false">
            <mm:createrelation source="dest_afd" destination="dest" role="readmore">
                     <mm:setfield name="readmore"><%= rm %></mm:setfield>
                     <mm:setfield name="readmore2"><%= rm2 %></mm:setfield>
            </mm:createrelation>
            </mm:field>
            </mm:field>
            <mm:deletenode element="readmore" />
      </mm:list>
      <mm:list nodes="$sourceId" path="afdelingen,readmore,medewerkers">
            <mm:node element="medewerkers" id="dest_med"/>
            <mm:field name="readmore.readmore" jspvar="rm" vartype="String" write="false">
            <mm:field name="readmore.readmore2" jspvar="rm2" vartype="String" write="false">
            <mm:createrelation source="dest" destination="dest_med" role="readmore">
                     <mm:setfield name="readmore"><%= rm %></mm:setfield>
                     <mm:setfield name="readmore2"><%= rm2 %></mm:setfield>
            </mm:createrelation>
            </mm:field>
            </mm:field>
            <mm:deletenode element="readmore" />
      </mm:list>
   </mm:node>
   </mm:node>
   <mm:deletenode number="$sourceId" />
   </body>
</html>
</mm:cloud>