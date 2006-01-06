<mm:import externid="mode">overview</mm:import>
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="projects/actions.jsp" />
</mm:present>

<mm:url id="baseurl" page="index.jsp" referids="main" write="false" />

<!-- end action check -->
<mm:compare referid="mode" value="overview">

<!-- start -->
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<tr>
<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Project Information
	</th>
	</tr>
 	<tr><th>Name</th><td><mm:write referid="name" /></td>
</table>
</td>

<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Project Actions
	</th>
	</tr>
        <tr><th>Change Project Info</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name">
            <mm:param name="mode" value="changeprojectinfo" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
       </tr> 
        <tr><th>Delete Project</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name">
            <mm:param name="mode" value="deleteview" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
       </tr> 
</table>
</td>
</tr>
</table>
<!-- end -->
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
			<A HREF="<mm:url referid="baseurl" referids="name"><mm:param name="sub" value="projectbundle" /><mm:param name="bundle" value="$_" /></mm:url>"><mm:field name="syntaxerrors"><mm:compare value="true"><font color="red"><mm:field name="name" /></font></mm:compare><mm:compare value="false"><mm:field name="name" /></mm:compare></mm:field></a>
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
			<A HREF="<mm:url referid="baseurl" referids="name"><mm:param name="sub" value="projectpackage" /><mm:param name="package" value="$_" /></mm:url>"><mm:field name="syntaxerrors"><mm:compare value="true"><font color="red"><mm:field name="name" /></font></mm:compare><mm:compare value="false"><mm:field name="name" /></mm:compare></mm:field></a>
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

<mm:compare referid="mode" value="changeprojectinfo">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="65%">
<tr>
	<th colspan="3">
	Project Name and Path
	</th>
</tr>
	<form action="<mm:url page="index.jsp" referids="main" />" method="post">
	<mm:nodefunction set="mmpb" name="getProjectInfo" referids="name">
	<input type="hidden" name="action" value="changeprojectsettings" />
	<input type="hidden" name="name" value="<mm:write referid="name" />" />
	<tr>
	<th width="100">Name</ht>
	<td><input name="newname" style="width: 45%" value="<mm:field name="name" />"><img src="<mm:write referid="image_help" />"  valign="middle" title="Name the project for example 'myproject'" /></td>
	</tr>
	<tr>
	<th width="100">Path</ht>
	<td><input name="newpath" style="width: 90%" value="<mm:field name="path" />"><img src="<mm:write referid="image_help" />"  valign="middle" title="path of the packaging.xml file,example /usr/me/myproject/packaging.xml" /></td>
	</tr>
	</mm:nodefunction>
	<tr>
		<td height="30" colspan="3" align="middle">
		<table width="50%">
		<tr><td align="middle">
		<input type="submit" value="Save">
		</form>
		</td>
		<form action="<mm:url page="index.jsp" referids="main,sub,name" />" method="post">
		<td align="middle">
		<input type="submit" value="Cancel">
		</form>
		</td>
		</tr>
		</table>
		</td>
	</tr>
</table>
</mm:compare>
<mm:compare referid="mode" value="deleteview">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="30%">  
<tr>    
<form action="<mm:url page="index.jsp" referids="main,name" />" method="post">
        <th colspan="2">Delete this project </th>
	<input type="hidden" name="action" value="delproject" />
<tr><td height="30"><center><input type="submit" value="Yes, delete"></center></td>
	</form>
	<form action="<mm:url page="index.jsp" referids="main,sub,name" />" method="post">
	<td <center><input type="submit" value="No, Oops"></center></td>
	</form>
	</tr>
</td>
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
	<input style="width: 80%" name="newtargetname" value="[auto]"><img src="<mm:write referid="image_help" />"  valign="middle" title="target name, leave on [auto] unless you have more of the same type in this project" />
        </td>
	<td width="140">
	<select name="newtargettype">
		<option>bundle/basic
	</select>
<img src="<mm:write referid="image_help" />"  valign="middle" title="type of bundle target you want to add to this project" />
	</td>
        <td>    
	<input style="width: 85%" name="newtargetpath" value="[auto]"><img src="<mm:write referid="image_help" />"  valign="middle" title="path of the packaging.xml file of this target when only one of each type leave on [auto],example /usr/me/myproject/packaging/basicbundle.xml" />
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
	<input style="width: 80%" name="newtargetname" value="[auto]">	
<img src="<mm:write referid="image_help" />"  valign="middle" title="target name, leave on [auto] unless you have more of the same type in this project" />
        </td>
	<td width="140">
	<select name="newtargettype">
		<option>display/html
		<option>cloud/model
		<option>data/apps1
		<option>java/jar
		<option>config/basic
		<option>function/set
	</select>
	<img src="<mm:write referid="image_help" />"  valign="middle" title="type of bundle target you want to add to this project" />
	</td>
        <td>    
	<input style="width: 86%" name="newtargetpath" value="[auto]"><img src="<mm:write referid="image_help" />"  valign="middle" title="path of the packaging.xml file of this target when only one of each type leave on [auto],example /usr/me/myproject/packaging/html_package.xml" />
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
