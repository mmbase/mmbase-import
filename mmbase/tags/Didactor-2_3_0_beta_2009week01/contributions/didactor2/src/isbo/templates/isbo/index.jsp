<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


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
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<%-- <di:hasrole role="systemadministrator"> --%>

<% request.getSession().setAttribute("hassysprivs","true"); %>
<%
	String errorMsg = "OK";
    if (xmlStream != null)
    {
		try {
        	new ISBOReader(cloud).parse(xmlStream);
		}
    	catch (Exception e) {
    	    errorMsg = e.toString();
    		e.printStackTrace(System.err);
    	}

    }
%>

<html>
<head>
<title>ISBO importer</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
   <script>
if (top == self) {
    var loc = document.location.href;
    loc = loc.replace(/&amp;/ig,'&').replace(/(education\/).*/,"$1wizards/index.jsp");
    document.location.href = loc;
}
</script>
</head>
<body>
<table class="head">
   <tr class="headsubtitle">
      <td><%= xmlStream == null ? "ISBO Importer" : "Import: "+errorMsg %></td>
    </tr>
</table>
<table class="body">
   <tr class="searchcanvas">
      <td>
         <table class="searchcontent">
            <tr>
               <td>
    
<form action="index.jsp" method="POST" enctype="multipart/form-data">
<input type="file" size="120" name="filename" style=" height:20px" /> ISBO file<br/>
<input type="submit" value="Start" style="width:60px; text-align:center" />
</form>
</td>
</tr></table></td></tr></table>
</body>
</html>

<%--</di:hasrole>--%>
</mm:cloud>
</mm:content>
