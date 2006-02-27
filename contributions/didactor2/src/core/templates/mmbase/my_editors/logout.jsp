<% String title = "Log out"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" method="logout" jspvar="wolk">
<mm:import jspvar="ntype" externid="ntype" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<% request.getSession().invalidate();	// start all over again %>

<p>&nbsp;</p>
<p class="message">You were logged out.</p>
<p>&nbsp;</p>
<table border="0" cellspacing="0" cellpadding="4" class="table-left">
<tr bgcolor="#EFEFEF">
  <td><a href="login.jsp"><img src="img/mmbase-cancel.gif" alt="Log in" width="21" height="20" hspace="4" vspace="4" border="0" /></a></td>
  <td><a href="login.jsp">Log back in again</a></td>
</tr>
</table>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
