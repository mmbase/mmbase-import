<mm:import externid="bundle" />

<mm:import externid="mode">none</mm:import>
<mm:import externid="logid">-1</mm:import>
<mm:import externid="parentlog">-1</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="projects/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:import id="project" externid="name" />
<mm:import id="target" externid="bundle" />

<mm:nodelistfunction set="mmpb" name="haveTargetLog" referids="project,target">
        <mm:import id="havelog"><mm:field name="log" /></mm:import>
	<mm:import id="state"><mm:field name="state" /></mm:import>
	<mm:import id="createprogressbarvalue"><mm:field name="createprogressbarvalue" /></mm:import>
	<mm:import id="createsubprogressbarvalue"><mm:field name="createsubprogressbarvalue" /></mm:import>
</mm:nodelistfunction>


<mm:compare referid="state" value="creating">

<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<tr>
<th colspan="2">progress on creating</th>
</tr>
<tr>
<td colspan="2">
When the create is done you can check state and log to see if all went well
</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<tr>
<td width="<mm:write referid="createprogressbarvalue" />%" style="background-color: #0000aa">&nbsp;</td><td>&nbsp;</td>
</tr>
</td></tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<tr>
<td width="<mm:write referid="createsubprogressbarvalue" />%" style="background-color: #00aa00">&nbsp;</td><td>&nbsp;</td>
</tr>
</td></tr>
</table>

<script language="JavaScript">
<!--

function doLoad()
{
    // the timeout value should be the same as in the "refresh" meta-tag
    setTimeout( "refresh()", 1000 );
}

function refresh()
{
    window.location.href ='index.jsp?main=<mm:write referid="main" />&sub=<mm:write referid="sub" />&name=<mm:write referid="name" />&bundle=<mm:write referid="bundle" />&mode=<mm:write referid="mode" />&newmsg=created';
}
//-->
</script>
</mm:compare>

<mm:compare referid="state" value="creating" inverse="true">
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<tr>
<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Target Information
	</th>
	</tr>
	  <mm:nodefunction set="mmpb" name="getProjectTargetInfo" referids="project,target">
 	    <tr><th>Target</th><td><mm:field name="name" /></td>
 	    <tr><th>Bundle</th><td><mm:field name="bundlename"><mm:compare value=""><font color="red">missing name</font></mm:compare></mm:field><mm:field name="bundlename" /></td>
 	    <tr><th>Type</th><td><mm:field name="type" /></td>
 	    <tr><th>Maintainer</th><td><mm:field name="maintainer"><mm:compare value=""><font color="red">missing name</font></mm:compare></mm:field><mm:field name="maintainer" /></td>
            <mm:import id="syntaxerrors"><mm:field name="syntaxerrors" /></mm:import>
	  </mm:nodefunction>
</table>
</td>

<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Target Actions
	</th>
	</tr>
        <tr><th>Bundle New Version</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle">
	    <mm:param name="mode" value="createview" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
      </tr> 
        <tr><th>Delete this target</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle">
	    <mm:param name="mode" value="deleteview" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
      </tr> 
        <tr><th>Publish Mode</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle">
	    <mm:param name="mode" value="publishview" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
      </tr> 
      <mm:compare referid="havelog" value="true">  
    		<tr><th>View log</th>
		<td>
           	<A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle">
	    	<mm:param name="mode" value="log" />
	   	</mm:url>">
	   	<IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td></tr>
       </mm:compare>
        <tr><th><mm:compare referid="syntaxerrors" value="true"><font color="red">Change Bundle Meta Info</font></mm:compare><mm:compare referid="syntaxerrors" value="false">Change Bundle Meta Info</font></mm:compare></th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle">
	    <mm:param name="mode" value="bundleinfo" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
      </tr> 


        <tr><th>Included Packages</th>
         <td>
           <A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle">
	    <mm:param name="mode" value="includedpackages" />
	   </mm:url>">
	   <IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
        </td>
      </tr> 


</table>
</td>
</tr>
</table>
</mm:compare>

<mm:write referid="mode">
<mm:compare value="createview">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">  
<mm:compare referid="syntaxerrors" value="false">
<mm:nodefunction set="mmpb" name="getProjectTargetInfo" referids="project,target">
<form action="<mm:url page="index.jsp" referids="main,sub,name,target,bundle"><mm:param name="mode" value="log" /></mm:url>" method="post">
	<tr>    
        <th colspan="4">Create a new version of this bundle </th>
