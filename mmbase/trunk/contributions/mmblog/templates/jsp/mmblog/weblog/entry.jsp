
<mm:import externid="weblogentryid" />
<mm:import externid="weblogrelid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblog/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<mm:node referid="weblogentryid">
<tr>
  <th><a href="<mm:url page="index.jsp" referids="main,weblogid" />"><img src="images/mmbase-left.gif" border="0" align="left"></a> Entry description</th>
  <th>Entry information</th>
</tr>
<tr>
  <td width="70%">
    <b>State</b><br />
    <mm:node referid="weblogrelid"><mm:field name="state" /></mm:node><br /><br />

    <b>Title</b><br />
    <mm:field name="title" /><br /><br />

    <b>Text</b><br />
    <mm:field name="body" escape="p" /><br /><br />

  </td>
  <td valign="top" width="30%">
  </td>
</tr>
</mm:node>
<tr>
</tr>
<tr>
	<td align="right" colspan="2">update <a href="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid"><mm:param name="sub">editentry</mm:param></mm:url>"><img src="images/mmbase-edit.gif" border="0"></a>&nbsp;&nbsp;&nbsp;delete <a href="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid"><mm:param name="sub">deleteentry</mm:param></mm:url>"><img src="images/mmbase-delete.gif" border="0"></a></td>
</tr>
</table>
