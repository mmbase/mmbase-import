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
<%@ include file="help/main.jsp" %>
<mm:nodefunction set="mmpb" name="getProjectInfo" referids="name">
	<mm:import id="dir"><mm:field name="dir" /></mm:import>
</mm:nodefunction>

<mm:import id="modelfilename"><mm:write referid="dir" /><mm:write referid="prefix" /></mm:import>


<mm:compare referid="editor" value="neededbuilders">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="25%">
<tr><th colspan="5">Builders in this model</ht></tr>
<tr><th>Name</ht><th>Maintainer</th><th>Version</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getNeededBuilders" referids="modelfilename">
<mm:context>
<tr>
	<td><mm:field name="name" id="oldbuilder" /></td>
	<td><mm:field name="maintainer" id="oldmaintainer" /></td>
	<td><mm:field name="version" id="oldversion" /></td>
	<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,oldbuilder@builder,mode" />" method="post">
	<input type="hidden" name="editor" value="builderedit" />
	<td width="50"><input type="submit" value="edit"></td>
	</form>
	<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,mode,name,prefix@modelfilename,oldbuilder,oldmaintainer,oldversion" />" method="post">
	<input type="hidden" name="editor" value="confirmdeleteneededbuilder" />
	<td width="50"><input type="submit" value="delete"></td>
	</form>
</tr>
</mm:context>
</mm:nodelistfunction>
<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename" />" method="post">
<input type="hidden" name="action" value="addneededbuilder" />
<tr><td><input name="newbuilder" size="20"><td><input name="newmaintainer" size="15"></td><td><input name="newversion" size="3"></td></td><td><input type="submit" value="add"></td><td></td>
</form>
</table>
</mm:compare>


<mm:compare referid="editor" value="neededreldefs">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="45%">
<tr><th colspan="8">Relation Types in this model</ht></tr>
<tr><th>Source</ht><th>Target</th><th>Direction</th><th>GuiSourceName</th><th>GuiTargetName</th><th>BuilderName</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getNeededRelDefs" referids="modelfilename">
<mm:context>
<tr>
	<td><mm:field name="source" id="oldsource" /></td>
	<td><mm:field name="target" id="oldtarget" /></td>
	<td><mm:field name="direction" id="olddirection" /></td>
	<td><mm:field name="guisourcename" id="oldguisourcename" /></td>
	<td><mm:field name="guitargetname" id="oldguitargetname" /></td>
	<td><mm:field name="buildername" id="oldbuilder" /></td>
	<form action="" method="post">
	<td width="50"><input type="submit" value="edit"></td>
	</form>
	<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor,oldsource,oldtarget,olddirection,oldguisourcename,oldguitargetname,oldbuilder" />" method="post">
        <input type="hidden" name="editor" value="confirmdeleteneededreldef" />
	<td width="50"><input type="submit" value="delete"></td>
	</form>
</tr>
</mm:context>
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
<tr><th colspan="5">Possible Relations in this model</ht></tr>
<tr><th>From</ht><th>To</th><th>Type</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getAllowedRelations" referids="modelfilename">
<mm:context>
<tr>
	<td><mm:field name="from" id="oldfrom" /></td>
	<td><mm:field name="to" id="oldto" /></td>
	<td><mm:field name="type" id="oldtype" /></td>
	<form action="" method="post">
	<td width="50"><input type="submit" value="edit"></td>
	</form>
	<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor,oldfrom,oldto,oldtype" />" method="post">
	<input type="hidden" name="editor" value="confirmdeleteallowedrelation" />
	<td width="50"><input type="submit" value="delete"></td>
	</form>
</tr>
</mm:context>
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


<mm:compare referid="editor" value="confirmdeleteneededbuilder">
<mm:import externid="oldbuilder" />
<mm:import externid="oldmaintainer" />
<mm:import externid="oldversion" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 130px;" width="35%">
<tr><th colspan="2">Delete NeededBuilder : <mm:write referid="oldbuilder" /></ht></tr>
<tr>
   <form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,oldbuilder,oldmaintainer,oldversion" />" method="post">
	<td align="middle" height="35"><input type="hidden" name="action" value="deleteneededbuilder" /><input type="submit" value="Yes, Delete" /></td>
   </form></td>
   <form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename" />" method="post"><td align="middle"><input type="submit" value="Oops, No" /></td></tr>
</table>
</mm:compare>

<mm:compare referid="editor" value="confirmdeleteneededreldef">
<mm:import externid="oldsource" />
<mm:import externid="oldtarget" />
<mm:import externid="olddirection" />
<mm:import externid="oldguisourcename" />
<mm:import externid="oldguitargetname" />
<mm:import externid="oldbuilder" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 130px;" width="35%">
<tr><th colspan="2">Delete NeededRelDef : <mm:write referid="oldbuilder" /></ht></tr>
<tr>  
   <mm:remove referid="editor" />
   <mm:import id="editor">neededreldefs</mm:import>
   <form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,oldbuilder,oldsource,oldtarget,olddirection,oldguisourcename,oldguitargetname,editor" />" method="post">
	<td align="middle" height="35"><input type="hidden" name="action" value="deleteneededreldef" /><input type="submit" value="Yes, Delete" /></td>
   </form></td>
   <form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor" />" method="post"><td align="middle"><input type="submit" value="Oops, No" /></td></tr>
