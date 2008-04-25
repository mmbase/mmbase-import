<mm:cloud rank="basic user">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="principles/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:import externid="principleid" />
<mm:import externid="principlerelid" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<form action="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset" ><mm:param name="sub">principle</mm:param></mm:url>" method="post">
<mm:node referid="principleid">
<tr>
  <th><a href="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset" ><mm:param name="sub">principle</mm:param></mm:url>"><img src="images/mmbase-left.gif" align="left" border="0"></a> Principle description</th>
  <th>Principle information</th>
</tr>
<tr>
  <td align="left" width="70%">
    <b>Qualification</b><br />
    <select name="newqualification"><mm:field name="qualification"><mm:compare value="Binding"><option>Binding<option>Strong rule<option>Light rule</mm:compare><mm:compare value="Strong rule"><option>Binding<option selected>Strong rule<option>Light rule</mm:compare><mm:compare value="Light rule"><option>Binding<option>Strong rule<option selected>Light rule</mm:compare></mm:field></select><br /><br />

    <b>Principle</b><br />
    <textarea name="newname" rows="5" style="width: 95%"><mm:field name="name" /></textarea><br /><br />

    <b>Explanation</b><br />
    <textarea name="newexplanation" rows="10" style="width: 95%"><mm:field name="explanation" /></textarea><br /><br />

    <b>Argumentation</b><br />
    <textarea name="newargumentation" rows="10" style="width: 95%"><mm:field name="argumentation" /></textarea><br /></br />

    <b>Consequence</b><br />
    <textarea name="newconsequence" rows="10" style="width: 95%"><mm:field name="consequence" /></textarea><br /></br />

    <b>Allowed Implementation</b><br />
    <textarea name="newallowedimpl" rows="4" style="width: 95%"><mm:field name="allowedimpl" /></textarea><br /></br />

    <b>Source</b><br />
    <textarea name="newsource" rows="2" style="width: 95%"><mm:field name="source" /></textarea><br /></br />
  </td>
  <td align="left" valign="top" width="30%">
      <b>Number : </b><input name="newprinciplenumber" size="4" value="<mm:field name="principlenumber" />" /><br /><br />
       <mm:import id="version"><mm:field name="version" /></mm:import>
      <b>Version : </b><input name="newversion" size="6" value="<mm:field name="version" />" /> autoversion <input type="checkbox" name="autoversion" value="true" checked /> <mm:function set="principletracker" name="getNextPatchLevel" referids="version" /><br /><br />
      <b>State : </b><select name="newstate"><mm:node referid="principlerelid"><mm:field name="state"><mm:compare value="active"><option selected>active<option>archived</mm:compare><mm:compare value="archived"><option selected>archived<option>active</mm:compare></mm:field></mm:node></select><br /><br />
      <b>Theme's : </b><br /><textarea name="newtheme" rows="3"  style="width: 95%"><mm:field name="theme" /></textarea><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Links : </b><br />No links available for this principle<br /><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Versions : </b><br />No other versions found<br /><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Discussion : </b><br />Not linked to a discussion site yet<br />
  </td>
</tr>
</mm:node>
<tr>
</tr>
<tr>
<td align="right" colspan="2">
<input type="hidden" name="action" value="updateprinciple" />
save <input type="image" src="images/mmbase-ok.gif" />
&nbsp;&nbsp;&nbsp;cancel <a href="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset" ><mm:param name="sub">principle</mm:param></mm:url>"><img src="images/mmbase-cancel.gif" border="0"></a>
</form>
</td>
</tr>
</table>
</mm:cloud>
