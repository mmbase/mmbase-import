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
   1. added search on department to /editors/evenementen/evenementen.jsp<br/>
   <a href="/editors/evenementen/util/natuurgebieden_without_department.jsp">update natuurgebieden.titel_eng field</a><br/>
   2. added restriction to department to /editors/evenementen/evenementen.jsp (afdelingen-rolerel-users)<br/>
   3. added new from list<br/>
   4. issues: 319, 320, 321, 322<br/>
   Done.
   </body>
</html>
</mm:cloud>
