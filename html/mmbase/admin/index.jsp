<%@ page language="java" contentType="text/html" session="false" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<mm:content expires="0" escaper="none">
<mm:cloud>
<mm:import externid="category">about</mm:import>
<mm:import externid="subcategory"></mm:import>
<mm:import externid="component">core</mm:import>
<mm:import externid="block">welcome</mm:import>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <title>MMBase Administration<mm:present referid="block"> | <mm:write referid="block" /></mm:present></title>
  <mm:link page="css/admin.css">
    <link rel="stylesheet" href="${_}" type="text/css" />
  </mm:link>
  <mm:link page="/mmbase/style/images/favicon.ico">
    <link rel="icon" href="${_}" type="image/x-icon" />
    <link rel="shortcut icon" href="${_}" type="image/x-icon" />
  </mm:link>
</head>
<body>
<div id="outerheader">
  <div id="header">
	<div id="logo"><a href="."><img src="../style/logo.png" alt="MMBase" width="40" height="50" /></a></div>
	<div id="head">
	  <h1>MMBase Administration</h1>
	  <p>You are logged in as: foo</p><!-- not implemented yet -->
	</div>
  </div>
</div>
<div id="wrap">
<div id="navigation">
<ul>
<mm:functioncontainer>
  <mm:param name="id">mmbase</mm:param>
  <mm:listfunction set="components" name="blockClassification">
    <mm:stringlist referid="_.subTypes" id="cat">
      <mm:link id="link"><mm:param name="category">${cat.name}</mm:param></mm:link>
      <li><a <mm:compare referid="category" value="${cat.name}">class="selected"</mm:compare>href="${link}">${cat.name}</a>

      <mm:compare referid="category" value="${cat.name}">
        
        <c:forEach var="subcat" items="${cat.blocks}">
		  <mm:first><ul></mm:first>
		  <mm:link referids="category">
			<mm:param name="component">${subcat.component.name}</mm:param>
			<mm:param name="block">${subcat.name}</mm:param>
			<li><a title="${subcat.description}" href="${_}">${subcat.name}</a></li>
		  </mm:link>
		  <mm:last></ul></mm:last>
        </c:forEach>
        
      </mm:compare>    
      
      </li>
    </mm:stringlist>
  </mm:listfunction>
</mm:functioncontainer>
</ul>
</div>
<div id="content">
  <div class="padder">
  <mm:present referid="component">
	<h2 class="top"><mm:write referid="block" /></h2>
	<mm:component name="$component" block="$block" />
  </mm:present>
  </div>
</div>
  <div id="footer">
  <ul>
    <li><a href="http://www.mmbase.org">www.mmbase.org</a></li>
    <li><a href="http://www.mmbase.org/license">license</a></li>
    <li><a href="http://www.mmbase.org/contact">contact</a></li>
    <li><a href="http://www.mmbase.org/mmdocs">mmdocs</a></li>
  </ul>
  </div>
</div><!-- /#wrap -->
</body>
</html>
</mm:cloud>
</mm:content>
