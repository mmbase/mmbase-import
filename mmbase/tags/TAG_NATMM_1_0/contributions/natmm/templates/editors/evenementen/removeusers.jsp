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
    <mm:list nodes="" path="users,schrijver,deelnemers">
      <mm:deletenode element="schrijver" />
    </mm:list>
    <mm:list nodes="" path="users,schrijver,inschrijvingen">
      <mm:deletenode element="schrijver" />
    </mm:list>
    <mm:list nodes="" path="users,schrijver,evenement" constraints="soort='child'">
      <mm:deletenode element="schrijver" />
    </mm:list>
   </body>
</html>
</mm:cloud>