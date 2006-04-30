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
		 MMBase Applications
		</th>
</tr>
</table>

<mm:write referid="main">
 <mm:compare value="bundles"><%@ include file="bundles.jsp" %></mm:compare>
 <mm:compare value="bundle"><%@ include file="bundle.jsp" %></mm:compare>
</mm:write>


<br />
<br />
</BODY>
</HTML>
