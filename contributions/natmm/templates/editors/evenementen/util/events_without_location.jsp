<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="nl.leocms.util.tools.HtmlCleaner"%>
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
      <h3>Activiteiten zonder provincie</h3>
      <mm:listnodes type="evenement" constraints="lokatie = ',-1,'">
            <mm:field name="number" />-<mm:field name="soort" /><br/>
      </mm:listnodes>
   </body>
</html>
</mm:cloud>