</tr><td height="25" colspan="2">Last created version </td><td colspan="2"> <mm:field name="lastversion" /> (<mm:field name="lastdate" />)</td></tr>
<tr><td colspan="2">New Version</td><td colspan="2"><input name="newversion" size="4" value="<mm:field name="nextversion" />"></td></tr>
	</tr>
	<mm:field name="publishprovider">
	<mm:compare value="" inverse="true">
	<td height="25" colspan="2">Publish server 
		<select name="publishprovider">
			<mm:import id="tmpfield"><mm:field name="publishprovider" /></mm:import>
			<mm:nodelistfunction set="mmpm" name="getProviders">
			<mm:field name="method">
			<mm:compare value="disk" inverse="true">
			<option <mm:field name="name"><mm:compare referid2="tmpfield">selected</mm:compare></mm:field> value="<mm:field name="name" />"><mm:field name="name" />
			</mm:compare>
			</mm:field>
			</mm:nodelistfunction>
		</select></td><td>
	<select name="publishstate">
		<mm:field name="publishstate">
		<mm:compare value="true">
		<option value="true" selected>active
		<option value="false">inactive
		</mm:compare>
		<mm:compare value="false">
		<option value="true">active
		<option value="false" selected>inactive
		</mm:compare>
		</mm:field>
	</select></td>
	<td>sharepassword : <input type="password" name="publishsharepassword" value="<mm:field name="publishsharepassword" />" /></td></tr>
	</mm:compare>
	</mm:field>
	<input type="hidden" name="action" value="packagetarget" />
        <tr><th>Included package</th><th>Included version</th><th>Use latest</th><th>Create new</th></tr>
        <mm:nodelistfunction set="mmpb" name="getTargetIncludedPackages" referids="project,target">
	<mm:context>
        <tr><td><mm:field name="name" /></td><td><mm:field name="version" id="version" /></td><td><mm:field name="lastversion"><mm:compare referid2="version" inverse="true"><mm:field name="lastversion" /><input type="checkbox" name="latest" value="<mm:field name="id" />" value="<mm:field name="id" />" checked></mm:compare></mm:field></td>

	<td height="20">
		<mm:field name="target">
		<mm:compare value="" inverse="true">
		<mm:field name="nextversion" /><input type="checkbox" name="createnew" value="<mm:field name="target" />" <mm:field name="forcednew"><mm:compare value="true">checked</mm:compare></mm:field> /> <mm:field name="target" />
		</mm:compare>
		<mm:compare value="">
		external
		</mm:compare>
		</mm:field>
	</td>

	</tr>
	</mm:context>
	</mm:nodelistfunction>
<tr><td colspan="4" height="30"><center><input type="submit" value="create new version"></center></td></tr>
</td>
</form>
</mm:nodefunction>
</mm:compare>
<mm:compare referid="syntaxerrors" value="true">
<tr><th>Packaging feedback</th></tr>
<tr><td><p /><b><center><font color="red">Packaging refused, fix syntax errors first.</font></center></b><p/ ></td></tr>
</mm:compare>
</table>
</mm:compare>
<mm:compare value="log">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr>
	<th COLSPAN="5" align="left">
	<mm:compare referid="logid" value="-1" inverse="true">
        <A HREF="<mm:url page="index.jsp" referids="main,sub,bundle,name,mode"><mm:param name="logid"><mm:write referid="parentlog" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowleft" />" BORDER="0" ALIGN="left"></A>
	</mm:compare>
	Package log of this Target (<mm:write referid="state" />)
	</th>
</tr>
<tr>
	<th>
	TimeStamp
	</th>
	<th>
	Info
	</th>
	<th>
	Warnings
	</th>
	<th>
	Errors
	</th>
	<th width="10">
	&nbsp;
	</th>
</tr>
		  <mm:nodelistfunction set="mmpb" name="getTargetPackageSteps" referids="project,target,logid">
		  <tr>
		  <td WIDTH="140">
			<mm:field name="timestamp"><mm:time format="hh:mm:ss dd/MM/yyyy" /></mm:field>
		  </td>
		  <td>
			<mm:field name="userfeedback" />
		  </td>
		  <td>
			<mm:field name="warningcount">
			<mm:field name="warningcount" />
			<mm:compare value="0" inverse="true"><img src="<mm:write referid="image_warning" />"></mm:compare>
			</mm:field>
		  </td>
		  <td>
			<mm:field name="errorcount">
			<mm:field name="errorcount" />
			<mm:compare value="0" inverse="true"><img src="<mm:write referid="image_error" />"></mm:compare>
			</mm:field>
		  </td>
		  <td>
			<mm:field name="haschilds">
			<mm:compare value="true">
			<A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode"><mm:param name="logid"><mm:field name="id" /></mm:param><mm:param name="parentlog"><mm:field name="parent" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</mm:compare>
			</mm:field>
		  </td>
		  </mm:nodelistfunction>
		  </tr>
