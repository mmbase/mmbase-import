<mm:cloud rank="basic user">
<mm:import externid="principleid" />
<mm:import externid="principlerelid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="principles/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="65%">
<mm:node referid="principleid">
<form action="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset" />" method="post">
<input name="action" type="hidden" value="deleteprinciple" />
<tr>
  <th colspan="2"><a href="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset"><mm:param name="sub">principle</mm:param></mm:url>"><img src="images/mmbase-left.gif" align="left" border="0"></a> Delete principle</th>
</tr>
<tr>
  <td align="center" width="50%" colspan="2">
    <br />
    <b>Do you really want to delete this principle version ?</b><br />
    <br />
    <table>
      <tr><td><b>number</b></td><td><mm:field name="principlenumber" /></td></tr>
      <tr><td><b>principle</b></td><td><mm:field name="name" /></td></tr>
      <tr><td><b>version</b></td><td><mm:field name="version" /></td></tr>
      <tr><td><b>delete all versions</b></td><td><input type="checkbox" name="deleteallversions" value="true" /></td></tr>
      <tr><td><b>delete older versions</b></td><td><input type="checkbox" name="deleteolderversions" value="true" /></td></tr>
      <tr><td><b>delete older patchlevels</b></td><td><input type="checkbox" name="deleteolderpatchlevels" value="true" /></td></tr>
    </table>
    <br />
    <br />
  </td>
</tr>
</mm:node>
<tr>
	<td align="center">yes delete <input type="image" src="images/mmbase-delete.gif"/></td>
	</form>
	<td align="center">no cancel <a href="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset"><mm:param name="sub">principle</mm:param></mm:url>"><img src="images/mmbase-cancel.gif" border="0"></a></td>
</tr>
</table>
</mm:cloud>
