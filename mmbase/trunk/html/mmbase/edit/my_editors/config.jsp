<% String title = "Configure"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<mm:import jspvar="savethis" externid="savethis" />
<% String path1 = "";		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<% // Save them
if (savethis != null && savethis.equals("Save")) {
	session.setAttribute("max_str",max_str); // Save new node nr in session 
	session.setAttribute("dayofs",dayofs); // Save new node nr in session 
}
%>

<h2>Configure my_editors</h2>

<p>Here you can configure the maximum number of items per page that will be shown after a search. And the 
maximum age in days of the items that will be found.</p>

<%
if (session.getAttribute("max_str") != null && session.getAttribute("dayofs") != null) {
%>
<p class="message">You have saved the following settings<br>
<%
	out.println("<br>Maximum age in days: " + session.getAttribute("dayofs").toString());
	out.println("<br>Maximum number of items shown: " + session.getAttribute("max_str").toString());
%>
</p>
<%
}
%>

<p>Your configuration will be saved for the duration of your browser session. 
You can change back to the default settings by choosing <a href="logout.jsp">log out</a>.
If you want to make permanent changes you will have to edit the file 'inc_head.jsp' of my_editors.
</p>


<form method="post" action="config.jsp">
<table border="0" cellspacing="0" cellpadding="4" class="table-form">
<tr bgcolor="#CCCCCC">
  <td colspan="2" class="title-m">Configure my_editors</td>
</tr>
<tr valign="top">
  <td align="right" class="name">Max days old</td>
  <td><input type="text" name="dayofs" value="<%= dayofs %>" size="9" maxlength="9"></td>
</tr>
<tr valign="top">
  <td align="right" class="name">Max items per page</td>
  <td><input type="text" name="max_str" value="<%= max_str %>" size="9" maxlength="9"></td>
</tr>
  <tr><td align="right" colspan="2"><input type="submit" name="savethis" value="Save" /></td></tr>
</table>
</form>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
