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
   Running script preparing data imported from NMIntra to be exported to NatMM<br/>
   This script converts the XML exported with LiveMMBase to a version ready to be imported in NatMM<br/>
   The files are read from and written to: NMIntraConfig.rootDir + "NMIntraXML/"<br/>
   Typical usage looks like this:<br/>
   1. Export the XML with LiveMMBase on the original installation.<br/>
   2. Place the XML files and run this script to migrate the XML-files (see the mmbase.log for output of the script)<br/>
   3. For admin set role to administrator and owner to system (or after importing:  update v1_users set owner='system' where account='admin';)<br/>
   4. Move the change XML files to the NMIntra application directory<br/>
   5. Start NatMM with an empty db. Don't load the data from LeoCMS<br/>
   6. Load the NMIntra application from (a) the admin interface or (b) by setting auto-deploy to true and restarting the application server.<br/>
	   Some notes:<br/>
      -set the max_allowed_packet large enough<br/>
      -running the import might take 1+ hour<br/>
      -check the mmbase.log and wait for the logmessage "Application 'NMIntra' deployed succesfully"<br/>
      Some todo's:
      -email.xml is not necessary<br/>
      -email address of medewerkers EmmerigM and BSwarts are to long (search on XS400)<br/>
   7. Add a rubriek with alias root and relate the Intranet and Ontwikkel rubrieken to it<br/>
   8. Add users.admin alias to admin (switch of security temporarily for this), set expiredate. 
   9. Link admin to root (rolerel.rol=3). Remove the other two rubrieken from admin.<br/>
   10. Run the other update scripts.
   11. Set new passwords for all users.<br/>
	Note: RelationsMigrator will call NMIntraToNatMM migrator<br/>
	<% (new nl.mmatch.util.migrate.RelationsMigrator()).run(); %>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
