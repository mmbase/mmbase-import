<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<html>
<head>
<title>MMBase editors (logged on as <%= cloud.getUser().getIdentifier() %>)</title>
<link rel="stylesheet" type="text/css" href="css/editors.css">
</head>
<body>
Deleting inactive employees:
<mm:listnodes type="medewerkers" constraints="importstatus='inactive'">
   <mm:hasrelations inverse="true"><mm:field name="title" /><br/><mm:deletenode/></mm:hasrelations>
</mm:listnodes>
Done.
</body>
</html>
</mm:cloud>
