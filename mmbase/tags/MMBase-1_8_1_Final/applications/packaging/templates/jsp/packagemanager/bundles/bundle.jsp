<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>
<mm:import externid="logid">-1</mm:import>
<mm:import externid="parentlog">-1</mm:import>
<mm:import externid="newmsg" />

<mm:import externid="mode">bundleinfo</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="bundles/actions.jsp" />
</mm:present>
<!-- end action check -->


<mm:nodelistfunction set="mmpm" name="haveBundleLog" referids="id,version,provider">
	<mm:import id="havelog"><mm:field name="log" /></mm:import>
</mm:nodelistfunction>

<mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
<mm:import id="state"><mm:field name="state" /></mm:import>
</mm:nodelistfunction>

<mm:compare referid="state" value="installing">

<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<tr>
<th colspan="2">progress on installing</th>
</tr>
<tr>
<td colspan="2">
When the install is done you can check state and log to see if all went well
</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
<tr>
<td width="<mm:field name="bundleprogressbarvalue" />%" style="background-color: #0000aa">&nbsp;</td><td>&nbsp;</td>
</tr>
</mm:nodelistfunction>
</td></tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
<tr>
<td width="<mm:field name="packageprogressbarvalue" />%" style="background-color: #00aa00">&nbsp;</td><td>&nbsp;</td>
</tr>
</mm:nodelistfunction>
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
    window.location.href ='index.jsp?main=<mm:write referid="main" />&sub=<mm:write referid="sub" />&id=<mm:write referid="id" />&version=<mm:write referid="version" />&provider=<mm:write referid="provider" />&newmsg=installed&msgtype=install';
}
//-->
</script>
</mm:compare>


<mm:compare referid="state" value="uninstalling">
<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
<tr>
<th colspan="2">progress on uninstalling</th>
</tr>
<tr>
<td colspan="2">
When the install is done you can check state and log to see if all went well
</td>
</tr>
<tr>
<td width="44%" style="background-color: #0000aa">&nbsp;</td><td>&nbsp;</td>
</tr>
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
    window.location.href ='index.jsp?main=<mm:write referid="main" />&sub=<mm:write referid="sub" />&id=<mm:write referid="id" />&version=<mm:write referid="version" />&provider=<mm:write referid="provider" />&newmsg=installed&msgtype=uninstall';
}
//-->
</script>
</mm:compare>

<mm:compare referid="state" value="installing" inverse="true">
<mm:compare referid="state" value="uninstalling" inverse="true">
<mm:present referid="newmsg">
	<mm:import externid="msgtype" />
	<mm:compare referid="msgtype" value="install">
	<mm:import id="action" reset="true">installlog</mm:import>
	<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
	<tr>
	<th colspan="1">Result of installing</th>
	</tr>
	<tr>
	<td colspan="1" align="middle">
		<br />
		<b>install complete : <mm:write referid="state" /></b>
		<br /><br />
	</tr>
	<tr></td><td width="100%" style="background-color: #0000aa">&nbsp;</td></tr>
	<tr></td><td width="100%" style="background-color: #00aa00">&nbsp;</td></tr>
	<td colspan="1" align="middle">
		<br />
		<b>If installed correctly, please read the release and install notes<br />
		For warnings and errors see below or under the 'show logfile'.<br /><br /></b>
		<form action="<mm:url page="index.jsp" referids="main,sub,id,provider,version" />" method="post">
		<center><input type="submit" value="ok"></center>
		</form>
	</tr>
	</table>
	</mm:compare>
	<mm:compare referid="msgtype" value="uninstall">
	<mm:import id="action" reset="true">installlog</mm:import>
	<table cellpadding="0" cellspacing="0" class="list" align="middle" width="60%">
	<tr>
	<th colspan="1">Result of uninstalling</th>
	</tr>
	<tr>
	<td colspan="1" align="middle">
		<br />
		<b>uninstall complete : <mm:write referid="state" /></b>
		<br /><br />
	</tr>
	<tr></td><td width="100%" style="background-color: #0000aa">&nbsp;</td></tr>
	<tr></td><td width="100%" style="background-color: #00aa00">&nbsp;</td></tr>
	<td colspan="1" align="middle">
		<br />
		<b>
		See log for any problems, read release notes to see if a uninstall really			 removed all parts once installed.<br /><br /></b>
		<form action="<mm:url page="index.jsp" referids="main,sub,id,provider,version" />" method="post">
		<center><input type="submit" value="ok"></center>
		</form>
	</tr>
	</table>
	</mm:compare>
