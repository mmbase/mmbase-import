<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<mm:import externid="id" />
<tr class="layout"><td align="left"><a href="index.jsp"><img src="/media/arrow-left.gif" border="0"></a></tr>
<tr>
<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Bundle Information
	</th>
	</tr>
 	    <tr><th>Name</th><td><mm:write referid="id" /></td>
	    <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/type.txt</mm:import>
 	    <tr><th>Type</th><td><mm:include page="$file" /></td>
	    <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/version.txt</mm:import>
 	    <tr><th>Version</th><td><mm:include page="$file" /></td>
	    <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/maintainer.txt</mm:import>
 	    <tr><th>Maintainer</th><td><mm:include page="$file" /></td>
	    <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/creation-date.txt</mm:import>
 	    <tr><th>Creation-Date</th><td><mm:include page="$file" /></td>
</table>
</td>

<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Bundle Actions
	</th>
	</tr>
         <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/download.txt</mm:import>
 	 <mm:include page="$file" />
         <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/website.txt</mm:import>
 	 <mm:include page="$file" />
</table>
</td>

</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr>
	<th colspan="2">
	Bundle overview and creator information
	</th>
</tr>
<tr>
	<td colspan="2" valign="top">
		<br />
		<b>Bundle Description</b><p />
		<p />
	    	<mm:import id="file" reset="true">bundles/<mm:write referid="id" />/description.txt</mm:import>
	    	<mm:include page="$file" />
		<p />
	</td>
</tr>
<tr>
	<td valign="top">
		<br />
		<b>Initiators</b><p />
	    		<mm:import id="file" reset="true">bundles/<mm:write referid="id" />/initiators.txt</mm:import>
	    		<mm:include page="$file" />
		<p />
	</td>
	<td valign="top">
		<br />
		<b>Licence info</b><p />
	    		<mm:import id="file" reset="true">bundles/<mm:write referid="id" />/license.txt</mm:import>
	    		<mm:include page="$file" />
		<p />
		<p />
		
	</td>

<tr>
	<td valign="top" width="50%">
		<br />
		<b>Supporters</b><p />
                        <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/supporters.txt</mm:import>
                        <mm:include page="$file" />
		<p />
		<p />
	</td>
	<td valign="top" width="50%">
		<br />
		<b>Contact info</b><p />
                        <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/contacts.txt</mm:import>
                        <mm:include page="$file" />
		<p />
		<p />
		
	</td>
</tr>
<tr>
	<td colspan="2" valign="top">
		<br />
		<b>Developers who have worked on this release</b><p />
                        <mm:import id="file" reset="true">bundles/<mm:write referid="id" />/developers.txt</mm:import>
                        <mm:include page="$file" />
		<p />
		<p />
	</td>
</tr>

</table>