</table>
</mm:compare>
<mm:compare value="addpackage">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="65%">
		<tr>        
			<th COLSPAN="4" align="left">Add new package to bundle</th>
		</tr>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,"><mm:param name="mode" value="includedpackages" /></mm:url>" method="post">
		<tr>
			<td>select package</td>
			<td><select name="newpackage">
			<mm:nodelistfunction set="mmpm" name="getPackages">
				<option value="<mm:field name="id" />">'<mm:field name="name" />' from '<mm:field name="maintainer" />' type '<mm:field name="type" />'
			</mm:nodelistfunction>
			</select>
			</td>
			<input type="hidden" name="action" value="addtargetpackage" />
			<td width="50"><input type="submit" value="add" /></td>
		</form>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,"><mm:param name="mode" value="includedpackages" /></mm:url>" method="post">
			<td width="50"><input type="submit" value="cancel" /></td>
		</form>
		</tr>
 	</table>
</mm:compare>

<mm:compare value="includedpackages">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
<tr>
	<th COLSPAN="6" align="left">
	Included packages in this bundle
	</th>
</tr>
<tr>
	<th width="150">
	Name
	</th>
	<th width="150">
	Maintainer
	</th>
	<th width="150">
	type	
	</th>
	<th width="150">
	Version
	</th>
	<th width="10">&nbsp;</th>
	<th width="10">&nbsp;</th>
</tr>
		  <mm:nodelistfunction set="mmpb" name="getTargetIncludedPackages" referids="project,target">
		  <form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		  <input type="hidden" name="action" value="setincludedversion" />
		  <tr>
		  <td>
			<mm:field name="name" />
		  </td>
		  <td>
			<mm:field name="maintainer" />
		  </td>
		  <td>
			<mm:field name="type" />
		  </td>
		  <td>
			&nbsp;&nbsp;cur : <mm:field name="version" /> &nbsp;&nbsp;
			<select name="newversion">
			<mm:import id="version" reset="true"><mm:field name="version" /></mm:import>
			<mm:import id="id" reset="true"><mm:field name="id" /></mm:import>
			<mm:import id="foundversion" reset="true">false</mm:import>
		  	<mm:nodelistfunction set="mmpm" name="getPackageVersionNumbers" referids="id">
				<mm:field name="version">
					<mm:compare referid2="version">
						<option selected><mm:field name="version" />
						<mm:import id="foundversion" reset="true">true</mm:import>
					</mm:compare>
					<mm:compare referid2="version" inverse="true">
						<option><mm:field name="version" />
					</mm:compare>
				</mm:field>
			</mm:nodelistfunction>
			</select>
                        <mm:compare referid="foundversion" value="false"><img src="<mm:write referid="image_warning" />"></mm:compare>
		  </td>
		  <td>
			<input type="hidden" name="bid" value="<mm:write referid="id" />">
			<input type="submit" value="save" />
		  </td>
		  </form>
		  <form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		  <td>
		  	<input type="hidden" name="action" value="delincludedpackage" />
			<input type="hidden" name="bid" value="<mm:write referid="id" />">
			<input type="submit" value="delete" />
		  </td>
		</form>
		  </mm:nodelistfunction>
		  </tr>
		  <tr>
         		<td colspan="6"  align="right">Add new package to bundle <A HREF="<mm:url page="index.jsp" referids="main,sub,name,bundle"><mm:param name="mode" value="addpackage" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" valign="middle"></A>&nbsp;&nbsp;
        		</td>
		  </tr>
</table>
</mm:compare>
<mm:compare value="deleteview">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="30%">  
<tr>    
<form action="<mm:url page="index.jsp" referids="main,name,bundle"><mm:param name="sub" value="project" /></mm:url>" method="post">
        <th colspan="2">Delete this target </th>
	<input type="hidden" name="action" value="deltarget" />
