<%@page import="java.text.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<title>Versiebeheer</title>
</head>
<body>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%
  SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
%>
<h1>Versiebeheer</h1>
Selecteer de versie die u terug wilt zetten. <br>
<b>Let op:</b>De inhoud van deze versie overschrijft de huidige inhoud!<BR><BR>
<table>
<tr><th>Datum</th></tr>
<%
String c="original_node="+request.getParameter("node");
%>
<mm:listnodes type="archief" constraints="<%=c%>" >
<tr>
<td><mm:field name="datum" jspvar="date" vartype="Long">
<a href="RestoreAction.eb?node=<mm:field name='number'/>"><%= sf.format( new java.util.Date(date.longValue()*1000))%></a>
</mm:field>
</td>
</tr>
</mm:listnodes>
</table>
<img src="<mm:url page="<%= editwizard_location %>"/>/media/cancel.gif" border='0' onClick="history.back()"/>
</mm:cloud>
</body>
</html>