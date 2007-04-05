<%@page contentType="text/html; charset=UTF8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%>


<%@page import="java.io.File"%>
<%@page import="java.io.RandomAccessFile"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="org.apache.commons.fileupload.*"%>

<%@page import="org.mmbase.bridge.*"%>

<%@page import="nl.didactor.utils.zip.Unpack"%>
<%@page import="nl.didactor.utils.files.FileCopier"%>
<%@page import="nl.didactor.utils.files.CommonUtils"%>

<%@page import="nl.didactor.component.scorm.player.MenuCreator"%>

<%@page import="uk.ac.reload.jdom.XMLDocument"%>
<%@page import="uk.ac.reload.moonunit.contentpackaging.CP_Core"%>


<mm:cloud method="delegate" jspvar="cloud">


<mm:import from="request" externid="import_package"   vartype="String" jspvar="requestImportPackageID" />
<mm:import from="request" externid="delete_package"   vartype="String" jspvar="requestDeletePackageID" />
<mm:import from="request" externid="publish_package"  vartype="String" jspvar="requestPublishPackageID" />



<%
//   System.out.println("a=" + request.);

//String directory = getServletContext().getInitParameter("filemanagementBaseDirectory");
//  String baseUrl = getServletContext().getInitParameter("filemanagementBaseUrl");

    String directory = getServletContext().getRealPath("/education/files");
    String baseUrl = "http://localhost/education/files";

   if (directory == null || baseUrl == null) {
       throw new ServletException("Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
   }

   directory += "/scorm";


   SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
   Node nodePackage = null;
   File newDir = null;
   File newDir_ = null;
   File fileStoreDir = null;
   File fileTempDir  = null;
   File filePlayerDir  = null;
   String sFileName = null;
   String msg = "";
   String mtype = null;




%>
   <mm:present referid="import_package">
      <mm:node number="$import_package">
	<%@include file="import.jsp"%>
      </mm:node>
   </mm:present>

   <mm:present referid="delete_package">
     <mm:node number="$delete_package">
       <%@include file="delete.jsp"%>
     </mm:node>
   </mm:present>

   <mm:present referid="publish_package">
      <mm:node number="$publish_package">
	<%@include file="publish.jsp"%>
      </mm:node>
   </mm:present>
   <%
   boolean uploadOK = false;
%>
<mm:log jspvar="log">
<%
   log.info("UPloading");
       


   String fileName = null;

   if (request.getSession(false) != null && "true".equals(request.getSession(false).getAttribute("mayupload"))) {
       log.info("UPloading2");
      if (FileUpload.isMultipartContent(request)) {
          log.info("UPloading4");
         DiskFileUpload upload = new DiskFileUpload();
         upload.setSizeMax(-1); // allow for unlimited file sizes
         upload.setSizeThreshold(4096);
         upload.setRepositoryPath(System.getProperties().getProperty("java.io.tmpdir"));
         List items = upload.parseRequest(request);
         Iterator itr = items.iterator();
         log.info("items .." + items);
         while(itr.hasNext()) {
             FileItem item = (FileItem) itr.next();
             log.info("UPloading2 .." + item);
             
             if (item.isFormField()) {
                 if (item.getFieldName().equals("manager")) {
                     mtype = item.getString();
                 }
             } else {
                 String fieldName = item.getFieldName();
                 File fileSrc = null;
                 if(fieldName.equals("filename")) {
                     //------------- Uploading of new package ----------------
                     
                     //Create a new node which describes this package
                     nodePackage = cloud.getNodeManager("packages").createNode();
                     nodePackage.setValue("uploaddate", "" + ((new Date()).getTime() / 1000));
                     nodePackage.setValue("type", "SCORM");
                     nodePackage.commit();
                     
                     fileName = item.getName().replaceFirst("\\A.*?[/\\\\:]([^/\\\\:]+)$\\z","$1");

                     try {
                         //Internal server error
                         newDir = new File(CommonUtils.fixPath(directory + File.separator + nodePackage.getNumber()));
                         newDir.mkdirs();
                         newDir_ = new File(newDir.getAbsolutePath() + "_");
                         newDir_.mkdirs();
                         
                         File savedFile = new File(CommonUtils.fixPath(directory) + File.separator + nodePackage.getNumber(), fileName);
                         item.write(savedFile);
                         fileSrc = new File(CommonUtils.fixPath(directory + File.separator + fileName));
                         
                         uploadOK = true;
                         savedFile = null;
                     } catch(Exception e) {
                         msg = "Internal server error: " + e.toString();
                         uploadOK = false;
                     }
                     
                     try {// A error during unpacking .zip
                         if(uploadOK) {
                             Unpack.unzipFileToFolder(CommonUtils.fixPath(directory + File.separator + nodePackage.getNumber() + File.separator + fileName), CommonUtils.fixPath(directory + File.separator + nodePackage.getNumber() + "_"));
                         }
                     } catch(Exception e) {
                         msg = "UNZIP Error: " + e.toString();
                         nodePackage.delete(true);
                         Unpack.deleteFolderIncludeSubfolders(newDir.getAbsolutePath(), false);
                         Unpack.deleteFolderIncludeSubfolders(newDir_.getAbsolutePath(), false);
                         uploadOK = false;
                     }


                     //check manifest file here
                     if(uploadOK) {
                         try {
                             File file = new File(CommonUtils.fixPath(directory + File.separator + nodePackage.getNumber() + "_" + File.separator + CP_Core.MANIFEST_NAME));
                             XMLDocument xmlDocument = new XMLDocument();
                             xmlDocument.loadDocument(file);
                             CP_Core cp_core = new CP_Core(xmlDocument);
                             nodePackage.setValue("version", "" + cp_core.getRootManifestElement().getAttributeValue("version"));
                             nodePackage.setValue("name", fileName);
                             nodePackage.commit();
                         } catch(Exception e) {
                             msg = "Error parsing manifest file: " + e.toString();
                             nodePackage.delete(true);
                             Unpack.deleteFolderIncludeSubfolders(newDir.getAbsolutePath(), false);
                             Unpack.deleteFolderIncludeSubfolders(newDir_.getAbsolutePath(), false);
                             uploadOK = false;
                         }
                     }
                     

                     //Copying player with own package ID config
                     filePlayerDir = new File(newDir.getAbsolutePath() + "_player");
                     ServletContext sc = getServletConfig().getServletContext();
                     
                     FileCopier.dirCopy(new File(getServletConfig().getServletContext().getRealPath("/") + File.separator + "education" + File.separator + "scorm" + File.separator + "player"), filePlayerDir);
                     
                     
                     
                     //Get structure of menu and write it to our instance of player
                     try {
                         MenuCreator menuCreator = new MenuCreator(new File(directory + File.separator + nodePackage.getNumber() + "_" + File.separator + CP_Core.MANIFEST_NAME), "http://", baseUrl + "/scorm/" + nodePackage.getNumber() + "_" + "/");
                         String[] arrstrJSMenu = menuCreator.parse(true, "" + nodePackage.getNumber(), "");
                         /*
                           DidactorSettings didactorSetings = new DidactorSettings();
                           didactorSetings.setPackageName("" + nodePackage.getNumber());
                           didactorSetings.setSettingsFilePath("Z:/SCORM/sequence/reload-settings.xml");
                           didactorSetings.setPackageManifestPath(directory + File.separator + nodePackage.getNumber() + "_" + File.separator + CP_Core.MANIFEST_NAME);
                           
                           ScormManager scormManager = new ScormManager(didactorSetings);
                         */
                         File fileMenuConfig = new File(directory + File.separator + nodePackage.getNumber() + "_player" + File.separator + "ReloadContentPreviewFiles" + File.separator + "CPOrgs.js");
                         RandomAccessFile rafileMenuConfig = new RandomAccessFile(fileMenuConfig, "rw");
                         for(int f = 0; f < arrstrJSMenu.length; f++) {
                             rafileMenuConfig.writeBytes(arrstrJSMenu[f]);
                             rafileMenuConfig.writeByte(13);
                             rafileMenuConfig.writeByte(10);
                         }
                         rafileMenuConfig.close();
                         
                     } catch (Exception e) {
                         // wtf
                     }



                     if(uploadOK) {
                         // wtf
                         //Clean up the folder
                         //                     Unpack.deleteFolderIncludeSubfolders(newDir_.getAbsolutePath(), false);
                     }
                 } // item = filename
             }// item is form field
         } // while
      } // is multipart
   } else {
       msg = "YOU MAY NOT UPLOAD";
       uploadOK = false;
   }
%>
       </mm:log>
<%@include file="/shared/setImports.jsp"%>
<%@include file="/education/wizards/roles_defs.jsp" %>
<mm:import id="editcontextname" reset="true">filemanagement</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>
<mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
<% request.getSession().setAttribute("mayupload","true"); %>



<html>
<head>
<title>File manager</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/mmbase/edit/wizard/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/mmbase/edit/wizard/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>
<%
    File dir = new File(directory);
    File[] farray = dir.listFiles();
    if (farray == null)
    {
       throw new ServletException("'" + directory + "' does not appear to be a directory! Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
    }
%>

<table class="head">
   <tr class="headsubtitle">
      <td>
	<mm:listnodescontainer type="packages">
	  <mm:size>
	    <div><di:translate key="scorm.scormpackagelisttotalamount" arg0="${_}" /></div>
	  </mm:size>
	</mm:listnodescontainer>
      </td>
   </tr>
</table>

<table class="body">
   <tr class="searchcanvas">
      <td>
         <table class="searchcontent">
            <tr>
               <td>
                  <form  method="POST" enctype="multipart/form-data">
                     <input type="file" name="filename"  style="width:200px; height:20px">
                     <input type="submit" value="Upload" style="width:60px; text-align:center">
                  </form>
                  <%
                     if (uploadOK)
                     {
                        %><b><di:translate key="scorm.scormpackagelistuploadok" /></b><%
                     }
                  %>
                  <b><%= msg %></b>
               </td>
            </tr>
         </table>
      </td>
   </tr>
   <tr class="listcanvas">
      <td>
         <table class="listcontent">
            <tr class="listheader">
               <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
                  <th/>
               </mm:islessthan>

               <th>#</th>
               <th><di:translate key="scorm.scormpackagelisttablename" /></th>
               <th><di:translate key="scorm.scormpackagelisttableversion" /></th>
               <th><di:translate key="scorm.scormpackagelisttablestatus" /></th>
               <th><di:translate key="scorm.scormpackagelisttableuploaddate" /></th>
               <th><di:translate key="scorm.scormpackagelisttableimportdate" /></th>
            </tr>

            <%
               int fileNum = 0;
            %>
            <mm:listnodes type="packages" orderby="uploaddate" constraints="type LIKE 'SCORM'">

               <mm:import id="imported" reset="true"><mm:field name="importdate"/></mm:import>
               <%
                  fileNum++;
               %>
               <tr>
                  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
                     <td class="deletebutton">
                        <a
                           href='index.jsp?delete_package=<mm:field name="number"/>'
                           onclick="return confirm('<di:translate key="scorm.filemanagementdeleteprompt" />');">
			   <mm:link page="/mmbase/edit/wizard/media/remove.gif">
			     <img border="0" src="${_}" />
			   </mm:link>
                        </a>
                     </td>
                  </mm:islessthan>

                  <td class="field"><mm:field name="name"/></td>
                  <td class="field"><!-- mm:field name="filename"/ --></td>
                  <td class="field"><mm:field name="version"/></td>
                  <td>
                     <mm:compare referid="imported" value="-1" inverse="true">
                        <di:translate key="scorm.scormpackagelistimported" /> (<a href="?publish_package=<mm:field name="number"/>"><di:translate key="scorm.scormpackagelistpublishlink" /></a>)
                     </mm:compare>
                     <mm:compare referid="imported" value="-1">
                        <di:translate key="scorm.scormpackagelistuploaded" /> (<a href="?import_package=<mm:field name="number"/>"><di:translate key="scorm.scormpackagelistimportlink" /></a>)
                     </mm:compare>
                  </td>
                  <td>
                     <mm:field name="uploaddate" jspvar="date" vartype="date">
                        <%= df.format(date) %>
                     </mm:field>
                  </td>
                  <td>
                     <mm:compare referid="imported" value="-1" inverse="true">
                        <mm:field name="importdate" jspvar="date" vartype="date">
                           <%= df.format(date) %>
                        </mm:field>
                     </mm:compare>
                  </td>
               </tr>
            </mm:listnodes>
         </table>
      </td>
   </tr>
</table>

</body>
</html>

</mm:islessthan>
</mm:cloud>

