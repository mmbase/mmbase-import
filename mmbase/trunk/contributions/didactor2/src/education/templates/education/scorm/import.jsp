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
   {// Internal server error during unpacking .zip because of
    // the archive already has been tested during upload
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

      System.out.println();
      System.out.println();
      System.out.println();
      ScormPackageHandler test = new ScormPackageHandler(fileManifest, requestImportPackageID);
      test.buildSettings();


       nodePackage = cloud.getNode(requestImportPackageID);
       nodePackage.setValue("importdate", "" + ((new Date()).getTime() / 1000));
       nodePackage.commit();


/*
      Element manifestRoot = (Element) xmlDocument.getRootElement().clone();

      // now get the organizations node
      Element orgs = manifestRoot.getChild(SCORM12_Core.ORGANIZATIONS, manifestRoot.getNamespace());

      SCORM12_Core _scormCore = new SCORM12_Core(xmlDocument);

      // get the identifier for the default organization
      Element defaultOrgNode = _scormCore.getDefaultOrganization(orgs);

      if (defaultOrgNode != null)
      {
         //Creating of education node
         Node nodeEducation = cloud.getNodeManager("educations").createNode();
         nodeEducation.setValue("name", defaultOrgNode.getChild("title", SCORM12_DocumentHandler.IMSCP_NAMESPACE_112).getText());
         nodeEducation.commit();

         //Relation to package node
         nodePackage = cloud.getNode(requestImportPackageID);
         nodePackage.createRelation(nodeEducation, cloud.getRelationManager("related")).commit();

         // and store the default identifier
         // set the default organization
         String defaultOrgIdentifier = defaultOrgNode.getAttributeValue(SCORM12_Core.IDENTIFIER);

         ScormPackageHandler test = new ScormPackageHandler(fileManifest);
         test.buildSettings();

      }
      else
      {
         //Exception: no default organization

      }
*/
   }
   catch(Exception e)
   {
   }

%>
