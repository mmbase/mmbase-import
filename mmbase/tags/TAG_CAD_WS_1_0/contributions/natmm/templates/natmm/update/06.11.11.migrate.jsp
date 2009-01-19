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
	1. Preparing database exported from WS to be imported in NatMM.<br/>
	Processing...<br/>
	<% 
  try {
    (new nl.mmatch.util.migrate.WebShopToNatMMigrator()).run(); 
  } catch (Exception e) {
    %><%= e %><%
  } %>
   Done.
   </body>
</html>
</mm:cloud>
