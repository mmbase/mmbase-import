<mm:cloud rank="basic user">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblog/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:node referid="weblogid">
  <mm:import id="weblogid" reset="true"><mm:field name="number" /></mm:import>
</mm:node>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<form action="<mm:url page="index.jsp" referids="main,sub,weblogid" />" method="post">
<tr>
  <th>Weblog description</th>
  <th>Weblog information</th>
</tr>
<tr>
  <td width="70%">
    <b>State</b><br />
    <select name="newstate"><option>published<option>concept<option>archived</select><br /><br />

    <b>Title</b><br />
    <input name="newtitle" style="width: 95%" /><br /><br />

    <b>Text</b><br />
    <textarea name="newbody" rows="35" style="width: 95%"></textarea><br /><br />
  </td>
  <td valign="top" width="30%">
  </td>
</tr>
<tr>
</tr>
<tr>
<td align="right" colspan="2">
<input type="hidden" name="action" value="createentry" />
save <input type="image" src="images/mmbase-ok.gif" />
</form>
&nbsp;&nbsp;&nbsp;cancel <a href="<mm:url page="index.jsp" referids="main,weblogid" />"><img src="images/mmbase-cancel.gif" border="0"></a>
</tr>
</table>
</mm:cloud>
