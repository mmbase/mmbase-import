<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*,java.util.ArrayList" %>

<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<html>
<head>
  <style>
    table {
     border-collapse: collapse;
     border-color: #000000;
    }
    th, td {
     border-color: #000000;
    }
  </style>
  <title>Overzicht van verzonden email</title>
</head>
<body>
<h3>Overzicht van verzonden email</h3>
<mm:listnodes type="emails">
  <mm:first>
    <table cellpadding="1" cellspacing="0" border="1">
     <tr>
      <th style="width:15%">bericht</th>
      <th style="width:10%">van</th>
      <th style="width:10%">aan</th>
      <th style="width:15%">tijdstip</th>
      <th style="width:50%">onderwerp</th>
     </tr>
  </mm:first>
  <tr>
    <td><mm:field name="subject"/></td>
    <td><mm:field name="from"/></td>
    <td><mm:field name="to"/></td>
    <td><mm:field name="date" jspvar="date" vartype="String" write="false"><mm:time time="<%=date%>" format="dd-MM-yyyy hh:mm"/></mm:field></td>
    <td><mm:field name="body"/></td>
  </tr>
  <mm:last>
    </table>
  </mm:last>
</mm:listnodes>
</body>
</html>
</mm:cloud>

