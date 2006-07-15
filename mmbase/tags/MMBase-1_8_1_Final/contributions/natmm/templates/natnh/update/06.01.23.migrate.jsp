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
	1. Preparing database exported from MicroSite to be imported in NatMM.<br/>
	Processing...<br/>
	<% (new nl.mmatch.util.migrate.NatNHToNatMMigrator()).run(); %>
   Done
	2. Start tomcat with an empty db and load the data from LeoCMS<br/>
   3. Move the changed XML files to the applications/NatNH application directory<br/>
   4. Load the NatNH application by setting auto-deploy to true and restarting the application server.<br/>
	   Some notes:<br/>
      -set the max_allowed_packet large enough<br/>
      -running the import might take 1+ hour<br/>
      -check the mmbase.log and wait for the logmessage "Application 'NatNH' deployed succesfully" or see below ...<br/>
	5. Run the other migration script<br/>
   ------
		Problem installing application : NatNH.xml, cause: 
		Cannot sync relation (exportnumber==10239, snumber:1208, dnumber:-1)
		Cannot sync relation (exportnumber==9159, snumber:1684, dnumber:-1)
		Cannot sync relation (exportnumber==6735, snumber:1764, dnumber:-1)
		Cannot sync relation (exportnumber==6741, snumber:1686, dnumber:-1)
		Cannot sync relation (exportnumber==4895, snumber:1878, dnumber:-1)
		Cannot sync relation (exportnumber==12903, snumber:1718, dnumber:-1)
		Cannot sync relation (exportnumber==9859, snumber:1682, dnumber:-1)
		Cannot sync relation (exportnumber==6393, snumber:1566, dnumber:-1)
		Cannot sync relation (exportnumber==11563, snumber:1764, dnumber:-1)
		Cannot sync relation (exportnumber==11365, snumber:1718, dnumber:-1)
		Cannot sync relation (exportnumber==6873, snumber:1516, dnumber:-1)
		Cannot sync relation (exportnumber==8057, snumber:1566, dnumber:-1)
   </body>
</html>
</mm:cloud>
