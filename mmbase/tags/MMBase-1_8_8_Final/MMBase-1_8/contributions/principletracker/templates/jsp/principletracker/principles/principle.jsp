
<mm:import externid="principleid" />
<mm:import externid="principlerelid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="principles/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<mm:node referid="principleid">
<tr>
  <th><a href="<mm:url page="index.jsp" referids="main,principleset" />"><img src="images/mmbase-left.gif" border="0" align="left"></a> Principle description</th>
  <th>Principle tinformation</th>
</tr>
<tr>
  <td align="left" width="70%">
    <b>Qualification</b><br />
    <mm:field name="qualification" /><br /><br />

    <b>Principle</b><br />
    <mm:field name="name" /><br /><br />

    <b>Explanation</b><br />
    <mm:field name="explanation" escape="p" /><br /><br />

    <b>Argumentation</b><br />
    <mm:field name="argumentation" escape="p"/><br /></br />

    <b>Consequence</b><br />
    <mm:field name="consequence" escape="p"/><br /></br />

    <b>Allowed Implementation</b><br />
    <mm:field name="allowedimpl" escape="p"/><br /></br />

    <b>Source</b><br />
    <mm:field name="source" escape="p"/><br /></br />
  </td>
  <td  align="left" valign="top" width="30%">
      <b>Number : </b><mm:field name="principlenumber" id="principlenumber" /><br /><br />
      <b>Version : </b><mm:field name="version" /><br /><br />
      <b>State : </b><mm:node referid="principlerelid"><mm:field name="state" /></mm:node><br /><br />
      <b>Theme's : </b><br /><mm:field name="theme" /><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Links : </b><br />No links available for this principle<br /><br />
      <b><font color="white">___________________________________</font></b><br />
      <b>Versions : </b><br />
	<mm:listcontainer path="principlesets,principlerel,principle" fields="principle.principlenumber,principle.name,principlerel.state">
        <mm:constraint field="principlesets.number" operator="EQUAL" value="$principleset" />
	<mm:constraint field="principle.principlenumber" operator="EQUAL" value="$principlenumber" />
	 <mm:list>
	   <a href="<mm:url page="index.jsp" referids="main,sub,principleset"><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><mm:field name="principle.version" />
	   (<mm:field name="principlerel.state" />)</a><br />
	 </mm:list>
	  <mm:size><mm:compare value="0">No other versions found<br /><br /></mm:compare></mm:size>
        </mm:listcontainer>
      <b><font color="white">___________________________________</font></b><br />
      <b>Discussion : </b><br />Not linked to a discussion site yet<br />
  </td>
</tr>
</mm:node>
<tr>
</tr>
<tr>
	<td align="right" colspan="2">update <a href="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset"><mm:param name="sub">editprinciple</mm:param></mm:url>"><img src="images/mmbase-edit.gif" border="0"></a>&nbsp;&nbsp;&nbsp;delete <a href="<mm:url page="index.jsp" referids="main,principleid,principlerelid,principleset"><mm:param name="sub">deleteprinciple</mm:param></mm:url>"><img src="images/mmbase-delete.gif" border="0"></a></td>
</tr>
</table>
