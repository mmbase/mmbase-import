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

<%@page import="nl.didactor.component.scorm.player.MenuCreator"%>

<%@page import="uk.ac.reload.jdom.XMLDocument"%>
<%@page import="uk.ac.reload.moonunit.contentpackaging.CP_Core"%>

aaa
<mm:cloud rank="editor" jspvar="cloud">
<mm:cloudinfo type="user" />

<mm:import from="parameters" externid="import_package"   vartype="String" jspvar="requestImportPackageID" />
<mm:import from="parameters" externid="delete_package"   vartype="String" jspvar="requestDeletePackageID" />
<mm:import from="parameters" externid="publish_package"  vartype="String" jspvar="requestPublishPackageID" />



<%
String dir = "scorm";

File directory = org.mmbase.servlet.FileServlet.getFile(dir, response);
directory.mkdir();
String baseUrl = request.getContextPath() + org.mmbase.servlet.MMBaseServlet.getBasePath("files") + dir;

   SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
   Node packageNode = null;
   File newDir = null;
   File newDir_ = null;
   File fileStoreDir = null;
   File fileTempDir  = null;
   File filePlayerDir  = null;
   String sFileName = null;
   String msg = "";
   String mtype = null;




%>
<mm:log>checking ${import_package}</mm:log>
   <mm:present referid="import_package">
     <mm:node number="$import_package">
       <mm:log>Importing package ${_node}</mm:log>
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



String fileName = null;

if (request.getSession(false) != null && "true".equals(request.getSession(false).getAttribute("mayupload"))) {
   log.info("Uploading SCORM package");

    if (FileUpload.isMultipartContent(request)) {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setSizeMax(-1); // allow for unlimited file sizes
        upload.setSizeThreshold(4096);
        upload.setRepositoryPath(System.getProperties().getProperty("java.io.tmpdir"));
        List items = upload.parseRequest(request);
        Iterator itr = items.iterator();
        while(itr.hasNext()) {
            FileItem item = (FileItem) itr.next();
            log.debug("Considering .." + item);

            if (item.isFormField()) {
                if (item.getFieldName().equals("manager")) {
                    mtype = item.getString();
                }
            } else {
                String fieldName = item.getFieldName();
                File fileSrc = null;
                if(fieldName.equals("filename")) {
                    log.info("Creating node of type packages");
                    //------------- Uploading of new package ----------------

                    //Create a new node which describes this package
                    packageNode = cloud.getNodeManager("packages").createNode();
                    packageNode.setValue("uploaddate", "" + ((new Date()).getTime() / 1000));
                    packageNode.setValue("type", "SCORM");
                    packageNode.commit();

                    fileName = item.getName().replaceFirst("\\A.*?[/\\\\:]([^/\\\\:]+)$\\z","$1");

                     try {
                         //Internal server error
                         newDir = new File(directory, "" + packageNode.getNumber());
                         newDir.mkdirs();
                         newDir_ = new File(directory, packageNode.getNumber()  + "_");
                         newDir_.mkdirs();

                         File savedFile = new File(newDir, fileName);
                         item.write(savedFile);
                         fileSrc = new File(directory, fileName);

                         uploadOK = true;
                         savedFile = null;
                     } catch(Exception e) {
                         msg = "Internal server error: " + e.toString();
                         uploadOK = false;
                     }

                     try {// A error during unpacking .zip
                         if(uploadOK) {
                             Unpack.unzipFileToFolder(directory + File.separator + packageNode.getNumber() + File.separator + fileName, directory + File.separator + packageNode.getNumber() + "_");
                         }
                     } catch(Exception e) {
                         msg = "UNZIP Error: " + e.toString();
                         packageNode.delete(true);
                         Unpack.deleteFolderIncludeSubfolders(newDir.getAbsolutePath(), false);
                         Unpack.deleteFolderIncludeSubfolders(newDir_.getAbsolutePath(), false);
                         uploadOK = false;
                     }


                     //check manifest file here
                     if(uploadOK) {
                         try {
                             File file = MenuCreator.getManifest(packageNode);
                             XMLDocument xmlDocument = new XMLDocument();
                             xmlDocument.loadDocument(file);
                             CP_Core cp_core = new CP_Core(xmlDocument);
                             packageNode.setValue("version", "" + cp_core.getRootManifestElement().getAttributeValue("version"));
                             packageNode.setValue("name", fileName);
                             packageNode.commit();
                         } catch(Exception e) {
                             msg = "Error parsing manifest file: " + e.toString();
                             packageNode.delete(true);
                             Unpack.deleteFolderIncludeSubfolders(newDir.getAbsolutePath(), false);
                             Unpack.deleteFolderIncludeSubfolders(newDir_.getAbsolutePath(), false);
                             uploadOK = false;
                         }
                     }


                     // THIS IS ABSURD

                     //Get structure of menu and write it to our instance of player
                     try {
                         MenuCreator menuCreator = new MenuCreator(packageNode);
                         String[] arrstrJSMenu = menuCreator.parse(true);
                         File fileMenuConfig = new File(directory, packageNode.getNumber() + "_player" + File.separator + "ReloadContentPreviewFiles" + File.separator + "CPOrgs.js");
                         // i'm sorry, but wtf?
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
<di:has action="rw" editcontext="mayupload">
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
    File[] farray = directory.listFiles();
    if (farray == null) {
    throw new ServletException("'" + directory.getAbsolutePath() + "' does not appear to be a directory! ");
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
		 <mm:link>
		   <form  action="${_}" method="POST" enctype="multipart/form-data">
		     <input type="file" name="filename"  style="width:200px; height:20px" />
		     <input type="submit" value="Upload" style="width:60px; text-align:center" />
		   </form>
		 </mm:link>
                  <%
                     if (uploadOK) {
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
               <th colspan="2">#</th>
               <th><di:translate key="scorm.scormpackagelisttablename" /></th>
               <th><di:translate key="scorm.scormpackagelisttableversion" /></th>
               <th><di:translate key="scorm.scormpackagelisttablestatus" /></th>
               <th><di:translate key="scorm.scormpackagelisttableuploaddate" /></th>
               <th><di:translate key="scorm.scormpackagelisttableimportdate" /></th>
            </tr>

            <%
               int fileNum = 0;
            %>

            <mm:listnodes type="packages" orderby="uploaddate" directions="down" constraints="type LIKE 'SCORM'"><!-- wtf -->

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
		  <td class="field">
		    <mm:field name="number" />
		  </td>
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
</di:has>
</mm:cloud>

