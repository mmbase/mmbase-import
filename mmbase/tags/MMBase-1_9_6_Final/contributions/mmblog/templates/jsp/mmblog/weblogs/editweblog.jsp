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
  <th><a href="<mm:url page="index.jsp" referids="main" />"><img src="images/mmbase-left.gif" align="left" border="0"></a> Weblog description </th>
</tr>
<tr>
  <td>
    <b>Name</b><br />
    <input name="newname" size="40" value="<mm:field name="name" />" /><br /><br />
    <b>Description</b><br />
    <textarea name="newbody" rows="5" cols="70"><mm:field name="body" /></textarea><br /><br />
    <b>Author</b><br />
    <input name="newauthor" size="40" value="<mm:field name="author" />" /><br /><br />
    <b>Author biography</b><br />
    <textarea name="newbio" rows="5" cols="70"><mm:field name="bio" /></textarea><br /><br />
    <b>Alias</b><br />
    <input name="newalias" size="40" value="<mm:aliaslist><mm:write /></mm:aliaslist>" /><br /><br />
  </td>
</tr>
</mm:node>
<tr>
</tr>
<tr>
<td align="right" colspan="2">
<input type="hidden" name="action" value="updateweblog" />
save <input type="image" src="images/mmbase-ok.gif" />
</form>
&nbsp;&nbsp;&nbsp;cancel <a href="<mm:url page="index.jsp" referids="main" />"><img src="images/mmbase-cancel.gif" border="0"></a>
</td>
</tr>
</table>
</mm:cloud>
