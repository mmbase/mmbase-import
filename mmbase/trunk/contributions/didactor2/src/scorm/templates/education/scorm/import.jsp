<%@page import="org.jdom.Element"%>
<%@page import="org.jdom.Namespace"%>

<%@page import="nl.didactor.component.scorm.metastandart.schema.Importer"%>
<%@page import="nl.didactor.component.scorm.metastandart.schema.Filter"%>


<%@page import="uk.ac.reload.moonunit.contentpackaging.SCORM12_Core"%>
<%@page import="uk.ac.reload.moonunit.contentpackaging.CP_Core"%>
<%@page import="uk.ac.reload.scormplayer.client.generic.contentpackaging.SCORM12_DocumentHandler"%>
<%@page import="uk.ac.reload.scormplayer.client.generic.contentpackaging.ScormPackageHandler"%>


    <mm:log jspvar="log">
<%
   fileStoreDir = new File(directory, requestImportPackageID);
   fileTempDir  = new File(directory, requestImportPackageID + "_");
   fileTempDir.mkdirs();

   String[] arrstrFiles = fileStoreDir.list();
   sFileName = arrstrFiles[0];

/*
   try
   {// This is a check for internal server error during unpacking .zip because of
    // the archive already has been tested during upload
    // and it seems the archive should be ok
      Unpack.unzipFileToFolder(new File(fileStoreDir, sFileName), fileTempDir.getAbsolutePath());
   }
   catch(Exception e)
   {
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
   }
*/


   try {//Importing the package
       log.info("Importing");
       File fileManifest = new File(fileTempDir, CP_Core.MANIFEST_NAME);
       XMLDocument xmlDocument = new XMLDocument();
       xmlDocument.loadDocument(fileManifest);
       
       ScormPackageHandler test = new ScormPackageHandler(fileManifest, requestImportPackageID);
       test.buildSettings();
       


       packageNode = cloud.getNode(requestImportPackageID);
       packageNode.setValue("importdate", "" + ((new Date()).getTime() / 1000));
       packageNode.commit();
       
       
       msg = "Import successful";

   } catch(Exception e) {
      msg = "An Error during import: <br/>" + e.toString();
   }



   try {//removing all temporal files
       //  Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
       //      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), true);
   } catch(Exception e)     {//internal server error
   }

%>
       </mm:log>