</table>
</mm:compare>


<mm:compare referid="editor" value="confirmdeleteallowedrelation">
<mm:import externid="oldfrom" />
<mm:import externid="oldto" />
<mm:import externid="oldtype" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 130px;" width="35%">
<tr><th colspan="2">Delete Allowed Relation : <mm:write referid="oldfrom" /> <mm:write referid="oldto" /> <mm:write referid="oldtype" /> </ht></tr>
<tr>  
   <mm:remove referid="editor" />
   <mm:import id="editor">allowedrelations</mm:import>
   <form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,oldfrom,oldto,oldtype,editor" />" method="post">
	<td align="middle" height="35"><input type="hidden" name="action" value="deleteallowedrelation" /><input type="submit" value="Yes, Delete" /></td>
   </form></td>
   <form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor" />" method="post"><td align="middle"><input type="submit" value="Oops, No" /></td></tr>
</table>
</mm:compare>


<mm:compare referid="editor" value="builderedit">
<mm:import externid="builder" />
<mm:import externid="language">en</mm:import>
<form action="" method="post">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="70%">
<tr><th colspan="4">Builder edit</ht></tr>
<mm:nodefunction set="mmpb" name="getNeededBuilderInfo" referids="modelfilename,builder,language">
<tr><th>name</th><th>maintainer</th><th>version</th><th>extends</th></tr>
<tr>
	<td><input name="newbuilder" value="<mm:field name="name" />" size="20"></td>
	<td><input name="newmaintainer" value="<mm:field name="maintainer" />" size="20"></td>
	<td><input name="newversion" value="<mm:field name="version" />" size="3"></td>
	<td><input name="newextends" value="<mm:field name="extends" />" size="20"></td>
</tr>
<tr>
	<td colspan="5" align="middle" ><input type="submit" value="save" /></td>
</tr>
</table>
</form>


<form action="" method="post">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 15px;" width="70%">
<tr><th>status</th><th>searchage</th><th>classname</th></tr>
<tr>
	<td><input name="newstatus" value="<mm:field name="status" />" size="10"></td>
	<td><input name="newsearchage" value="<mm:field name="searchage" />" size="4"></td>
	<td><input name="newclassname" value="<mm:field name="classname" />" size="45"></td>
</tr>
<tr>
	<td colspan="5" align="middle" ><input type="submit" value="save" /></td>
</tr>
</table>
</form>

<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor,builder" />" method="post">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 15px;" width="70%">
<tr><th>language</th><th>singular gui name</th><th>plural gui name</th></tr>
<tr>
	<td><select name="language" onchange="submit()">
		<option><mm:write referid="language" />
		<option>en
		<option>nl
		<option>fr
		<option>eo
	</select></td>
</form>
<form action="" method="post">
	<td><input name="newsingularname" value="<mm:field name="singularname" />" size="30"></td>
	<td><input name="newpluralname" value="<mm:field name="pluralname" />" size="30"></td>
</tr>
<tr>
	<td colspan="5" align="middle" ><input type="submit" value="save" /></td>
</tr>
</table>
</form>

<form action="<mm:url page="editcloud.jsp" referids="main,sub,id,package,name,prefix@modelfilename,editor,builder" />" method="post">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 15px;" width="70%">
<tr><th>Description</th><th>language</th></tr>
<tr>
	<td><select name="language" onchange="submit()">
		<option><mm:write referid="language" />
		<option>en
		<option>nl
		<option>fr
		<option>eo
	</select></td>
</form>
<form action="" method="post">
<td><textarea name="newdescription" rows="5" cols="60"><mm:field name="description" /></textarea></td>
</tr>
<tr>
	<td colspan="5" align="middle" ><input type="submit" value="save" /></td>
</tr>
</table>
</form>

<form action="" method="post">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 15px;" width="70%">
<tr><th>pos</th><th>field</th><th>type</th><th>status</th><th>size</th><th></th><th></th></tr>
<mm:nodelistfunction set="mmpb" name="getNeededBuilderFields" referids="modelfilename,builder,language">
<mm:context>
<tr>
	<td><mm:field name="dbpos" /></td>
	<td><mm:field name="dbname" /></td>
	<td><mm:field name="dbtype" id="dbtype"/></td>
	<td><mm:field name="dbstate" /></td>
	<td><mm:field name="dbsize"><mm:compare value="-1" inverse="true"><mm:field name="dbsize" /></mm:compare></mm:field></td>
	<td><input type="submit" value="edit" /></td>
	<td><input type="submit" value="delete" /></td>
</tr>
</mm:context>
</mm:nodelistfunction>
<tr>
	<td></td>
	<td><input name="newbuilder" size="20" /></td>
	<td><select name="newtype">
		<option>STRING
		<option>INTEGER
		<option>LONG
		<option>BYTES
	<select>
	</td>
	<td><input name="newsize" size="7" /></td>
	<td><input type="submit" value="add" /></td>
	<td></td>
</tr>
</mm:nodefunction>
</table>
</form>

</mm:compare>

</mm:cloud>
<br />
<br />
</BODY>
</HTML>
