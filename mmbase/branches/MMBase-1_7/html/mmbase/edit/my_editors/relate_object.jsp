<% String title = "Relate objects"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">

<mm:context id="relate_node">
<mm:import externid="ntype" jspvar="ntype" required="true" />	<%-- nodetype to relate with --%>
<mm:import externid="nr" 	jspvar="nr"	required="true" />		<%-- originale node (nr) that wants relation --%>
<mm:import externid="dir" 	jspvar="dir" />						<%-- direction of relation --%>
<mm:import externid="rkind" jspvar="rkind" />					<%-- kind of relation --%>
<mm:import externid="rnr" />									<%-- node (nr) to relate with --%>


<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<!-- main table -->
<table border="0" cellspacing="0" cellpadding="3">
<tr>
  <td valign="top" width="20%">
	
	<!-- table with back button -->
	<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">	
	<tr bgcolor="#EFEFEF">
	  <td width="24" align="right"><a href="<mm:url page="edit_object.jsp" referids="nr" />"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></td>
	  <td><a href="<mm:url page="edit_object.jsp" referids="nr" />">Back</a> to editing <b><mm:node referid="nr"><mm:nodeinfo type="type" /></mm:node></b> object</td>
	</tr>
	<tr valign="top">
	  <td>&nbsp;</td>
	  <td>
	  <mm:node referid="nr">
		  <mm:fieldlist type="list">
			  <mm:fieldinfo type="guiname" /> <mm:first><b></mm:first> <mm:fieldinfo type="guivalue" /><mm:first></b></mm:first><br />
		  </mm:fieldlist>
	  </mm:node>
	  </td>
	</tr>
	</table>
	<!-- end table back button -->
	<%--	<%@ include file="inc_relations.jsp" %> --%>

  </td>
  <td valign="top" width="80%">
  	<!-- ### Search results -->	

	<mm:notpresent referid="rnr">
  		<%@ include file="inc_searchresults.jsp" %>
  	</mm:notpresent>

  	<!-- ### Relate nodes -->
	<%@ include file="inc_relate.jsp" %>


  </td>
</tr><tr>
  <td width="20%" align="center" valign="top">
	<!-- table about the icons -->
	<table border="0" cellpadding="0" cellspacing="0" align="center">
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
			  <td nowrap="nowrap"> Search object to relate to </td>
			</tr>
			<tr>
			  <td align="right" width="24"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a></td>
			  <td nowrap="nowrap"> Create new object (and relate) </td>
			</tr>
			<tr>
			  <td align="right" width="24"><img src="img/mmbase-relation-left.gif" alt="relation" width="22" height="20" border="0" /></a></td>
			  <td nowrap="nowrap"> Create relation </td>
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
  </tr><tr>
    <td colspan="5" bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr>
</table>
<!-- end about the icons -->
  </td>
  <td width="80%" valign="top">
  	<!-- ### Search form -->
	<mm:notpresent referid="rnr">
	  <%@ include file="inc_searchform.jsp" %>
	</mm:notpresent>
  </td>
</tr>
</table>
<!-- end main table -->

</mm:context>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
