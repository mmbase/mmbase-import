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
   Things to be uploaded:<br/>
   1.editors<br/>
   2.dev<br/>
   3.classes (including resource bundle)<br/>
   4.inschrijvingen builder<br/>
   5.editwizards including htmlarea.js<br/>
   6.crontab.xml<br/>
   7.new version of data files<br/><br/>
   To be carried out:<br/>
   /editors/util/setimagetemplate.jsp<br/>
   /editors/util/unusedemployees.jsp<br/><br/>
   The following SQL statements have to be carried out:<br/>
   ALTER TABLE `v1_inschrijvingen` ADD `ticket_office` VARCHAR( 255 );<br/>
   UPDATE v1_inschrijvingen SET ticket_office="backoffice";<br/>
   Configuration settings:<br/>
   EventNotifier.liveUrl<br/>
   CVSReader: root, incoming and temp<br/>
   </body>
</html>
</mm:cloud>