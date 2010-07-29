<mm:cloud rank="basic user">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="principles/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:node referid="principleset">
  <mm:import id="principleset" reset="true"><mm:field name="number" /></mm:import>
</mm:node>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<form action="<mm:url page="index.jsp" referids="main,sub,principleset" />" method="post">
<tr>
  <th>Principle description</th>
  <th>Principle information</th>
</tr>
<tr>
  <td width="70%">
    <b>Qualification</b><br />
    <select name="newqualification"><option>Binding<option>Strong rule<option>light rule</select><br /><br />

    <b>Principle</b><br />
    <textarea name="newname" rows="5" style="width: 95%"></textarea><br /><br />

    <b>Explanation</b><br />
    <textarea name="newexplanation" rows="10" style="width: 95%"></textarea><br /><br />

    <b>Argumenation</b><br />
    <textarea name="newargumentation" rows="10" style="width: 95%"></textarea><br /></br />

    <b>Consequence</b><br />
    <textarea name="newconsequence" rows="10" style="width: 95%"></textarea><br /></br />

    <b>Allowed Implementation</b><br />
    <textarea name="newallowedimpl" rows="4" style="width: 95%"></textarea><br /></br />

    <b>Source</b><br />
    <textarea name="newsource" rows="2" style="width: 95%"></textarea><br /></br />
  </td>
  <td valign="top" width="30%">
      <b>Number : </b><input name="newprinciplenumber" size="4" value="<mm:function set="principletracker" name="getNextPrincipleNumber" referids="principleset" />" /><br /><br />
      <b>Version : </b><input name="newversion" size="6" value="1.0" /><br /><br />
      <b>State : </b><select name="newstate"><option>active</select><br /><br />
      <b>Theme's : </b><br /><textarea name="newtheme" rows="3"  style="width: 95%"></textarea><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Links : </b><br />No links available for this principle<br /><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Versions : </b><br />No other versions found<br /><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Discussion : </b><br />Not linked to a discussion site yet<br />
  </td>
</tr>
<tr>
</tr>
<tr>
<td align="right" colspan="2">
<input type="hidden" name="action" value="createprinciple" />
save <input type="image" src="images/mmbase-ok.gif" />
</form>
&nbsp;&nbsp;&nbsp;cancel <a href="<mm:url page="index.jsp" referids="main,principleset" />"><img src="images/mmbase-cancel.gif" border="0"></a>
</tr>
</table>
</mm:cloud>