</mm:present>
<mm:notpresent referid="newmsg">
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<tr>
<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Bundle Information
	</th>
	</tr>
	<mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
 	    <tr><th>Name</th><td align="left"><mm:field name="name" /></td>
 	    <tr><th>Type</th><td align="left"><mm:field name="type" /></td>
 	    <tr><th>Version</th><td align="left"><mm:field name="version" /></td>
 	    <tr><th>Maintainer</th><td align="left"><mm:field name="maintainer" /></td>
 	    <tr><th>Creation-Date</th><td align="left"><mm:field name="creation-date" /></td>
 	    <tr><th>Provider</th><td align="left"><mm:field name="provider" /></td>
	  </mm:nodelistfunction>
</table>
</td>

<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Bundle Actions
	</th>
	</tr>
	  <mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
 	    <tr><th>State</th><td align="left"><mm:field name="state" /></td>
                  <mm:write referid="state">
                  <mm:compare value="not installed">  
 	    		<tr><th>Install</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode" value="askinstall" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
		  </mm:compare>

                  <mm:compare value="upgrade available">  
 	    		<tr><th>Upgrade</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode" value="askinstall" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
		  </mm:compare>



                  <mm:compare value="installed">  
 	    		<tr><th>Uninstall</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode" value="askuninstall" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>


		  </mm:compare>


                  <mm:compare referid="state" value="installing" inverse="true">  
                  <mm:compare referid="havelog" value="true">  
 	    		<tr><th>View log</th>
			<td>
                        <A HREF="<mm:url page="index.jsp">
                                        <mm:param name="main" value="$main" />
                                        <mm:param name="sub" value="bundle" />
                                        <mm:param name="id" value="$id" />
                                        <mm:param name="action" value="installlog" />
                                        <mm:param name="provider"><mm:field name="provider" /></mm:param>
                                        <mm:param name="version"><mm:field name="version" /></mm:param>
                                </mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
		  </mm:compare>
		  </mm:compare>

                  <mm:compare referid="state" value="installing">  
 	    		<tr><th>View progress</th>
			<td>
                        <A HREF="<mm:url page="index.jsp">
                                        <mm:param name="main" value="$main" />
                                        <mm:param name="sub" value="bundle" />
                                        <mm:param name="id" value="$id" />
                                        <mm:param name="action" value="installlog" />
                                        <mm:param name="provider"><mm:field name="provider" /></mm:param>
                                        <mm:param name="version"><mm:field name="version" /></mm:param>
                                </mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
		  </mm:compare>


 	    		<tr><th>Versions</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param>
					<mm:param name="mode">versions</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>


 	    		<tr><th>Overview</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param>
					<mm:param name="mode">bundleinfo</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>

 	    		<tr><th>Included Packages</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param>
					<mm:param name="mode">included</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>


		  </mm:write>
	  </mm:nodelistfunction>
</table>
</td>

</tr>
</table>
</mm:notpresent>
</mm:compare>
</mm:compare>

<mm:import id="showlog">false</mm:import>
<mm:compare referid="state" value="installing">
  <mm:remove referid="showlog" />
  <mm:import id="showlog">true</mm:import>
</mm:compare>
<mm:compare referid="action" value="installlog">
  <mm:remove referid="showlog" />
  <mm:import id="showlog">true</mm:import>
</mm:compare>
<p />
<p />
<p />
<p />
<mm:compare referid="showlog" value="false">
<center>
<mm:compare referid="mode" value="included">
<table cellpadding="0" cellspacing="0" style="margin-left : 10px;" width="95%" align="middle">
<tr>
	<td colspan="4">
	Packages within this bundle
	</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;" width="95%" align="middle">
<tr>
	<th>
	Version
	</th>
	<th>
	Maintainer
	</th>
	<th>
	Version
	</th>
	<th>
	State
	</th>
	<th>
	Provider	
	</th>
	<th WIDTH="10">
	<B></B>
	</th>
</tr>
		  <mm:nodelistfunction set="mmpm" name="getBundleNeededPackages" referids="id,version,provider">
		  <tr>
		  <td>
			<mm:field name="name" />
		  </td>
		  <td>
			<mm:field name="maintainer" />
		  </td>
		  <td>
			<mm:field name="version" />
		  </td>
		  <td>
			<mm:field name="state" />
		  </td>
		  <td>
			<mm:field name="provider" />
		  </td>
		  <td>
			<mm:remove referid="newid" />
			<mm:import id="newid"><mm:field name="id" /></mm:import>
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="packages" /><mm:param name="sub" value="package" /><mm:param name="id" value="$newid" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </TD>
		  </mm:nodelistfunction>
		  </tr>
