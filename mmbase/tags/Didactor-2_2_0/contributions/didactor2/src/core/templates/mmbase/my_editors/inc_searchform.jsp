<!-- ### Search form ### --><a name="search"></a>
<mm:present referid="ntype">
	<mm:present referid="nr">		<%-- if there is a nr, there is a node and thus we are trying to find another to relate to --%>
		<form method="post" action="<mm:url referids="ntype,nr,rkind,dir" />">
	</mm:present>
	<mm:notpresent referid="nr">
		<form method="post" action="<mm:url referids="ntype" />">
	</mm:notpresent>
<table width="100%" border="0" cellspacing="0" cellpadding="4" class="table-search">
	<tr>
	  <td bgcolor="#CCCCCC" align="center"><img src="img/mmbase-search.gif" alt="Search" width="21" height="20" border="0" /></td>
	  <td bgcolor="#CCCCCC" class="title-s">Search node of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b> (<%= ntype %>)</td>
	</tr>
	<tr valign="top">
	  <td align="right" class="name">Days old</td>
	  <td><input class="small" type="text" name="conf_days" value="<%= conf_days %>" size="9" maxlength="9" /></td>
	</tr>
	<mm:fieldlist nodetype="$ntype" type="search">
		<tr valign="top">
		  <td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
		  <td><mm:fieldinfo type="searchinput" /></td>
		</tr>
	</mm:fieldlist>
  <tr><td>&nbsp;</td><td><input type="submit" name="search" value="Search" /></td></tr>
</table>
</form>
</mm:present>
