<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.io.File, org.apache.commons.fileupload.*, java.util.List, java.util.Iterator, java.util.Collections, java.util.ArrayList"%>
<%

//    String directory = getServletContext().getRealPath("/education/files");

    String directory = getServletContext().getInitParameter("filemanagementBaseDirectory");
    String baseUrl = getServletContext().getInitParameter("filemanagementBaseUrl");
    
    if (directory == null || baseUrl == null) {
        throw new ServletException("Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
    }
    
    boolean uploadOK = false;

    if (request.getSession(false) != null && "true".equals(request.getSession(false).getAttribute("mayupload"))) {

        if (FileUpload.isMultipartContent(request)) {
            DiskFileUpload upload = new DiskFileUpload();
            upload.setSizeMax(50*1024*1024);
            upload.setSizeThreshold(4096);
            upload.setRepositoryPath(System.getProperties().getProperty("java.io.tmpdir"));
            List items = upload.parseRequest(request);
            Iterator itr = items.iterator();
            while(itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if(!item.isFormField()) {
                    String fieldName = item.getFieldName();
                    if(fieldName.equals("filename")) {
                        File fullFile  = new File(item.getName());
                        File savedFile = new File(directory,fullFile.getName());
                        item.write(savedFile);
                        uploadOK=true;
                    }
                }
            }            
        }
    }
%>

<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<di:hasrole role="filemanager">
<% request.getSession().setAttribute("mayupload","true"); %>
<html>
<head>
<title>File manager</title>
<link href="../../editwizards/style/layout/wizard.css" type="text/css" rel="stylesheet">
</head>
<body>
<mm:import externid="deletefile" jspvar="deletefile"/>
<table>
<%
    File dir = new File(directory);
    File[] farray = dir.listFiles();
    List files = new ArrayList();
    for (int i = 0; i < farray.length; i++) {
        files.add(farray[i]);
    }
    Collections.sort(files);
    Iterator it = files.iterator();
    while (it.hasNext()) {
        File file = (File) it.next();
        if (file.isDirectory()) {
            continue;
        }
        if (file.getName().equals(deletefile)) {
            file.delete();
            continue;
        }
        %><mm:import id="filename" reset="true"><%= file.getName() %></mm:import>
        <tr>
        <td>
            <a href="../files/<mm:write referid="filename"/>"><mm:write referid="filename"/></a> 
        </td>
        <td>
            <a href="index.jsp?deletefile=<mm:write referid="filename"/>" onclick="return confirm('Wis <mm:write referid="filename"/>?');">Wis</a>
        </td>
        </tr>
<%  } %>
</table>
    <% if (uploadOK) { %><b>Upload OK</b><br><% } %>
    <form action="index.jsp" method="POST" enctype="multipart/form-data">
    <input type="file" name="filename">
    <input type="submit" value="Ok">
    </form>

</body>
</html>

</di:hasrole>
</mm:cloud>
</mm:content>
