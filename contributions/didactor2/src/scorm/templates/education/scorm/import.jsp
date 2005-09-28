<%@page import="org.jdom.Element"%>
<%@page import="org.jdom.Namespace"%>

<%@page import="nl.didactor.component.scorm.metastandart.schema.Importer"%>
<%@page import="nl.didactor.component.scorm.metastandart.schema.Filter"%>


<%@page import="uk.ac.reload.moonunit.contentpackaging.SCORM12_Core"%>
<%@page import="uk.ac.reload.moonunit.contentpackaging.CP_Core"%>
<%@page import="uk.ac.reload.scormplayer.client.generic.contentpackaging.SCORM12_DocumentHandler"%>
<%@page import="uk.ac.reload.scormplayer.client.generic.contentpackaging.ScormPackageHandler"%>



<%
   fileStoreDir = new File(CommonUtils.fixPath(directory + File.separator + requestImportPackageID));
   fileTempDir  = new File(CommonUtils.fixPath(directory + File.separator + requestImportPackageID + "_"));
   fileTempDir.mkdirs();

   String[] arrstrFiles = fileStoreDir.list();
   sFileName = arrstrFiles[0];

/*
   try
   {// This is a check for internal server error during unpacking .zip because of
    // the archive already has been tested during upload
    // and it seems the archive should be ok
      Unpack.unzipFileToFolder(CommonUtils.fixPath(fileStoreDir.getAbsolutePath() + File.separator + sFileName), CommonUtils.fixPath(fileTempDir.getAbsolutePath()));
   }
   catch(Exception e)
   {
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
   }
*/


   try
   {//Importing the package
      File fileManifest = new File(CommonUtils.fixPath(fileTempDir.getAbsolutePath()  + File.separator + CP_Core.MANIFEST_NAME));
      XMLDocument xmlDocument = new XMLDocument();
      xmlDocument.loadDocument(fileManifest);

      ScormPackageHandler test = new ScormPackageHandler(fileManifest, requestImportPackageID);
      test.buildSettings();



      nodePackage = cloud.getNode(requestImportPackageID);
      nodePackage.setValue("importdate", "" + ((new Date()).getTime() / 1000));
      nodePackage.commit();


      msg = "Import successful";

   }
   catch(Exception e)
   {
      msg = "An Error during import: <br/>" + e.toString();
   }



   try
   {//removing all temporal files
//      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
//      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), true);
   }
   catch(Exception e)
   {//internal server error
   }

%>
