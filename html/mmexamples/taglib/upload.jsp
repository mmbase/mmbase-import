<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<head>
 <title>A simple http upload</title>
 <link href="../../css/mmbase.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="menu.jsp"%>
<mm:import externid="processupload">false</mm:import>
<mm:cloud method="http">

  <h1>Example of how to upload a file into mmbase using taglibs</h1>
	<p>This page shows an example of how to upload an attachment into mmbase
		the page constist of two parts and depending on the processupload paramteter
		one part of the document is shown
	</p>
	<%-- the form part --%>
  <mm:compare referid="processupload" value="false">
	  <%-- create a html form  with method post and enctype multipart   --%>
  	<form action="upload.jsp" method="post" enctype="multipart/form-data">
  		<input type="hidden" name="processupload" value="true"/>
      <mm:fieldlist nodetype="attachments" fields="handle">
        Select the file you want to upload: <mm:fieldinfo type="input"/>
      </mm:fieldlist>
  		<input type="submit"/>
  	</form>
	</mm:compare>

	<%-- the process form part --%>
  <mm:compare referid="processupload" value="true">
	  <%-- create a node of type attachments --%>
    <mm:createnode type="attachments" id="attachment">
			<%-- set only the handle part --%>
		  <mm:fieldlist type="all" fields="handle">
			   <mm:fieldinfo type="useinput" />
			</mm:fieldlist>
    </mm:createnode>

    <%-- show some info --%>
	  <mm:node referid="attachment">
        <mm:field name="number"/>
        <mm:field name="gui()"/>
        <mm:field name="size"/>
	  </mm:node>
	</mm:compare>
</mm:cloud>
