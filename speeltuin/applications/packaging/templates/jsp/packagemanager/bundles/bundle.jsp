<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>
<mm:import externid="logid">-1</mm:import>
<mm:import externid="parentlog">-1</mm:import>

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
 	    <tr><th>Name</th><td><mm:field name="name" /></td>
 	    <tr><th>Type</th><td><mm:field name="type" /></td>
 	    <tr><th>Version</th><td><mm:field name="version" /></td>
 	    <tr><th>Maintainer</th><td><mm:field name="maintainer" /></td>
 	    <tr><th>Creation-Date</th><td><mm:field name="creation-date" /></td>
 	    <tr><th>Provider</th><td><mm:field name="provider" /></td>
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
 	    <tr><th>State</th><td><mm:field name="state" /><mm:import id="state"><mm:field name="state" /></mm:import></td>
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
	<td colspan="2" valign="top">
		<br />
		<b>Bundle Description</b><p />
		<mm:field name="description" />
		<p />
		<p />
	</td>
</tr>
<tr>
	<td valign="top">
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
	<td valign="top">
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
	<td valign="top" width="50%">
		<br />
		<b>Supporters</b><p />
		<mm:import id="type" reset="true" >supporters</mm:import>
	        <mm:nodelistfunction set="mmpm" name="getBundlePeople" referids="id,version,provider,type">
			<mm:field name="company" /><br />
		</mm:nodelistfunction>
		<p />
		<p />
	</td>
	<td valign="top" width="50%">
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
	<td colspan="2" valign="top">
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
	<td colspan="4">
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
