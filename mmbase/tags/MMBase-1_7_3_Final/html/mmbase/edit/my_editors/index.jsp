<% String title = "Home"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">
<mm:import externid="ntype" jspvar="ntype" />
<mm:import externid="nr" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>
<table border="0" cellspacing="0" cellpadding="3">
<tr>
<td valign="top" width="20%">
<!-- Choose a node type -->
<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">
<tr bgcolor="#CCCCCC">
  <td colspan="3" align="center"> List of <b><mm:write referid="conf_list" /></b> node types (<a href="config.jsp">configure</a>)</td> 
</tr>
<% // Choose a node type
String guiName = "";
NodeManagerList l = wolk.getNodeManagers();
java.util.Collections.sort(l);
int j = 0;
for (int i = 0; i < l.size(); i++) {
	NodeManager nm = l.getNodeManager(i);
	guiName = nm.getGUIName();
 	if (nm.mayCreateNode() && !nm.hasField("dnumber") || conf_list.equals("all")) { // Are we allowed to create?
 	j++;
 	%>
	<tr<%if (j % 2 == 0) { %> bgcolor="#FFFFFF"<% } %>>
	  <td align="right"><b><%= nm.getName() %></b> </td>
	  <td><a href="index.jsp?ntype=<%= nm.getName() %>" title="show nodes"><%= guiName %></a></td>
	  <td nowrap="nowrap"> 
	  	<a href="index.jsp?ntype=<%= nm.getName() %>" title="show recent nodes"><img src="img/mmbase-search.gif" alt="show recent nodes" width="21" height="20" border="0" /></a>
	    <% if (nm.mayCreateNode()) { %><a href="new_object.jsp?ntype=<%= nm.getName() %>" title="new node"><img src="img/mmbase-new.gif" alt="new node" width="21" height="20" border="0" /></a><% } %>
	  </td>
	</tr>
	<%
	}
} 
%>
</table>
<!-- /nodetypes -->
<!-- table about icons -->
<div align="center">
<table border="0" cellpadding="0" cellspacing="0">
  <tr align="left">
    <td colspan="2">&nbsp;</td>
    <td rowspan="3" nowrap="nowrap">&nbsp;<b class="title-ss">About the icons</b></td>
    <td colspan="2">&nbsp;</td>
  </tr><tr align="left">
    <td bgcolor="#000000" colspan="2"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
    <td bgcolor="#000000" colspan="2"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr><tr align="left">
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
    <td width="20">&nbsp;</td>
    <td width="20">&nbsp;</td>
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr><tr align="left">
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
    <td colspan="3">
<!- table in table -->
  <table width="100%" border="0" cellspacing="0" cellpadding="4">
	<tr>
	  <td align="right" width="24"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a></td>
	  <td nowrap="nowrap"> Search node </td>
	</tr>
	<tr>
	  <td align="right" width="24"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a></td>
	  <td nowrap="nowrap"> Create new node </td>
	</tr>
	<tr>
	  <td align="right" width="24"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></td>
	  <td nowrap="nowrap"> Edit node </td>
	</tr>
	<tr>
	  <td align="right" width="24"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></td>
	  <td nowrap="nowrap"> Delete node </td>
	</tr>
  </table>
<!- end table in table -->
    </td>
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr><tr align="left">
    <td colspan="5" bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr>
</table>
</div>
<!-- /about the icons -->
</td>
<td valign="top" width="80%">

<!-- Search and search results -->
<%@ include file="inc_searchresults.jsp" %>
<%@ include file="inc_searchform.jsp" %>

</td>
</tr>
</table>
<%@ include file="inc_foot.jsp" %>
</mm:cloud>
