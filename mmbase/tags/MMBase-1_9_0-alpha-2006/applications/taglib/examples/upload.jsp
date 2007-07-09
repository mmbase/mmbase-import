<%@page session="true" language="java" contentType="text/html; charset=utf-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<head>
 <title>HTTP upload</title>
 <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
  <%@ include file="menu.jsp"%>
<mm:import externid="processupload">false</mm:import>
<mm:import externid="processupload_alternative">false</mm:import>
<mm:cloud method="http">

  <h1>Uploading a file into MMBase</h1>
  <p>
    This page shows an example of how to upload an attachment into mmbase
    the page constist of two parts and depending on the processupload parameter
    one part of the document is shown
  </p>
  <%-- the form part --%>

  <mm:compare referid="processupload" value="false">
    <mm:compare referid="processupload_alternative" value="false">
    <%-- create a html form  with method post and enctype multipart   --%>
    <form action="upload.jsp" method="post" enctype="multipart/form-data">
      <input type="hidden" name="processupload" value="true"/>
        Select the file you want to upload: 
      <mm:fieldlist id="fields1" nodetype="attachments" fields="title,handle">
        <p><mm:fieldinfo type="guiname" />: <mm:fieldinfo type="input"/></p>
      </mm:fieldlist>
        Select another file you want to upload:
      <mm:fieldlist id="fields2" nodetype="attachments" fields="title,handle">
        <p><mm:fieldinfo type="guiname" />: <mm:fieldinfo type="input"/></p>
      </mm:fieldlist>      
      <input type="submit"/>
    </form>
    <p>This is a different way of implementing it (using mm:setfield)</p>
    <form action="upload.jsp" method="post" enctype="multipart/form-data">
      <input type="hidden" name="processupload_alternative" value="true"/>
      <p>Title: <input  type="text" name="title"  value=""/></p>
      <p>File: <input  type="file" name="fileupload" /></p>
      <input type="submit" />
    </form>
    </mm:compare>
  </mm:compare>

  <%-- the process form part --%>
  <mm:compare referid="processupload" value="true">
    <%-- create a node of type attachments --%>
    <mm:createnode type="attachments" id="attachment1">
      <mm:fieldlist id="fields1" fields="title,handle">
         <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
      <mm:field name="title">
        <mm:isempty>
          <mm:setfield name="title"><mm:field name="filename" /></mm:setfield>
        </mm:isempty>
      </mm:field>
    </mm:createnode>
    <%-- show some info --%>
    <mm:node referid="attachment1">
        number: <mm:field name="number"/><br/>
        title: <mm:field name="title"/><br/>
        mimetype: <mm:field name="mimetype"/><br/>
        size: <mm:field name="size"/><br/>
        gui: <mm:function name="gui" /><br />
    </mm:node>
    <mm:import externid="fields2_handle" />
    <mm:isnotempty referid="fields2_handle">
      <mm:createnode type="attachments" id="attachment2">
        <mm:fieldlist id="fields2" fields="title,handle">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
        <mm:field name="title">
          <mm:isempty>
            <mm:setfield name="title"><mm:field name="filename" /></mm:setfield>
          </mm:isempty>
        </mm:field>
      </mm:createnode>
      <%-- show some info --%>
      <mm:node referid="attachment2">
        number: <mm:field name="number"/><br/>
        title: <mm:field name="title"/><br/>
        mimetype: <mm:field name="mimetype"/><br/>
        size: <mm:field name="size"/><br/>
        gui: <mm:function name="gui" /><br />
      </mm:node>
    </mm:isnotempty>


  </mm:compare>

  <mm:compare referid="processupload_alternative" value="true">
    <mm:import externid="title"  from="multipart"/>
    <mm:import externid="fileupload" from="multipart" vartype="bytes" />
    <mm:createnode id="attachment1" type="attachments">
      <mm:setfield name="title"><mm:write referid="title" /></mm:setfield>
      <mm:setfield name="handle" valueid="fileupload" />
      <!-- would also work (but with an intermediate 'base64' encoding 
      <mm:setfield name="handle"><mm:write referid="fileupload" /></mm:setfield>
      -->
    </mm:createnode>
      <%-- show some info --%>
      <mm:node referid="attachment1">
        number: <mm:field name="number"/><br/>
        title: <mm:field name="title"/><br/>
        mimetype: <mm:field name="mimetype"/><br/>
        size: <mm:field name="size"/><br/>
        gui: <mm:function name="gui" /><br />
      </mm:node>
  </mm:compare>
</mm:cloud>
