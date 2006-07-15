<mm:cloud rank="basic user">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblog/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:import externid="weblogentryid" />
<mm:import externid="weblogrelid" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<form action="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid" ><mm:param name="sub">entry</mm:param></mm:url>" method="post">
<mm:node referid="weblogentryid">
<tr>
  <th><a href="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid" ><mm:param name="sub">entry</mm:param></mm:url>"><img src="images/mmbase-left.gif" align="left" border="0"></a> Entry description</th>
  <th>Entry information</th>
</tr>
<tr>
  <td width="70%">

    <b>State</b><br />
    <select name="newstate"><mm:node referid="weblogrelid"><option><mm:field name="state" /></mm:node><option>published<option>concept<option>archived</select><br /><br />

    <b>Title</b><br />
    <input name="newtitle" style="width: 95%" value="<mm:field name="title" />" /><br /><br />
    <b>Text</b><br />
    <textarea name="newbody" rows="35" style="width: 95%"><mm:field name="body" /></textarea><br /></br />

  <td valign="top" width="30%">
  </td>
</tr>
</mm:node>
<tr>
</tr>
<tr>
<td align="right" colspan="2">
<input type="hidden" name="action" value="updateentry" />
save <input type="image" src="images/mmbase-ok.gif" />
</form>
&nbsp;&nbsp;&nbsp;cancel <a href="<mm:url page="index.jsp" referids="main,weblogentryid,weblogrelid,weblogid" ><mm:param name="sub">entry</mm:param></mm:url>"><img src="images/mmbase-cancel.gif" border="0"></a>
</td>
</tr>
</table>
</mm:cloud>
