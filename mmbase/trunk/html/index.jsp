<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<head>
<title>Welcome to MMBase</title>
<style>
<!--
 body {  background-color: white;
         color:42BDAD;
 	   font-size:medium;}
 a:link { color: #42BDAD; }
 a:visited { color: #AAAAAA; }
 a:active { color: #AAAAAA; }

 th {  background-color: #44BDAD;
       color:#00425A;
	 font-size:medium;
	 font-weight:bold; }
 th.main {  background-color: #44BDAD;
       color:#00425A;
	 font-size:large;
	 font-weight:bold; }
 td    {  background-color: #00425A;
         color:#44BDAD;
	 font-size:medium;
	 font-weight:normal; }
 td.link {  background-color: #44BDAD;
       color:#00425A;
	 font-size:medium;
	 font-weight:normal;
	 width:14; }
-->
</style>
</head>
<body >
<table align="center" width="97%" cellspacing="1" cellpadding="3" border="0">
<tr>
	<th class="main" colspan="3">Welcome to MMBase</th>
</tr>
<tr>
<td colspan="3">
      You are running <mm:include page="/version" /><br />
<p>This software is OSI Certified Open Source Software.<br />
OSI Certified is a certification mark of the Open Source Initiative.</p>
<p>MMBase has been build under the <a href="mpl-1.0.html">Mozilla Public License, Version 1.0</a></p>
&nbsp;
</td>
</tr>

<tr>
	<th>Section</th>
	<th colspan="2">Description</th>
</tr>

<tr>
	<td>MMBase Demos</td>
	<td>
	   Examples of MMBase functionality. Includes among other things a news application, an alternate editor,
           and the MMBase editwizards.
	</td>
	<td class="link" >
		<a href="<mm:url page="mmexamples/" />"><img alt=">" src="mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>Taglib Demo</td>
	<td>
	   Demonstrates the use of the MMBase taglib, a library of useful tags with which you can
           retrieve data form MMBase in your web pages.
	</td>
	<td class="link" >
		<a href="<mm:url page="mmexamples/taglib/whatistaglib.jsp" />"><img alt=">" src="mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>JSP Editors</td>
	<td>
	   Generic editors for adding and changing objects in MMBase.<br />
           These editors are mostly intended for experienced users.
	</td>
	<td class="link" >
		<a href="<mm:url page="mmeditors/jsp/" />"><img alt=">" src="mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>Admin pages</td>
	<td>
	   The administrator's pages of MMBase allow you to retrieve info and configure modules, builders, the cache,
           servers, and databases.
	</td>
	<td class="link" >
		<a href="<mm:url page="mmadmin/jsp/" />"><img alt=">" src="mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>www.mmbase.org</td>
	<td>
	   Link to the MMBase website.
	</td>
	<td class="link" >
		<a href="http://www.mmbase.org"><img alt=">" src="mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>


<tr><td colspan="3">&nbsp;</td></tr>
</table>
</body>
</html>