<tr><td height="30"><center><input type="submit" value="Yes, delete"></center></td>
	</form>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle"><mm:param name="mode" value="none" /></mm:url>" method="post">
	<td <center><input type="submit" value="no, Oops"></center></td>
	</form>
	</tr>
</td>
</table>
</mm:compare>
<mm:compare value="publishview">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="50%">  
<tr>    
<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,target"><mm:param name="mode" value="none" /></mm:url>" method="post">
        <th colspan="2">Allow publishing of this bundle </th>
	<tr>
		<td>Provider : <select name="publishprovider">
		<mm:nodelistfunction set="mmpm" name="getProviders">
		<mm:field name="method">
		<mm:compare value="disk" inverse="true">
		<option value="<mm:field name="name" />"><mm:field name="name" />
		</mm:compare>
		</mm:field>
		</mm:nodelistfunction>
		</select>
		</td>
		<td><input type="password" name="publishsharepassword" /></td>
	</tr>
	<input type="hidden" name="action" value="publishmodeon" />
<tr><td height="30"><center><input type="submit" value="On"></center></td>
	</form>
	<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,target"><mm:param name="mode" value="none" /></mm:url>" method="post">
	<input type="hidden" name="action" value="publishmodeoff" />
	<td <center><input type="submit" value="Off"></center></td>
	</form>
	</tr>
</td>
</table>
</mm:compare>
<mm:compare value="bundleinfo">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr>
	<th colspan="2">
	Bundle overview and creator information
	</th>
</tr>
 <mm:nodefunction set="mmpb" name="getProjectTargetInfo" referids="project,target">
<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
<tr>
<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
	<td  valign="top">
                <b><mm:field name="bundlename"><mm:compare value=""><font color="red">Bundle Name</font></mm:compare><mm:compare value="" inverse="true">Bundle Name</mm:compare></mm:field></b><p />
		<input type="hidden" name="action" value="setpackagename" />
		&nbsp;&nbsp;<input name="newname" size="30" value="<mm:field name="bundlename" />">
		<input type="submit" value="save">
		<img src="<mm:write referid="image_help" />"  valign="middle" title="Name of this bundle, combined with the Maintainer will be its world wide id" />
	</td>
</form>
<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
	<td  valign="top">
                <b><mm:field name="maintainer"><mm:compare value=""><font color="red">Bundle Maintainer</font></mm:compare><mm:compare value="" inverse="true">Bundle Maintainer</mm:compare></mm:field></b><p />
		<input name="newmaintainer" size="30" value="<mm:field name="maintainer" />" >
		<input type="hidden" name="action" value="setpackagemaintainer" />
		<input type="submit" value="save">
		<img src="<mm:write referid="image_help" />"  valign="middle" title="Maintainer of this bundle, combined with the Name will be its world wide id" />
	</td>
</form>
</tr>
<tr>
<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
	<td colspan="2" valign="top">
                <b><mm:field name="description"><mm:compare value=""><font color="red">Bundle Description</font></mm:compare><mm:compare value="" inverse="true">Bundle Description</mm:compare></mm:field></b><p />
		<center><textarea name="newdescription" rows="7" style="width: 98%"><mm:field name="description" /></textarea></center>
		<p />
		 <input type="hidden" name="action" value="setpackagedescription" />
		<center><input type="submit" value="save">
<img src="<mm:write referid="image_help" />"  valign="middle" title="Description of this bundle, please fill it in correctly since its used in the online search system" />
                </center>
		<p />
	</td>
</tr>
</form>


<tr>
<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
	<td colspan="2" valign="top">
                <b>Bundle Screenshots</b><p />
		<p />
		Name / File / Description <br />
		<table>
	        <mm:nodelistfunction set="mmpb" name="getTargetScreenshots" referids="project,target">
		<tr>
		<td><input name="newname" value="<mm:field name="name" />" size="12" /> <input name="newfile" value="<mm:field name="link" />" size="20"> <input name="newdescription" value="<mm:field name="description" />" size="60" />  <input type="submit" value="save"></td>
			<td>
		 		<input type="hidden" name="action" value="delpackagescreenshot" />
				<input type="submit" value="delete" />
			</td>

		</tr>
		</mm:nodelistfunction>
		<tr>
		<td colsnap="2">
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		<input type="hidden" name="action" value="addpackagescreenshot" />
		<input name="newname" value="" size="12"> <input name="newfile" value="screenshots/" size="20" /> <input name="newname" value="" size="60"> <input type="submit" value="add">
