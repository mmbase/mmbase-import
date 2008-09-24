<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<HTML>   
<HEAD>
<mm:cloud>
   <link rel="stylesheet" type="text/css" href="css/default.css" />
   <TITLE>PrincipeTracker</TITLE>
</HEAD>
<mm:import externid="main" />
<mm:import externid="sub" >none</mm:import>
<mm:import externid="principleset">Principle.default</mm:import>

<mm:notpresent referid="main">
  <mm:import id="main" reset="true" >principlesets</mm:import>
  <mm:node referid="principleset" notfound="skip">
     <mm:import id="main" reset="true" >principles</mm:import>
   </mm:node>
</mm:notpresent>


<body>
<!-- first the selection part -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">

<tr>

		<th COLSPAN="8">
		 PrincipeTracker - version 0.3
		</th>
</tr>
</table>

<%@ include file="headers/main.jsp" %>
<mm:write referid="main">
 <mm:compare value="principles"><%@ include file="principles/index.jsp" %></mm:compare>
 <mm:compare value="principlesets"><%@ include file="principlesets/index.jsp" %></mm:compare>
 <mm:compare value="importexport"><%@ include file="importexport/index.jsp" %></mm:compare>
</mm:write>


</mm:cloud>
<br />
<br />
</BODY>
</HTML>
