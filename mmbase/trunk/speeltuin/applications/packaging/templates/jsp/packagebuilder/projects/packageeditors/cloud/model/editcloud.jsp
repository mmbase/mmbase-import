<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" rank="administrator">
<%@ include file="../../../../thememanager/loadvars.jsp" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <TITLE>MMBase Cloud Editor</TITLE>
</HEAD>
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>

<mm:import externid="main" >projects</mm:import>
<mm:import externid="sub" >none</mm:import>
<mm:import externid="id" >none</mm:import>
<mm:import externid="help" >on</mm:import>
<mm:import externid="name" />
<mm:import externid="package" />
<mm:import externid="modelfilename" id="prefix" />
<mm:import externid="editor">neededbuilders</mm:import>




<body>
<!-- first the selection part -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">

<tr>

		<th COLSPAN="8">
		 MMBase Cloud Editor - version 0.1
		</th>
<%@ include file="headers/main.jsp" %> 
</tr>
</table>
<mm:nodefunction set="mmpb" name="getProjectInfo" referids="name">
	<mm:import id="dir"><mm:field name="dir" /></mm:import>
</mm:nodefunction>

<mm:import id="modelfilename"><mm:write referid="dir" /><mm:write referid="prefix" /></mm:import>


<mm:compare referid="editor" value="neededbuilders">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="25%">
<tr><th colspan="5">Needed Builders</ht></tr>
<tr><th>Name</ht><th>Maintainer</th><th>Version</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getNeededBuilders" referids="modelfilename">
<tr>
	<td><mm:field name="name" /></td>
	<td><mm:field name="maintainer" /></td>
	<td><mm:field name="version" /></td>
	<form action="" method="post">
	<td width="50"><input type="submit" value="edit"></td>
	</form>
	<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename" />" method="post">
	<td width="50"><input type="submit" value="delete"></td>
	</form>
</tr>
</mm:nodelistfunction>
<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename" />" method="post">
<input type="hidden" name="action" value="addneededbuilder" />
<tr><td><input name="newbuilder" size="20"><td><input name="newmaintainer" size="15"></td><td><input name="newversion" size="3"></td></td><td><input type="submit" value="add"></td><td></td>
</form>
</table>
</mm:compare>


<mm:compare referid="editor" value="neededreldefs">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="45%">
<tr><th colspan="8">Needed RefDefs</ht></tr>
<tr><th>Source</ht><th>Target</th><th>Direction</th><th>GuiSourceName</th><th>GuiTargetName</th><th>BuilderName</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getNeededRelDefs" referids="modelfilename">
<tr>
	<td><mm:field name="source" /></td>
	<td><mm:field name="target" /></td>
	<td><mm:field name="direction" /></td>
	<td><mm:field name="guisourcename" /></td>
	<td><mm:field name="guitargetname" /></td>
	<td><mm:field name="buildername" /></td>
	<form action="" method="post">
	<td width="50"><input type="submit" value="edit"></td>
	</form>
	<form action="" method="post">
	<td width="50"><input type="submit" value="delete"></td>
	</form>
</tr>
</mm:nodelistfunction>

<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor" />" method="post">
<tr>
<input type="hidden" name="action" value="addneededreldef" />
<td><input name="newsource" size="12"></td>
<td><input name="newtarget" size="12"></td>
<td><select name="newdirection">
	<option>bidirectional
	<option>unidirectional
	</select></td>
<td><input name="newguisourcename" size="12"></td>
<td><input name="newguitargetname" size="12"></td>
<td><select name="newbuilder">
<mm:nodelistfunction set="mmpb" name="getNeededBuilders" referids="modelfilename">
	<option><mm:field name="name" />
</mm:nodelistfunction>
	</select></td>
<td><input type="submit" value="add"></td><td></td>
</table>
</mm:compare>

<mm:compare referid="editor" value="allowedrelations">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="45%">
<tr><th colspan="5">AllowedRelations</ht></tr>
<tr><th>From</ht><th>To</th><th>Type</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getAllowedRelations" referids="modelfilename">
<tr>
	<td><mm:field name="from" /></td>
	<td><mm:field name="to" /></td>
	<td><mm:field name="type" /></td>
	<form action="" method="post">
	<td width="50"><input type="submit" value="edit"></td>
	</form>
	<form action="" method="post">
	<td width="50"><input type="submit" value="delete"></td>
	</form>
</tr>
</mm:nodelistfunction>
<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor" />" method="post">
<input type="hidden" name="action" value="addallowedrelation" />
<tr>
<td><select name="newfrom">
<mm:nodelistfunction set="mmpb" name="getNeededBuilders" referids="modelfilename">
	<option><mm:field name="name" />
</mm:nodelistfunction>
	</select></td>
<td><select name="newto">
<mm:nodelistfunction set="mmpb" name="getNeededBuilders" referids="modelfilename">
	<option><mm:field name="name" />
</mm:nodelistfunction>
	</select></td>
<td><select name="newtype">
	<option>insrel
	<option>posrel
	<option>rolerel
	</select></td><td>
<input type="submit" value="add"></td><td></td>
</form>
</table>
</mm:compare>


</mm:cloud>
<br />
<br />
</BODY>
</HTML>
