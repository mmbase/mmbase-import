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
   Adding isvisible field to rubriek:<br/>
   1. Add isvisible field to rubriek table by using the following sql-statement<br/>
   ALTER TABLE `v1_rubriek` ADD `isvisible` VARCHAR( 255 );
   2. Migrate rubriek.url to rubriek.isvisible.<br/>
   <mm:listnodes type="rubriek">
    <mm:setfield name="isvisible"><mm:field name="url" /></mm:setfield>
   </mm:listnodes>
   3. Set rubriek.url for the root rubrieken in Natuurmonumenten.<br/>
   <mm:node number="492">
     <mm:setfield name="url">www.natuurmonumenten.nl</mm:setfield>
   </mm:node>
   <mm:node number="84159">
     <mm:setfield name="url">www.naardermeer.com</mm:setfield>
   </mm:node>
	 <mm:node number="360367">
     <mm:setfield name="url">www.hierwordtnietgebouwd.nl</mm:setfield>
   </mm:node>
	 <mm:node number="690821">
     <mm:setfield name="url">www.natuurherstel.nl</mm:setfield>
   </mm:node>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