</table>
</mm:compare>


<mm:compare referid="mode" value="bundleinfo">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
	  <mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
<tr>
	<th colspan="2">
	Bundle overview and creator information
	</th>
</tr>
<tr>
	<td colspan="2" valign="top" align="left">
		<br />
		<b>Bundle Description</b><p />
		<mm:field name="description" />
		<p />
		<p />
	</td>
</tr>
<mm:nodelistfunction set="mmpm" name="getBundleScreenshots" referids="id,version,provider">
<mm:first>
<tr>
	<td valign="top" align="left" colspan=2">
		<br />
		<b>Screenshots</b><p />
		<table>
		<tr>
</mm:first>
			<th>
			<a href="<mm:field name="file" />" target="_new" title="<mm:field name="description" />"><img src="<mm:field name="file" />" width="150"></a><br />
			<mm:field name="name" />
			</th>
<mm:last>
		</tr>
		</table>
		<br />
		<br />
	</td>
</tr>
</mm:last>
</mm:nodelistfunction>


<mm:nodelistfunction set="mmpm" name="getBundleStarturls" referids="id,version,provider">
<mm:first>
<tr>
	<td valign="top" align="left" colspan=2">
		<br />
		<b>Starturls</b><p />
</mm:first>
		<mm:field name="name" /> : <a href="<mm:field name="link" />"><mm:field name="link" /></a> (<mm:field name="description" />)
<mm:last>
		<br />
		<br />
	</td>
</tr>
</mm:last>
</mm:nodelistfunction>

<tr>
	<td valign="top" align="left">
		<br />
		<b>Initiators</b><p />
		<mm:import id="type" reset="true" >initiators</mm:import>
	        <mm:nodelistfunction set="mmpm" name="getBundlePeople" referids="id,version,provider,type">
			Name : <mm:field name="name" /><br />
			Company : <mm:field name="company" /><br />
			<p />
		</mm:nodelistfunction>
		<p />
	</td>
	<td valign="top" align="left">
		<br />
		<b>Licence info</b><p />
		Type : <mm:field name="licensetype" /><br />
		<mm:field name="licensename"><mm:compare value="" inverse="true">
		Name : <mm:field name="licensename" /><br />
		</mm:compare></mm:field>
		Version : <mm:field name="licenseversion" /><br />
		<p />
		<p />
		
	</td>

<tr>
	<td valign="top" width="50%" align="left">
		<br />
		<b>Supporters</b><p />
		<mm:import id="type" reset="true" >supporters</mm:import>
	        <mm:nodelistfunction set="mmpm" name="getBundlePeople" referids="id,version,provider,type">
			<mm:field name="company" /><br />
		</mm:nodelistfunction>
		<p />
		<p />
	</td>
	<td valign="top" width="50%" align="left">
		<br />
		<b>Contact info</b><p />
		<mm:import id="type" reset="true" >contacts</mm:import>
	        <mm:nodelistfunction set="mmpm" name="getBundlePeople" referids="id,version,provider,type">
			<mm:field name="reason" /> : <mm:field name="name" /> (<mm:field name="mailto" />) <br />
		</mm:nodelistfunction>
		<p />
		<p />
		
	</td>
</tr>
<tr>
	<td colspan="2" valign="top" align="left">
		<br />
		<b>Developers who have worked on this release</b><p />
		<mm:import id="type" reset="true" >developers</mm:import>
	        <mm:nodelistfunction set="mmpm" name="getBundlePeople" referids="id,version,provider,type">
			<mm:field name="name" /> from <mm:field name="company" /> <br />
		</mm:nodelistfunction>
		<p />
		<p />
	</td>
</tr>
	 </mm:nodelistfunction>

</table>
</mm:compare>

<mm:compare referid="mode" value="versions">
<table cellpadding="0" cellspacing="0" style="margin-left : 10px;" width="95%" align="middle">
<tr>
	<td colspan="4"s>
	Available versions of this bundle and their providers
	</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;" width="95%">
<tr>
	<th>
	Version
	</th>
	<th>
	Provider
	</th>
	<th>
	Maintainer
	</th>
	<th>
	State
	</th>
	<th WIDTH="10">
	&nbsp;
	</th>
