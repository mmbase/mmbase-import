<mm:import externid="o" jspvar="ofs_str" vartype="String">0</mm:import>
<mm:import from="cookie" id="conf_max"  externid="my_editors_maxitems" jspvar="conf_max" vartype="String">25</mm:import>
<mm:import from="cookie" id="conf_list" externid="my_editors_typelist" jspvar="conf_list" vartype="String">editable</mm:import>
<mm:import from="cookie" id="maxdays"   externid="my_editors_maxdays">99</mm:import>
<mm:import from="parameters" externid="conf_days" jspvar="conf_days" vartype="String"><mm:write referid="maxdays" /></mm:import>
<% // Set and get some values
int max = Integer.parseInt(conf_max);
int ofs = Integer.parseInt(ofs_str);
%>
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/2000/REC-xhtml1-20000126/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>my_editors - <%= title %></title>
	<link rel="stylesheet" href="my_editors.css" type="text/css" />
	<link href="img/favicon.ico" rel="icon" type="image/x-icon" />
	<link href="img/favicon.ico" rel="shortcut icon" type="image/x-icon" />
</head>
<body bgcolor="#FFFFFF">
<table width="100%" class="top-table" border="0" cellspacing="0" cellpadding="3">
  <tr>
    <td width="50"><a href="index.jsp"><img src="img/mmbase-edit-40.gif" alt="my_editors" width="41" height="40" border="0" hspace="4" vspace="4" /></a></td>
    <td>
	  <div class="top-title"><%= title %></div>
	  <div class="top-links"><a class="top-links" href="index.jsp">home</a> - 
	  <a class="top-links" href="help.jsp">help</a> -
	  <a class="top-links" href="config.jsp">configure</a> -
	  logged on as:  <%= wolk.getUser().getIdentifier() %> (rank: <%= wolk.getUser().getRank() %>) - 
	  <a href="logout.jsp">log out</a> </div>
	</td>
	<td align="right">
	<form action="edit_object.jsp" method="post">
	  <div class="top-left">edit node #<br /><input type="text" name="nr" size="8" maxlength="255" /></div>
	</form>
	</td>
  </tr><tr bgcolor="#CCCCCC"> 
    <td colspan="3"><div class="top-links">
	  <a href="index.jsp">my_editors</a> 
	  <% if (path1 != null) { %>&gt; <a class="top-links" href="index.jsp?ntype=<%= path1 %>">overview <%= path1 %></a> <% } %>
	  <% if (title != null) { %>&gt; <%= title.toLowerCase() %> <% } %>
	</div></td>
  </tr>
</table>
<div style="margin-left: 5px; margin-right: 5px; margin-top: 5px; margin-bottom: 5px;" align="center">