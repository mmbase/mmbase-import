<% String title = "Log out"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" method="logout" jspvar="wolk">
<mm:import jspvar="ntype" externid="ntype" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<% request.getSession().invalidate();	// start all over again %>

<h2>Log out</h2>
<p class="message">You were logged out.</p>
<p><a href="index.jsp">Back to the homepage of my_editors</a></p>

<p>Vreemd, maar dit werkt nog niet goed (op mijn Mac OS X met IE 5.2 dan, en misschien op wel meer masjientjes). 
Nog eens naar kijken...</p>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
