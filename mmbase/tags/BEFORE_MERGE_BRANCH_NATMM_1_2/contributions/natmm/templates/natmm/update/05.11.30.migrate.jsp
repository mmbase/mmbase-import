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
   1. added attachments to vacatures and changed order of fields in template and editwizard<br/>
   2. corrected path to the includes with the maps for natuurgebieden (includes/natuurgebieden/related.jsp)<br/>
   3. corrected id for shorty.jsp (and therefore also shorty_home.jsp and teaser.jsp), the paginaID can not be changed in chain of includes<br/>
   4. put the link to findmemberid.jsp popup back into the subscribe.jsp form<br/>
   Done.
   </body>
</html>
</mm:cloud>