</tr>
		  <mm:nodelistfunction set="mmpm" name="getBundleVersions" referids="id">
		  <tr>
		  <td>
			<mm:field name="version" />
		  </td>
		  <td>
			<mm:field name="provider" />
		  </td>
		  <td>
			<mm:field name="maintainer" />
		  </td>
		  <td>
			<mm:field name="state" />
		  </td>
		  <td>
			<A HREF="<mm:url page="index.jsp">
			<mm:param name="main" value="$main" />
			<mm:param name="sub" value="bundle" />
			<mm:param name="id" value="$id" />
			<mm:param name="provider"><mm:field name="provider" /></mm:param>
			<mm:param name="version"><mm:field name="version" /></mm:param>
			</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </td>
		  </tr>
		  </mm:nodelistfunction>
</TABLE>
</mm:compare>

<mm:compare referid="mode" value="askinstall">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
<tr>
	<th COLSPAN="2">
	Install confirmation
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
<tr>
  <td colspan="2" width="300">
	<br />
	&nbsp;&nbsp;Are you sure you want to install bundle : <br /><br />
	<center>name : <b>'<mm:field name="name" />'</b> maintainer : <b>'<mm:field name="maintainer" />'</b> version : <b>'<mm:field name="version" />'</b> </center>
	<center>will install from provider : <b>'<mm:field name="provider" />'</b> </center>
	<br />
  </td>
</tr>
<tr>
  <td>
	<br />
	<form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
	<center><input type="submit" value="Oops,No "></center></form>
  </td>
  <td>
	<br />
	<form action="<mm:url page="index.jsp"><mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="action" value="installbundle" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param></mm:url>" method="post">
	<center><input type="submit" value="Yes, Install"></center></form>
  </td>
</tr>
</mm:nodelistfunction>
</table>

</mm:compare>


<mm:compare referid="mode" value="askuninstall">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
<tr>
	<th COLSPAN="2">
	Uninstall confirmation
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getBundleInfo" referids="id,version,provider">
<tr>
  <td colspan="2" width="300">
	<br />
	&nbsp;&nbsp;Are you sure you want to uninstall bundle : <br /><br />
	<center>name : <b>'<mm:field name="name" />'</b> maintainer : <b>'<mm:field name="maintainer" />'</b> version : <b>'<mm:field name="version" />'</b> </center>
	<center>will install from provider : <b>'<mm:field name="provider" />'</b> </center>
	<br />
  </td>
</tr>
<tr>
  <td>
	<br />
	<form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
	<center><input type="submit" value="Oops,No "></center></form>
  </td>
  <td>
	<br />
	<form action="<mm:url page="index.jsp"><mm:param name="main" value="$main" />
					<mm:param name="sub" value="bundle" />
					<mm:param name="id" value="$id" />
					<mm:param name="action" value="uninstallbundle" />
					<mm:param name="provider"><mm:field name="provider" /></mm:param>
					<mm:param name="version"><mm:field name="version" /></mm:param></mm:url>" method="post">
	<center><input type="submit" value="Yes, Uninstall"></center></form>
  </td>
</tr>
</mm:nodelistfunction>
</table>

</mm:compare>

</mm:compare>

<mm:compare referid="showlog" value="true">
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;" width="95%">
<tr>
	<th colspan="5">
		<A HREF="<mm:url page="index.jsp" referids="main,sub,provider,version,id,action"><mm:param name="logid"><mm:write referid="parentlog" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowleft" />" BORDER="0" ALIGN="left"></A>
	Install log of this bundle
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
	<B></B>
	</th>
</tr>
		  <mm:nodelistfunction set="mmpm" name="getBundleInstallSteps" referids="id,version,provider,logid">
		  <tr>
		  <td width="140">
			<mm:field name="timestamp"><mm:time format="hh:mm:ss dd/MM/yyyy" /></mm:field>
		  </td>
		  <td>
			<mm:field name="userfeedback" />
		  </td>
		  <td>
			<mm:field name="warningcount" />
                        <mm:field name="warningcount">
                        <mm:compare value="0" inverse="true"><img src="<mm:write referid="image_warning" />"></mm:compare>
                        </mm:field>
		  </td>
		  <td>
			<mm:field name="errorcount" />
                        <mm:field name="errorcount">
                        <mm:compare value="0" inverse="true"><img src="<mm:write referid="image_error" />"></mm:compare>
                        </mm:field>
		  </td>
		  <td>
			<mm:field name="haschilds">
			<mm:compare value="true">
			<A HREF="<mm:url page="index.jsp" referids="main,sub,provider,version,id,action"><mm:param name="logid"><mm:field name="id" /></mm:param><mm:param name="parentlog"><mm:field name="parent" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</mm:compare>
			</mm:field>
		  </td>
		  </mm:nodelistfunction>
		  </tr>
</table>
</mm:compare>
