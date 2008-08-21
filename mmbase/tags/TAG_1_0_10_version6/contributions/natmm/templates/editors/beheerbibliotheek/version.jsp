<%@page import="java.text.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Versiebeheer</title>
</head>
<body style="overflow:auto;">
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%
String node = request.getParameter("node");
String c="original_node=" + node;
%>
<h1>Versiebeheer</h1>
<mm:listnodes type="archief" constraints="<%= c %>" orderby="number" directions="DOWN">
   <mm:first>
      Klik op de versie van "<mm:node number="<%= node %>"><mm:field name="titel" /></mm:node>" die u terug wilt zetten.<br/>
      <b>Let op:</b> De inhoud van deze versie overschrijft de huidige inhoud!<br/><br/>
      <table class="formcontent">
      <mm:import id="version_found" />
      <tr><th>Datum</th></tr>
   </mm:first>
   <tr>
      <td>
      <mm:field name="datum" jspvar="date" vartype="String">
         <mm:isnotempty>
            <a href="RestoreAction.eb?node=<mm:field name='number'/>"><mm:time time="<%=date%>" format="dd-MM-yyyy"/></a>
         </mm:isnotempty>
      </mm:field>
      </td>
   </tr>
   <mm:last></table></mm:last>
</mm:listnodes>
<mm:notpresent referid="version_found">
   Er is geen opgeslagen versie van deze "<mm:node number="<%= node %>"><mm:field name="titel" /></mm:node>".
</mm:notpresent>
<br/>
<br/>
<img src="<mm:url page="<%= editwizard_location %>"/>/media/cancel.gif" border='0' onClick="history.back()"/>
</mm:cloud>
</body>
</html>