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
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
<tr>
	<th colspan="3">
	 Data/Apps1 Package Settings
	</th>
</tr>
<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<input type="hidden" name="action" value="setpackagevalue" />
	<th width="150">Data File</ht>
	 <mm:import id="nid" reset="true">datafile</mm:import>
	<td><input name="newvalue" style="width: 99%" value="<mm:function set="mmpb" name="getPackageValue" referids="project,target,nid@name" />"></td>
	<input type="hidden" name="newname" value="<mm:write referid="nid" />" />
	<td width="50"><input type="submit" value="save"></td>
	</form>
</tr>


<tr>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,package,mode" />" method="post">
	<input type="hidden" name="action" value="setpackagevalue" />
	<th width="150">Data Dir</ht>
	 <mm:import id="nid" reset="true">datadir</mm:import>
	<td><input name="newvalue" style="width: 99%" value="<mm:function set="mmpb" name="getPackageValue" referids="project,target,nid@name" />"></td>
	<input type="hidden" name="newname" value="<mm:write referid="nid" />" />
	<td width="50"><input type="submit" value="save"></td>
	</form>
</tr>


</mm:cloud>
