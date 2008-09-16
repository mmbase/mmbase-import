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
   1. The artikel.sms is changed into contentelement.use_verloopdatum<br/>
   <mm:listnodes type="artikel" constraints="sms = '0'"
		><mm:setfield name="use_verloopdatum">0</mm:setfield
	></mm:listnodes>
	Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