<img src="<mm:write referid="image_help" />"  valign="middle" title="Screenshots related to the bundle for example newsoverview, screenshots/news.jpg, Example of a newsoverview page" />
		<br />
		</form>
		</td>
		</tr>
		</table>
	</td>
</tr>
</form>

<tr>
	<td valign="top">
		<br />
                <mm:field name="haveinitiators"><mm:compare value="false"><b><font color="red">Initiators</font></b></mm:compare><mm:compare value="true"><b>Initiators</b></mm:compare></mm:field><p />
		<mm:import id="type" reset="true" >initiators</mm:import>
		<mm:import id="subtype" reset="true" >initiator</mm:import>
		Name / Company <br />
		<table>
		<tr>
	        <mm:nodelistfunction set="mmpb" name="getTargetPeople" referids="project,target,type,subtype">
			<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		 	<input type="hidden" name="action" value="setpackageinitiator" />
			<td>
			<input name="newname" value="<mm:field name="name" />" size="10"> at <input name="newcompany" value="<mm:field name="company" />" size="10"> <input type="submit" value="save">
			<br />
			<input type="hidden" name="oldname" value="<mm:field name="name" />" />
			<input type="hidden" name="oldcompany" value="<mm:field name="company" />" />
			</td>
			</form>
			<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
			<td>
		 		<input type="hidden" name="action" value="delpackageinitiator" />
				<input type="hidden" name="oldname" value="<mm:field name="name" />" />
				<input type="hidden" name="oldcompany" value="<mm:field name="company" />" />
				<input type="submit" value="delete" />
			</td>
			</form>
		</tr>
		</mm:nodelistfunction>
		<tr>
		<td colsnap="2">
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		<input type="hidden" name="action" value="addpackageinitiator" />
		<input name="newname" value="" size="10"> at <input name="newcompany" value="" size="10" /> <input type="submit" value="add">
<img src="<mm:write referid="image_help" />"  valign="middle" title="Who started this bundle, within opensource a project can change hands quicky here we honor the ones who started it" />
		<br />
		</form>
		</td>
		</tr>
		</table>
		<p />
	</td>
	<td valign="top">
		<br />
                <b><mm:field name="licensetype"><mm:compare value=""><font color="red">License Info</font></mm:compare><mm:compare value="" inverse="true">License Info</mm:compare></mm:field></b><p />
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		Type : <select name="newlicensetype">
			<option><mm:field name="licensetype" />
			<option>MPL
			<option>GPL
			<option>LGPL
			<option>BSD
			<option>OTHER
			</select><input type="submit" value="save" />
<img src="<mm:write referid="image_help" />"  valign="middle" title="License this package is released under, If not one of the given licenses select 'other' and use the name field" />
		 	<input type="hidden" name="action" value="setpackagelicensetype" />
		</form>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		Name : <input name="newlicensename" value="<mm:field name="licensename" />" /><input type="submit" value="save" /><img src="<mm:write referid="image_help" />"  valign="middle" title="License name, either its full name (mozilla public license for example) of the type field or name of your own license" /><br />
		 <input type="hidden" name="action" value="setpackagelicensename" />
		</form>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		Version : <input name="newlicenseversion" value="<mm:field name="licenseversion" />" size="4" /> <input type="submit" value="save" /><img src="<mm:write referid="image_help" />"  valign="middle" title="version number of the defined license" /><br />
		 <input type="hidden" name="action" value="setpackagelicenseversion" />
		</form>
		<p />
		<p />
		
	</td>

<tr>
	<td valign="top" width="50%">
		<br />
		<b>Supporters</b><p />
		<mm:import id="type" reset="true" >supporters</mm:import>
		<mm:import id="subtype" reset="true" >supporter</mm:import>
		<table>
	        <mm:nodelistfunction set="mmpb" name="getTargetPeople" referids="project,target,type,subtype">
			<tr>
			<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
			<td>
		 	<input type="hidden" name="action" value="setpackagesupporter" />
			<input type="hidden" name="oldcompany" value="<mm:field name="company" />" />
			<input name="newcompany" value="<mm:field name="company" />"> <input type="submit" value="save"><br />
			</td>
			</form>
			<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
			<td>
		 		<input type="hidden" name="action" value="delpackagesupporter" />
				<input type="hidden" name="oldcompany" value="<mm:field name="company" />" />
				<input type="submit" value="delete"><br />
			</td>
			</form>
			</tr>
		</mm:nodelistfunction>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		<tr><td colspan="2">
		<input type="hidden" name="action" value="addpackagesupporter" />
		<input name="newcompany" value=""> <input type="submit" value="add"><img src="<mm:write referid="image_help" />"  valign="middle" title="the companies who supported the development" />
