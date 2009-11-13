<mm:cloud rank="basic user">
<mm:import externid="weblogentryid" />
<mm:import externid="weblogrelid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblog/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="65%">
<mm:node referid="weblogentryid">
<form action="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid" />" method="post">
<input name="action" type="hidden" value="deleteentry" />
<tr>
  <th colspan="2"><a href="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid"><mm:param name="sub">entry</mm:param></mm:url>"><img src="images/mmbase-left.gif" align="left" border="0"></a> Delete entry</th>
</tr>
<tr>
  <td align="center" width="50%" colspan="2">
    <br />
    <b>Do you really want to delete this entry ?</b><br />
    <br />
    <table>
      <tr><td><b>title</b></td><td><mm:field name="title" /></td></tr>
      <tr><td><b>postdate</b></td><td><mm:field name="postdate"><mm:time format="dd MMMM yyyy" /></mm:field></td></tr>
    </table>
    <br />
    <br />
  </td>
</tr>
</mm:node>
<tr>
	<td align="center">yes delete <input type="image" src="images/mmbase-delete.gif"/></td>
	</form>
	<td align="center">no cancel <a href="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid"><mm:param name="sub">entry</mm:param></mm:url>"><img src="images/mmbase-cancel.gif" border="0"></a></td>
</tr>
</table>
</mm:cloud>
