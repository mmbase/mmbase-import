<% String title = "Configure"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">
<mm:import jspvar="savethis" externid="savethis" />
<mm:import jspvar="ntype" externid="ntype" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>
<% // Save them
if (savethis != null && savethis.equals("Save")) {
	session.setAttribute("conf_max",conf_max);		// Save in session 
	session.setAttribute("conf_days",conf_days); 
	session.setAttribute("conf_list",conf_list); 
}
%>

<h2>Configure my_editors</h2>

<p>Here you can configure the following preferences:</p>
<ul>
  <li>maximum number of items per page that will be shown after a search;</li>
  <li>the maximum age in days of the items that will be found; and</li>
  <li>if you want all the node types to be shown or only the ones you are allowed to edit.</li>
</ul>

<%
if (session.getAttribute("conf_max") != null 
	&& session.getAttribute("conf_days") != null 
	&& session.getAttribute("conf_list") != null) {
%>
<p class="message">You have saved the following settings<br />
<%
	out.println("<br />Maximum age in days: <b>" + session.getAttribute("conf_days").toString() + "</b>");
	out.println("<br />Maximum number of items shown: <b>" + session.getAttribute("conf_max").toString() + "</b>");
	out.println("<br />Show me <b>" + session.getAttribute("conf_list").toString() + "</b> node types in the list");
%>
</p>
<%
}
%>
<p>Your configuration will be saved for the duration of your browser session. 
You can change back to the default settings by choosing <a href="logout.jsp">log out</a>.
If you want to make permanent changes you will have to edit the file 'inc_head.jsp' of my_editors.
</p>

<form method="post" action="<mm:url />">
<table border="0" cellspacing="0" cellpadding="4" class="table-form">
<tr bgcolor="#CCCCCC">
  <td>&nbsp;</td>
  <td class="title-s">Configure my_editors</td>
</tr><tr valign="top">
  <td align="right" class="name">Max days old</td>
  <td><input type="text" name="conf_days" value="<%= conf_days %>" size="9" maxlength="9" /></td>
</tr><tr valign="top">
  <td align="right" class="name">Max items per page</td>
  <td><input type="text" name="conf_max" value="<%= conf_max %>" size="9" maxlength="9" /></td>
</tr><tr valign="top">
  <td align="right" class="name">Show me</td>
  <td>
    <select name="conf_list">
    <option label="all the node types" value="all"<mm:compare referid="conf_list" value="all"> selected="selected"</mm:compare>></option>
    <option label="only the editable node types" value="editable"<mm:compare referid="conf_list" value="editable"> selected="selected"</mm:compare>></option>
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
