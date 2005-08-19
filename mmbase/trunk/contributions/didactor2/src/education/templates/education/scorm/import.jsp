<%@page import="org.jdom.Element"%>
<%@page import="org.jdom.Namespace"%>

<%@page import="uk.ac.reload.moonunit.contentpackaging.SCORM12_Core"%>
<%@page import="uk.ac.reload.moonunit.contentpackaging.CP_Core"%>
<%@page import="uk.ac.reload.scormplayer.client.generic.contentpackaging.SCORM12_DocumentHandler"%>
<%@page import="uk.ac.reload.scormplayer.client.generic.contentpackaging.ScormPackageHandler"%>



<%
   fileStoreDir = new File(Unpack.fixPath(directory + File.separator + requestImportPackageID));
   fileTempDir  = new File(Unpack.fixPath(directory + File.separator + requestImportPackageID + "_"));
   fileTempDir.mkdirs();

   String[] arrstrFiles = fileStoreDir.list();
   sFileName = arrstrFiles[0];

   try
   {// This is a check for internal server error during unpacking .zip because of
    // the archive already has been tested during upload
    // and it seems the archive should be ok
      Unpack.unzipFileToFolder(Unpack.fixPath(fileStoreDir.getAbsolutePath() + File.separator + sFileName), Unpack.fixPath(fileTempDir.getAbsolutePath()));
   }
   catch(Exception e)
   {
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
   }



   try
   {//Importing the package
      File fileManifest = new File(Unpack.fixPath(fileTempDir.getAbsolutePath()  + File.separator + CP_Core.MANIFEST_NAME));
      XMLDocument xmlDocument = new XMLDocument();
      xmlDocument.loadDocument(fileManifest);

      ScormPackageHandler test = new ScormPackageHandler(fileManifest, requestImportPackageID);
      test.buildSettings();

      nodePackage = cloud.getNode(requestImportPackageID);
      nodePackage.setValue("importdate", "" + ((new Date()).getTime() / 1000));
      nodePackage.commit();

   }
   catch(Exception e)
   {
   }



   try
   {//removing all temporal files
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), true);
   }
   catch(Exception e)
   {//internal server error
   }

%>
