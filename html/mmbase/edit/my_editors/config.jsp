<% String title = "Configure"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">
<mm:import externid="savethis" />
<mm:import jspvar="ntype" externid="ntype" />
<% String path1 = ntype; %>
<%@ include file="inc_head.jsp" %>

<mm:import externid="max_items"><mm:write referid="conf_max" /></mm:import>
<mm:import externid="max_days"><mm:write referid="conf_days" /></mm:import>
<mm:import externid="type_list"><mm:write referid="conf_list" /></mm:import>
<mm:present referid="savethis">
	<mm:write cookie="my_editors_maxitems" referid="max_items" />
	<mm:write cookie="my_editors_maxdays"  referid="max_days" />
	<mm:write cookie="my_editors_typelist" referid="type_list" />
</mm:present>
<h2>Configure my_editors</h2>

<p>Here you can configure the following preferences:</p>
<ul>
  <li>the maximum age in days of the items that will be found: <b><mm:write referid="max_days" /></b>;</li>
  <li>maximum number of items per page that will be shown after a search: <b><mm:write referid="max_items" /></b>; and</li>
  <li>if you want all the node types to be shown or only the ones you are allowed to edit: <b><mm:write referid="type_list" /></b>.</li>
</ul>

<p>Your preferences are saved in a cookie 'my_editors'. You'll find it in your browsers cookie jar.</p>

<form method="post" action="<mm:url />">
<table border="0" cellspacing="0" cellpadding="4" class="table-form">
<tr bgcolor="#CCCCCC">
  <td>&nbsp;</td>
  <td class="title-s">Configure my_editors</td>
</tr><tr valign="top">
  <td align="right" class="name">Max days old</td>
  <td><input type="text" name="max_days" value="<mm:write referid="max_days" />" size="9" maxlength="9" /></td>
</tr><tr valign="top">
  <td align="right" class="name">Max items per page</td>
  <td><input type="text" name="max_items" value="<mm:write referid="max_items" />" size="9" maxlength="9" /></td>
</tr><tr valign="top">
  <td align="right" class="name">Show me</td>
  <td>
    <select name="type_list">
    <option label="all the node types" value="all"<mm:compare referid="type_list" value="all"> selected="selected"</mm:compare>>all the node types</option>
    <option label="only the editable node types" value="editable"<mm:compare referid="type_list" value="editable"> selected="selected"</mm:compare>>only the editable node types</option>
    </select> in the list
  </td>
</tr><tr>
  <td>&nbsp;</td>
  <td><input type="submit" name="savethis" value="Save" /></td>
</tr>
</table>
</form>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
