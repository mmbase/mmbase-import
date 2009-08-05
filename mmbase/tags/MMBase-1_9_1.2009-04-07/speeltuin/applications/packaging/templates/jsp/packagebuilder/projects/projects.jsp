<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="projects/actions.jsp" />
</mm:present>
<!-- end action check -->
<mm:import externid="mode">list</mm:import>
<mm:compare referid="mode" value="list">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
<tr>
	<th>
	Project	
	</th>
	<th>
	Packaging file	
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpb" name="getProjects">
<tr>
		<td width="200">
			<mm:field name="name">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="project" /><mm:param name="name" value="$_" /></mm:url>"><mm:field name="syntaxerrors"><mm:compare value="true"><font color="red"><mm:field name="name" /></font></mm:compare><mm:compare value="false"><mm:field name="name" /></mm:compare></mm:field></a>
			</mm:field>
		</td>
		<td>
			<mm:field name="path" />	
		</td>
		<td width="15">
			<mm:field name="name">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="project" /><mm:param name="name" value="$_" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</mm:field>
		</td>
</tr>
 </mm:nodelistfunction>
<tr>
 <td colspan="6" align="right">
 Add Project
 <A HREF="<mm:url page="index.jsp"><mm:param name="mode" value="newproject" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td>
</tr>

</table>
</mm:compare>
<mm:compare referid="mode" value="newproject">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="75%">   
<tr>    
        <th width="200">
        Project Name 
        </th>
        <th>    
	Packaging file
	</th>
</tr>
<form action="<mm:url page="index.jsp" />" method="post">
<input type="hidden" name="action" value="addproject" />
<tr>    
        <td width="200">
	<input style="width: 90%" name="newprojectname"><img src="<mm:write referid="image_help" />"  valign="middle" title="Name the project for example 'myproject'" />
        </td>
        <td>    
	<input style="width: 95%" name="newprojectpath"><img src="<mm:write referid="image_help" />"  valign="middle" title="path of the packaging.xml file,example /usr/me/myproject/packaging.xml" />
	</td>
</tr>
<tr>    
        <td colspan="2" align="middle">
	<table width="50%"><tr>
	<td align="middle"><input type="submit" value="Add"></form></td>
	<form action="<mm:url page="index.jsp" />" method="post">
	<td align="middle"><input type="submit" value="Cancel"></form></td>
	</tr></table>
</tr>
</table>
</mm:compare>
