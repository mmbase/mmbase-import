<%@page import="java.io.*,java.util.*,org.mmbase.bridge.*" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
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
   1. The number of route is moved from artikel.titel to artikel.status<br/>
   <mm:list nodes="routes" path="pagina,contentrel,provincies,pos4rel,natuurgebieden,rolerel,artikel"  orderby="artikel.titel" fields="artikel.number">
      <mm:node element="artikel">
         <mm:field name="titel" jspvar="title" vartype="String" write="false">
            <% int dPos = title.indexOf(".");
               if(dPos>-1) {
                  String sTNumber = title.substring(0,dPos).trim();
                  String sTName = title.substring(dPos+1).trim();
                  %>
                  <mm:setfield name="titel"><%= sTName %></mm:setfield>
                  <mm:setfield name="status"><%= sTNumber %></mm:setfield>
                  <%
                } %>
         </mm:field>
      </mm:node>
   </mm:list>
	Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
