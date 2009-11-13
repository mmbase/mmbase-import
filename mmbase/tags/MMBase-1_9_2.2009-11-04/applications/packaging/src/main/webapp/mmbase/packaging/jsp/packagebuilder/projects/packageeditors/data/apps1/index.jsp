<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="../../../../thememanager/loadvars.jsp" %>
<mm:import externid="main" />
<mm:import externid="sub" />
<mm:import externid="name" />
<mm:import externid="mode" />
<mm:import externid="name" id="project" />
<mm:import externid="package" />
<mm:import externid="package" id="target" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
  <mm:write referid="action">
    <mm:compare value="setDataFileType">
	 <mm:import externid="newtype">saasa</mm:import>
         <mm:function set="mmpb" name="setDataFileType" referids="project,target,newtype" />
    </mm:compare>
  </mm:write>
</mm:present>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="55%">
<tr>
	<th colspan="3">
	 Data/Apps1 Package Settings
	</th>
</tr>

<mm:nodefunction set="mmpb" name="getTypeInfo" referids="project,target">
<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<input type="hidden" name="action" value="setDataFileType" />
	<th width="150">DataSet type</ht>
	<td><select name="newtype">
		<mm:field name="type">
		<option <mm:compare value="depth">selected</mm:compare>>depth
		<option <mm:compare value="table">selected</mm:compare>>table
		<option <mm:compare value="full">selected</mm:compare>>full
		</mm:field>
	</select></td>
	<td width="50"><input type="submit" value="change"></td>
	</form>
</tr>
<mm:field name="type">
<mm:compare value="depth">
<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<th width="150">Data Model</ht>
	<td>
		<mm:field name="depthname" /> 
		( <mm:field name="depthmaintainer" /> / <mm:field name="depthversion" /> )
	</td>
	<td width="50"><input type="submit" value="change"></td>
	</form>
</tr>
<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<th width="150">StartNode (alias)</ht>
	<td>
		<input name="depthalias" value="<mm:field name="depthalias" />" />
	</td>
	<td width="50"><input type="submit" value="change"></td>
	</form>
</tr>

<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<th width="150">StartNode (builder)</ht>
	<td>
		<input name="depthbuilder" value="<mm:field name="depthbuilder" />" />
	</td>
	<td width="50"><input type="submit" value="change"></td>
	</form>
</tr>


<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<th width="150">StartNode (where)</ht>
	<td>
		<input name="depthwhere" value="<mm:field name="depthwhere" />" size="40" />
	</td>
	<td width="50"><input type="submit" value="change"></td>
	</form>
</tr>

</mm:compare>

</mm:field>
</mm:nodefunction>
<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<input type="hidden" name="action" value="setpackagevalue" />
	<th width="150">Export on package</ht>
	 <mm:import id="nid" reset="true">dataexport</mm:import>
	<td><select name="newvalue">
	<mm:function set="mmpb" name="getPackageValue" referids="project,target,nid@name">
	<mm:compare value="true">
	<option selected>true
	<option>false
	</mm:compare>
	<mm:compare value="false">
	<option>true
	<option selected>false
	</mm:compare>
	</mm:function>
	</select>
	</td>
	<input type="hidden" name="newname" value="<mm:write referid="nid" />" />
	<td width="50"><input type="submit" value="change"></td>
	</form>
</tr>
</mm:cloud>
