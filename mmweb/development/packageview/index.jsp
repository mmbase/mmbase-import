<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" />
   <TITLE>MMBase Package Builder</TITLE>
</HEAD>
<mm:import externid="main" >bundles</mm:import>


<body>
<!-- first the selection part -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">

<tr>

		<th COLSPAN="8">
		 MMBase Packages
		</th>
</tr>
<tr>
		<td COLSPAN="8">
		<br />
		<center>This is a temporary package overview for the MMBase. In time it will be replaced with the package manager from the apps2 project (not done yet).<br /><br /> As a result this page is maintained by hand.
		So if your package is not on this list and you feel it should be or you have extra<br /> information on your package mail me at daniel@mmbase.org and I will update this list.
		<br />
		<br />
		</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">
<tr>

		<th>
		<b>******* <a href="http://packages.mmbase.org/mmbase/packagemanager/public"><font color="black">click here for the preview / new packaging system (1.8) with extra applications</font></a> *******</b>
		</th>
</tr>
</table>
<br />
<br />
<mm:write referid="main">
 <mm:compare value="bundles"><%@ include file="bundles.jsp" %></mm:compare>
 <mm:compare value="bundle"><%@ include file="bundle.jsp" %></mm:compare>
</mm:write>


<br />
<br />
</BODY>
</HTML>
