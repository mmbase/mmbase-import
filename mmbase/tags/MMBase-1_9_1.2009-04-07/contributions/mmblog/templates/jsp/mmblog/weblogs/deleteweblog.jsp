<mm:cloud rank="basic user">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblogs/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="70%">
<form action="<mm:url page="index.jsp" referids="main,weblogid" />" method="post">
<mm:node referid="weblogid">
<tr>
  <th><a href="<mm:url page="index.jsp" referids="main" />"><img src="images/mmbase-left.gif" align="left" border="0"></a> Delete weblog</th>
</tr>
<tr>
  <td align="center">
    <br />
    Do you really want to delete the principleset '<b><mm:field name="name" /></b>' ?<br />
    <br />
	** this will delete all the weblogentries in the weblog  and the weblog itself !!
    <br /><br />
	  confirm : <select name="deleteconfirm"><option value="no">No, Sorry<option value="yes">Yes, Delete</select>
    <br /><br />
  </td>
</tr>
</mm:node>
<tr>
</tr>
<tr>
<td align="right" colspan="2">
<input type="hidden" name="action" value="deleteweblog" />
delete <input type="image" src="images/mmbase-delete.gif" />
</form>
&nbsp;&nbsp;&nbsp;cancel <a href="<mm:url page="index.jsp" referids="main" />"><img src="images/mmbase-cancel.gif" border="0"></a>
</td>
</tr>
</table>
</mm:cloud>
