<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<%@page import="java.io.File, org.apache.commons.fileupload.*, java.util.List, java.util.Iterator, java.util.Collections, java.util.ArrayList, org.mmbase.bridge.Node, org.mmbase.bridge.NodeManager, org.mmbase.bridge.NodeIterator, java.io.InputStream, nl.didactor.isbo.ISBOReader"%>
<%
    InputStream xmlStream = null;
    String fileName = null;
    String mtype = null;
    if (request.getSession(false) != null && "true".equals(request.getSession(false).getAttribute("hassysprivs")))
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
                if (!item.isFormField())
                {
                    String fieldName = item.getFieldName();
                    if(fieldName.equals("filename"))
                    {
                        System.err.println("found an isbo xml file");
                        xmlStream = item.getInputStream();
                    }
                }
            }
        }
    }
%>

<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<di:hasrole role="systemadministrator">

<% request.getSession().setAttribute("hassysprivs","true"); %>
<%
    String msg = "";
    if (xmlStream != null)
    {
        new ISBOReader(cloud).parse(xmlStream);
    }
%>
<fmt:bundle basename="nl.didactor.component.isbo.ISBOMessageBundle">

<html>
<head>
<title>ISBO importer</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>
<h1>ISBO Importer</h1>
<% if (xmlStream != null) { %>
<h2>Import OK</h2>
<% } %>
<form action="index.jsp" method="POST" enctype="multipart/form-data">
<input type="file" name="filename"/> ISBO file<br/>
<input type="submit" value="Start"/>
</form>
</body>
</html>

</fmt:bundle>
</di:hasrole>
</mm:cloud>
</mm:content>
