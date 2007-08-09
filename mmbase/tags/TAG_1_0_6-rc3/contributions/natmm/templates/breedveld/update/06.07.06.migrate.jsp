<%@include file="/taglibs.jsp" %>
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
   Running script preparing data imported from Breedveld to be exported to NatMM<br/>
   This script converts the XML exported with LiveMMBase to a version ready to be imported in NatMM<br/>
   The files are read from and written to: NMIntraConfig.rootDir + "BreedveldXML/"<br/>
   Typical usage looks like this:<br/>
   1. Export the XML with LiveMMBase on the original installation.<br/>
   2. Place the XML files and run this script to migrate the XML-files (see the mmbase.log for output of the script)<br/>
   3. Move the changed XML files to the Breedveld application directory<br/>
   4. Start NatMM with an empty db. Load the LeoCMS application, but don't load the data from LeoCMS<br/>
   5. Load the Breedveld application by setting auto-deploy to true and restarting the application server.<br/>
	   Some notes:<br/>
      -set the max_allowed_packet large enough<br/>
      -running the import might take 1+ hour<br/>
      -check the mmbase.log and wait for the logmessage "Application 'Breedveld' deployed succesfully"<br/>
      Some todo's:
      -email.xml is not necessary<br/>
      -medewerkers.xml email address of EmmerigM and BSwarts are to long (search on X400)<br/>
   6. Set new passwords for all users, and set their rights<br/>
	<% (new nl.mmatch.util.migrate.BreedveldToNatMMigrator()).run(); %>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
