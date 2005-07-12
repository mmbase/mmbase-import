<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.io.File, org.apache.commons.fileupload.*, java.util.List, java.util.Iterator, java.util.Collections, java.util.ArrayList, org.mmbase.bridge.Node, org.mmbase.bridge.NodeManager, org.mmbase.bridge.NodeIterator"%>
<%

//    String directory = getServletContext().getRealPath("/education/files");

    String directory = getServletContext().getInitParameter("filemanagementBaseDirectory");
    String baseUrl = getServletContext().getInitParameter("filemanagementBaseUrl");
    
    if (directory == null || baseUrl == null) {
        throw new ServletException("Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
    }
    
    boolean uploadOK = false;
    String fileName = null;
    String mtype = null;
    if (request.getSession(false) != null && "true".equals(request.getSession(false).getAttribute("mayupload"))) {

        if (FileUpload.isMultipartContent(request)) {
            DiskFileUpload upload = new DiskFileUpload();
            upload.setSizeMax(250*1024*1024);
            upload.setSizeThreshold(4096);
            upload.setRepositoryPath(System.getProperties().getProperty("java.io.tmpdir"));
            List items = upload.parseRequest(request);
            Iterator itr = items.iterator();
            while(itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {
                    if (item.getFieldName().equals("manager")) {
                        mtype = item.getString();
                    }
                }
                else {
                    String fieldName = item.getFieldName();
                    if(fieldName.equals("filename")) {
                        fileName = item.getName().replaceFirst("\\A.*?[/\\\\:]([^/\\\\:]+)$\\z","$1");
                        File savedFile = new File(directory,fileName);
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
<%@include file="/education/wizards/roles_defs.jsp" %>

<mm:import id="editcontextname" reset="true">filemanagement</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>
<mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">

<% request.getSession().setAttribute("mayupload","true"); %>
<%
    String msg = "";
    if (uploadOK && fileName != null) {
        // add link to specific builder
        
        String manager = null;
        if ("audio".equals(mtype)) {
            manager = "audiotapes";
        } else if ("video".equals(mtype)) {
            manager = "videotapes";
        } else if ("url".equals(mtype)) {
            manager = "urls";
        }
        
        if (manager == null) {
            msg = "Onbekend bestands type '"+mtype+"'";
        }
        else {
            Node n = cloud.getNodeManager(manager).createNode();
            n.setValue( "urls".equals(manager) ? "name" : "title" ,fileName);
            n.setValue("url",baseUrl+"/"+fileName);
            n.commit();
        }
    }
%>
      
        

<html>
<head>
<title>File manager</title>
<link href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />"></link>
<link href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" type="text/css" rel="stylesheet">
</head>
<body>
<mm:import externid="deletefile" jspvar="deletefile"/>
<table border="1" class="body">
<tr class="header"><th>Naam</th><th>Type</th><th>Grootte</th><th>Bestandsextensie</th></tr>
<%
    File dir = new File(directory);
    File[] farray = dir.listFiles();
    if (farray == null) {
         throw new ServletException("'"+directory+"' does not appear to be a directory! Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
    }
    
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
        if (deletefile != null && file.getName().equals(deletefile)) {
            String[] managers = {"audiotapes","videotapes","urls"};
            for (int i = 0; i < managers.length; i++) {
                NodeIterator ni = cloud.getNodeManager(managers[i]).getList("url='"+baseUrl+"/"+deletefile+"'",null,null).nodeIterator();
                while(ni.hasNext()) {
                    ni.nextNode().delete(true);
                }
            }
            file.delete();
            continue;
        }
        %><mm:import id="filename" reset="true"><%= file.getName() %></mm:import>
          <mm:import id="ext" reset="true"><%= file.getName().replaceAll(".*?\\.","") %></mm:import>
        <tr>
        <td>
            <a href="<%= baseUrl %>/<mm:write referid="filename"/>"><mm:write referid="filename"/></a> 
        </td>
        <td>
        <%
        String[] managers = {"audiotapes","videotapes","urls"};
            for (int i = 0; i < managers.length; i++) {
                NodeIterator ni = cloud.getNodeManager(managers[i]).getList("url='"+baseUrl+"/"+file.getName()+"'",null,null).nodeIterator();
                if (ni.hasNext()) {
                    %><%= managers[i] %><%
                    break;
                }
            }
        %>
        </td>
        <td>
        <%=
            (file.length() / 1025)
        %> Kb
        </td>
        <td><mm:write referid="ext"/></td>
        <td>
            <a href="index.jsp?deletefile=<mm:write referid="filename"/>" onclick="return confirm('Wis <mm:write referid="filename"/>?');">Wis</a>
        </td>
        </tr>
<%  } %>
</table>
    <% if (uploadOK) { %><b>Upload OK</b><br><% } %>
    <b><%= msg %></b><br>
    <form action="index.jsp" method="POST" enctype="multipart/form-data">
    <input type="file" name="filename">
    <select name="manager">
        <option value="url">Algemeen URL</option>
        <option value="audio">Audio</option>
        <option value="video">Video</option>
    </select>
    <input type="submit" value="Ok">
    </form>

</body>
</html>

</mm:islessthan>
</mm:cloud>
</mm:content>
