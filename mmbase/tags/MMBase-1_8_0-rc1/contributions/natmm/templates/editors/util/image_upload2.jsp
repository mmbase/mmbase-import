<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,java.io.*,java.text.*,java.util.zip.*,org.mmbase.bridge.*,org.apache.commons.fileupload.*"%> 
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<%
  String tempDir =  "C:/temp/";
  String zipFile =  "tmpzip.zip";
  Node sourceNode = null;
  RelationManager rm = null;
  String referrer = "";

  if(FileUpload.isMultipartContent(request))
  {
    DiskFileUpload upload = new DiskFileUpload();
    upload.setSizeMax(250*1024*1024);
    upload.setSizeThreshold(4096);
    upload.setRepositoryPath(tempDir);
    List items = upload.parseRequest(request);
    Iterator itr = items.iterator();
    while(itr.hasNext()) {

      FileItem item = (FileItem) itr.next();
      String fieldName = item.getFieldName();
      if (item.isFormField())
      {
        if (fieldName.equals("source")) {
          String sourceNum = item.getString();
          if(sourceNum!=null&&!sourceNum.equals("")) {
            sourceNode = cloud.getNode(sourceNum);
          }
        }
        if (fieldName.equals("role")) {
          String relName = item.getString();
          if(relName!=null&&!relName.equals("")) {
            rm = cloud.getRelationManager(relName);
          }
        }
        if (fieldName.equals("referrer")) {
          referrer = item.getString();
        }

      } else {

        if(fieldName.equals("filename")) {
          try {
            //Internal server error
            File savedFile = new File(tempDir+ zipFile);
            item.write(savedFile);
          } catch(Exception e) { }
        }
      }
    }
  }

  int BUFFER = 2048;

  Vector dataFiles = new Vector();
  ZipFile zFile = new ZipFile(tempDir+zipFile);
  Enumeration entries = zFile.entries();
  int j=0;
  while(entries.hasMoreElements()) { // *** write the files to disk ***
    try {
      int count;
      byte data[] = new byte[BUFFER];
      ZipEntry entry = (ZipEntry) entries.nextElement();
      InputStream zis = zFile.getInputStream(entry);

      String dataFile = entry.getName();
      if(dataFile.indexOf("/")>-1) {
         dataFile = dataFile.substring(dataFile.lastIndexOf("/")+1);
      }
      dataFiles.add(dataFile);
  
      BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(tempDir + dataFile), BUFFER);
      while ((count = zis.read(data)) > 0) {
        dest.write(data, 0, count);
      }
      dest.flush();
      dest.close();
      zis.close();
      j++;
    } catch (Exception e) {
      log.error("Error when unzipping uploaded images file " + e);
    }
  }
  zFile.close();
  File deleteFile = new File(tempDir+ zipFile);
  deleteFile.delete();
  log.info("Unzipped " + j + " images from " + zipFile);

  int i = 0;
  int k = 0;
  while(i<dataFiles.size()) {
    try {
      String alias = (String) dataFiles.get(i);
      File f = null;
      int fsize = 0;
      try {
        f = new File(tempDir + alias);
        fsize = (int) f.length();
      } catch (Exception e) { }
      if(fsize!=0) {
        byte[] thedata = new byte[fsize];
        FileInputStream instream = new FileInputStream(f);
        instream.read(thedata);

        Node imgNode = cloud.getNodeManager("images").createNode();
        imgNode.setValue("title",alias);
        imgNode.setValue("handle",thedata);
        imgNode.setValue("description","Imported " + (new Date()));
        imgNode.commit();
        if(sourceNode!=null&&rm!=null) {
           Relation thisRelation = sourceNode.createRelation(imgNode, rm);
           thisRelation.commit();
        }
        instream.close();
        f.delete();
        k++;
      }
    } catch (Exception e) {
      log.error("Error when importing uploaded images " + e);
    }
    i++;
  }
  log.info("Imported " + k + " images from " + tempDir);
  if (!referrer.equals("")) {
   %>
   <mm:redirect page="<%=referrer %>" />
   <%
  }
%>
Unzipped <%= j %> and imported <%= k %> images.
</mm:log>
</mm:cloud>
</mm:content>
