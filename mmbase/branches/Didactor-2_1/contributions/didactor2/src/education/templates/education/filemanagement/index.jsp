<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.io.File, org.apache.commons.fileupload.*, java.util.List, java.util.Iterator, java.util.Collections, java.util.ArrayList, org.mmbase.bridge.Node, org.mmbase.bridge.NodeManager, org.mmbase.bridge.NodeIterator"%>
<%

//    String directory = getServletContext().getRealPath("/education/files");

    String directory = getServletContext().getInitParameter("filemanagementBaseDirectory");
    String baseUrl = getServletContext().getInitParameter("filemanagementBaseUrl");

    if (directory == null || baseUrl == null)
    {
        throw new ServletException("Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
    }

    boolean uploadOK = false;
    String fileName = null;
    String mtype = null;
    if (request.getSession(false) != null && "true".equals(request.getSession(false).getAttribute("mayupload")))
    {

        if (FileUpload.isMultipartContent(request))
        {
            DiskFileUpload upload = new DiskFileUpload();
            upload.setSizeMax(250*1024*1024);
            upload.setSizeThreshold(4096);
            upload.setRepositoryPath(System.getProperties().getProperty("java.io.tmpdir"));
            List items = upload.parseRequest(request);
            Iterator itr = items.iterator();
            while(itr.hasNext())
            {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField())
                {
                    if (item.getFieldName().equals("manager"))
                    {
                        mtype = item.getString();
                    }
                }
                else
                {
                    String fieldName = item.getFieldName();
                    if(fieldName.equals("filename"))
                    {
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
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<%@include file="/education/wizards/roles_defs.jsp" %>
<mm:import id="editcontextname" reset="true">filemanagement</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>
<mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">

<% request.getSession().setAttribute("mayupload","true"); %>
<%
    String msg = "";
    if (uploadOK && fileName != null)
    {
        // add link to specific builder

        String manager = null;

        if ("audio".equals(mtype))
        {
            manager = "audiotapes";
        }
        else if ("video".equals(mtype))
        {
            manager = "videotapes";
        }
        else if ("url".equals(mtype))
        {
            manager = "urls";
        }

        if (manager == null)
        {
            msg = "Onbekend bestands type '"+mtype+"'";
        }
        else
        {
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
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>
<script>
if (top == self) {
    var loc = document.location.href;
    loc = loc.replace(/&amp;/ig,'&').replace(/(education\/).*/,"$1wizards/index.jsp");
    document.location.href = loc;
}
</script>
<mm:import externid="deletefile" jspvar="deletefile"/>
<%
    File dir = new File(directory);
    File[] farray = dir.listFiles();
    if (farray == null) {
         throw new ServletException("'"+directory+"' does not appear to be a directory! Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
    }
%>

<table class="head">
   <tr class="headsubtitle">
      <td>
         <%
            String sResults = "";
         %>
         <mm:import id="FTPfiles" jspvar="sTemplate" vartype="String" reset="true"><di:translate key="education.ftpfiles" /></mm:import>
         <%
            sResults = sTemplate.replaceAll("\\{\\$\\$\\$\\}", "" + farray.length);
         %>
         <div><%= sResults %></div>
      </td>
   </tr>
</table>

<table class="body">
   <tr class="searchcanvas">
      <td>
         <table class="searchcontent">
            <tr>
               <td>
                  <form action="index.jsp" method="POST" enctype="multipart/form-data">
                     <input type="file" name="filename"  style="width:200px; height:20px">
                     <select name="manager">
                        <option value="url">Algemeen URL</option>
                        <option value="audio">Audio</option>
                        <option value="video">Video</option>
                     </select>
                     <input type="submit" value="Upload" style="width:60px; text-align:center">
                  </form>
                  <% if (uploadOK) { %><b><di:translate key="education.fileuploadok" /></b><% } %><b><%= msg %></b>
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
               <th><di:translate key="education.filemanagementtablename" /></th>
               <th><di:translate key="education.filemanagementtabletype" /></th>
               <th><di:translate key="education.filemanagementtablesize" /></th>
               <th><di:translate key="education.filemanagementtablefileext" /></th>
            </tr>
            <%
               List files = new ArrayList();

               for (int i = 0; i < farray.length; i++)
               {
                  files.add(farray[i]);
               }

               Collections.sort(files);
               Iterator it = files.iterator();
               int fileNum = 0;

               while (it.hasNext())
               {
                  File file = (File) it.next();

                  if (file.isDirectory())
                  {
                     continue;
                  }

                  if (deletefile != null && file.getName().equals(deletefile))
                  {
                     String[] managers = {"audiotapes","videotapes","urls"};

                     for (int i = 0; i < managers.length; i++)
            {
                        NodeIterator ni = cloud.getNodeManager(managers[i]).getList("url='"+baseUrl+"/"+deletefile+"'",null,null).nodeIterator();

                        while(ni.hasNext())
                        {
                           ni.nextNode().delete(true); // delete next node related with this file ...
                        }
                     }
                     file.delete(); // and delete file itself ...
                     continue;
                  }

                  fileNum++;
                  %>

                  <mm:import id="filename" reset="true"><%= file.getName() %></mm:import>
                  <mm:import id="ext" reset="true"><%= file.getName().replaceAll(".*?\\.","") %></mm:import>

                  <tr>
                     <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
                        <td class="deletebutton">
                           <a
                              href='index.jsp?deletefile=<mm:write referid="filename"/>'
                              onclick="return confirm('<di:translate key="education.filemanagementdeleteprompt" />');">
                                <img border="0" src="<%= request.getContextPath() %>/editwizards/media/remove.gif"/>
                           </a>
                        </td>
                     </mm:islessthan>

                     <td class="field">
                        <%= fileNum %>
                     </td>
                     <td class="field">
                        <a href="<%= baseUrl %>/<mm:write referid="filename"/>"><mm:write referid="filename"/></a>
                     </td>

                     <td class="field">
                        <%
                           String[] managers = {"audiotapes","videotapes","urls"};
                           for (int i = 0; i < managers.length; i++)
                           {
                              NodeIterator ni = cloud.getNodeManager(managers[i]).getList("url='"+baseUrl+"/"+file.getName()+"'",null,null).nodeIterator();
                              if (ni.hasNext())
                              {
                                 %><%= managers[i] %><%
                                 break;
                              }
                           }
                        %>
                     </td>

                     <td class="field">
                        <%
                           if(file.length() > 1024 * 1024)
                           {
                              out.print("" + file.length() / (1024*1024) + "MB");
                           }
                           else
                           {
                              out.print("" + file.length() / 1024 + "KB");
                           }
                        %>
                     </td>

                     <td class="field">
                       <mm:write referid="ext"/>
                     </td>
                  </tr>
            <% } %>
         </table>
      </td>
   </tr>
</table>





</body>
</html>

</mm:islessthan>
</mm:cloud>
</mm:content>
