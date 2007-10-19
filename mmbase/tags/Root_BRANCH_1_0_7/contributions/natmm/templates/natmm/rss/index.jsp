<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="java.util.Date,nl.leocms.util.tools.HtmlCleaner" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten RSS Feeds</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
    <a href="xmlnatuurgebied.jsp">natuurgebieden</a><br/>
    <a href="xmlroutes.jsp">routes</a><br/>
    <a href="xmlactiviteiten.jsp">activiteiten</a><br/>
   </body>
</html>
</mm:cloud>