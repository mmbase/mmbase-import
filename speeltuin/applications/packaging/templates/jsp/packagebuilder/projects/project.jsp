<mm:import externid="mode">overview</mm:import>
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="projects/actions.jsp" />
</mm:present>

<mm:url id="baseurl" page="index.jsp" referids="main" write="false" />

<!-- end action check -->
<mm:compare referid="mode" value="overview">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="4">
		Bundle Targets
	</th>
</tr>
<tr>
	<th>
	Target	
	</th>
	<th>
	Type	
	</th>
	<th>
	Path	
	</th>
	<th width="15">
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpb" name="getProjectBundleTargets" referids="name">
<tr>
		<td width="200">
			<mm:field name="name">
			<A HREF="<mm:url referid="baseurl" referids="name"><mm:param name="sub" value="projectbundle" /><mm:param name="bundle" value="$_" /></mm:url>"><mm:field name="name" /></a>
			</mm:field>
		</td>
		<td>
			<mm:field name="type" />	
		</td>
		<td>
			<mm:field name="path" />	
		</td>
		<td width="15">
			<mm:field name="name">
			<A HREF="<mm:url referid="baseurl" referids="name"><mm:param name="sub" value="projectbundle" /><mm:param name="bundle" value="$_" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</mm:field>
		</td>
</tr>
 </mm:nodelistfunction>
<tr>
 <td colspan="6" align="right">
 Add Bundle Target
 <A HREF="<mm:url referid="baseurl" referids="sub,name"><mm:param name="mode" value="addbundletarget" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td> 
</tr>   



</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="4">
		Package Targets
	</th>
</tr>
<tr>
	<th>
	Target	
	</th>
	<th>
	Type	
	</th>
	<th>
	Path	
	</th>
	<th>
	&nbsp;
	</th>
</tr>

<mm:nodelistfunction set="mmpb" name="getProjectPackageTargets" referids="name">
<TR>
		<td width="200">
			<mm:field name="name">
			<A HREF="<mm:url referid="baseurl" referids="name"><mm:param name="sub" value="projectpackage" /><mm:param name="package" value="$_" /></mm:url>"><mm:field name="name" /></a>
			</mm:field>
		</td>
		<td>
			<mm:field name="type" />	
		</td>
		<td>
			<mm:field name="path" />	
		</td>
		<td width="15">
			<mm:field name="name">
			<A HREF="<mm:url referid="baseurl" referids="name"><mm:param name="sub" value="projectpackage" /><mm:param name="package" value="$_" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</mm:field>
		</td>
</tr>
 </mm:nodelistfunction>
<tr>
 <td colspan="6" align="right">
 Add Package Target
 <A HREF="<mm:url referid="baseurl" referids="sub,name"><mm:param name="mode" value="addpackagetarget" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td>
</tr>
</table>
</mm:compare>
<mm:compare referid="mode" value="addbundletarget">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="75%">   
<tr>    
        <th width="200">
      	Target Name 
        </th>
        <th>    
	Bundle Type
	</th>
        <th>    
	Packaging file
	</th>
</tr>
<form action="<mm:url referid="baseurl" referids="sub,name" />" method="post">
<input type="hidden" name="action" value="addbundletarget" />
<tr>    
        <td width="200">
	<input style="width: 98%" name="newtargetname" value="[auto]">	
        </td>
	<td width="100">
	<select name="newtargettype">
		<option>bundle/basic
	</select>
	</td>
        <td>    
	<input style="width: 99%" name="newtargetpath" value="[auto]">	
	</td>
</tr>
<tr>    
        <td colspan="3" align="middle">
	<table width="50%"><tr>
	<td align="middle"><input type="submit" value="Add"></form></td>
	<form action="<mm:url referid="baseurl" referids="sub,name" />" method="post">
	<td align="middle"><input type="submit" value="Cancel"></form></td>
	</tr></table>
</tr>
</table>
</mm:compare>

<mm:compare referid="mode" value="addpackagetarget">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="75%">   
<tr>    
        <th width="200">
      	Target Name 
        </th>
        <th>    
	Package Type
	</th>
        <th>    
	Packaging file
	</th>
</tr>
<form action="<mm:url referid="baseurl" referids="sub,name" />" method="post">
<input type="hidden" name="action" value="addpackagetarget" />
<tr>    
        <td width="200">
	<input style="width: 98%" name="newtargetname" value="[auto]">	
        </td>
	<td width="100">
	<select name="newtargettype">
		<option>display/html
		<option>cloud/model
		<option>data/apps1
		<option>java/jar
		<option>config/basic
		<option>function/set
	</select>
	</td>
        <td>    
	<input style="width: 99%" name="newtargetpath" value="[auto]">	
	</td>
</tr>
<tr>    
        <td colspan="3" align="middle">
	<table width="50%"><tr>
	<td align="middle"><input type="submit" value="Add"></form></td>
	<form action="<mm:url referid="baseurl" referids="sub,name" />" method="post">
	<td align="middle"><input type="submit" value="Cancel"></form></td>
	</tr></table>
</tr>
</table>
</mm:compare>