<br />
		</tr></td>
		</form>
		</table>
		<p />
		<p />
	</td>
	<td valign="top" width="50%">
		<br />
                <mm:field name="havecontacts"><mm:compare value="false"><b><font color="red">Contact Info</font></b></mm:compare><mm:compare value="true"><b>Contact Info</b></mm:compare></mm:field><p />
		<mm:import id="type" reset="true" >contacts</mm:import>
		<mm:import id="subtype" reset="true" >contact</mm:import>
		reason: name (email)<br />
		<table>
	        <mm:nodelistfunction set="mmpb" name="getTargetPeople" referids="project,target,type,subtype">
		<tr>
		  <form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		  <input type="hidden" name="action" value="setpackagecontact" />
		 	<td>
			<input name="newreason" value="<mm:field name="reason" />" size="10">:<input name="newname" value="<mm:field name="name" />" size="10">(<input name="newemail" value="<mm:field name="mailto" />" size="10">) 
			<input type="hidden" name="oldname" value="<mm:field name="name" />" />
			<input type="hidden" name="oldemail" value="<mm:field name="mailto" />" />
			<input type="hidden" name="oldreason" value="<mm:field name="reason" />" />
		 </td>
		<td>
			<input type="submit" value="save">
		</td>
		  </form>
		  <form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		<td>
		  	<input type="hidden" name="action" value="delpackagecontact" />
			<input type="hidden" name="oldname" value="<mm:field name="name" />" />
			<input type="hidden" name="oldemail" value="<mm:field name="mailto" />" />
			<input type="hidden" name="oldreason" value="<mm:field name="reason" />" />
			<input type="submit" value="delete" />
		  </td>
		</form>
		</tr>
		</mm:nodelistfunction>
                <tr>
                <form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
                <td>
                <input type="hidden" name="action" value="addpackagecontact" />
		<input name="newreason" value="" size="10">:<input name="newname" value="" size="10">(<input name="newemail" value="" size="10">) 
		</td>
		<td>
		 <input type="submit" value="add"><img src="<mm:write referid="image_help" />"  valign="middle" title="contact people for this package with a reason for example 'bugs Joe Plain (joe@mysite.com)" />
                </td>
		<td></td>
                </form>
                </tr>

		</table>
		<p />
		<p />
		
	</td>
</tr>
<tr>
	<td colspan="2" valign="top">
		<br />
                <mm:field name="havedevelopers"><mm:compare value="false"><b><font color="red">Developers who have worked on this release</font></b></mm:compare><mm:compare value="true"><b>Developers who have worked on this release</b></mm:compare></mm:field><p />
		<mm:import id="type" reset="true" >developers</mm:import>
		<mm:import id="subtype" reset="true" >developer</mm:import>
		<table>
		<tr>
	        <mm:nodelistfunction set="mmpb" name="getTargetPeople" referids="project,target,type,subtype">
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
			<td>
			<input type="hidden" name="action" value="setpackagedeveloper" />
			<input type="hidden" name="oldname" value="<mm:field name="name" />" />
			<input type="hidden" name="oldcompany" value="<mm:field name="company" />" />
			name : <input name="newname" value="<mm:field name="name" />"> from <input name="newcompany" value="<mm:field name="company" />" > <input type="submit" value="save"><br /></td>
		</form>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
			<td>
		 	<input type="hidden" name="action" value="delpackagedeveloper" />
			<input type="hidden" name="oldname" value="<mm:field name="name" />" />
			<input type="hidden" name="oldcompany" value="<mm:field name="company" />" />
			<input type="submit" value="delete" />
			</td>
			</form>
		</tr>
		</mm:nodelistfunction>
		<form action="<mm:url page="index.jsp" referids="main,sub,name,bundle,mode" />" method="post">
		<tr><td>
			<input type="hidden" name="action" value="addpackagedeveloper" />
			name : <input name="newname" value=""> from <input name="newcompany" value="" > <input type="submit" value="add"><img src="<mm:write referid="image_help" />"  valign="middle" title="Define all the developers who worked on this bundle (all versions), please keep this up to date" />
<br />
		</td></tr>
		</form>
		</table>
		<p />
	</td>
</tr>
	 </mm:nodefunction>

</table>
</mm:compare>

</mm:write>
