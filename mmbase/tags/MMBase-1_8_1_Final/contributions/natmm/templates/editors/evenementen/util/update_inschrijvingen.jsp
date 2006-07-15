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
      <mm:listnodes type="inschrijvingen">
         <mm:setfield name="users">,-1,</mm:setfield>
      </mm:listnodes>
      The related department field in inschrijvingen has been updated.<br/>
      <mm:listnodes type="inschrijvingen">
         <mm:field name="users"/><br/>
      </mm:listnodes>
   </body>
</html>
</mm:cloud>