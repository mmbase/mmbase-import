<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
   <title>EditWizard samples</title>
   <link rel="stylesheet" type="text/css" href="style.css" />
	
    <script language="javascript"><!--
	  function openListImages(el) {
	  var href = el.getAttribute("href");
	  var zoek = document.forms[0].elements["imagedesc"].value.toUpperCase();
	  if (zoek != '') {
	  href += "&constraints=UCASE%28description%29%20LIKE%20%27%25" + zoek + "%25%27%20or%20UCASE%28title%29%20LIKE%20%27%25" + zoek + "%25%27";
	  }
	  document.location = href;
	  return false;
}
--></script>
</head>
<body>
<form>
	<h1>Editwizard - samples (taglib)</h1>

	<br />	
	<a href="<mm:url page="list.jsp">
		  <mm:param name="title">People</mm:param>
		  <mm:param name="wizard">samples/people</mm:param>
	      <mm:param name="nodepath">people</mm:param>
	      <mm:param name="fields">firstname,lastname</mm:param>
	      <mm:param name="orderby">lastname</mm:param>
          <mm:param name="directions">up</mm:param>
          <mm:param name="distinct">false</mm:param>
		  <mm:param name="constraints"></mm:param>
          </mm:url>">Person test</a><br />   
    <a href="<mm:url page="list.jsp">
            <mm:param name="title">Images</mm:param>
            <mm:param name="wizard">samples/imageupload</mm:param>
            <mm:param name="nodepath">images</mm:param>
            <mm:param name="fields">title</mm:param>
            <mm:param name="orderby">title</mm:param>
            <mm:param name="directions">up</mm:param>
            <mm:param name="distinct">false</mm:param>
            </mm:url>" 
            onClick="return openListImages(this);">    
        Images</a> (search:  <input type="text" name="imagedesc" value="" style="width:200px;text-align:left;" />) <br />
	<a href="<mm:url page="list.jsp">
		  <mm:param name="title">News</mm:param>
		  <mm:param name="wizard">samples/news</mm:param>
	      <mm:param name="nodepath">news</mm:param>
	      <mm:param name="fields">title</mm:param>
	      <mm:param name="orderby">title</mm:param>
          <mm:param name="directions">up</mm:param>
          <mm:param name="distinct">false</mm:param>
          </mm:url>">News</a><br />  
</form>
</body>
</html>
