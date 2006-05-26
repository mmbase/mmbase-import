<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<mm:import externid="a">none</mm:import>

<html>
<head>
<title>MMBase kicker</title>
<link rel="stylesheet" type="text/css" href="../css/editorstyle.css">
</head>
<body style="overflow:auto;padding:5px;">
	<mm:compare referid="a" value="none">
		Please select the process you want to run:<br/>
		<a href="kicker.jsp?a=unuseditems">UpdateUnusedElements</a><br/>
		<a href="kicker.jsp?a=csvreader">CSVReader</a><br/>
		<a href="kicker.jsp?a=eventnotifier">EventNotifier</a><br/>
	</mm:compare>
	<mm:compare referid="a" value="unuseditems">
		<% (new nl.leocms.content.UpdateUnusedElements()).run(); %>
	</mm:compare>
	<mm:compare referid="a" value="csvreader">
		<% (new nl.mmatch.CSVReader()).run(); %>
	</mm:compare>
	<mm:compare referid="a" value="eventnotifier">
		<% (new nl.leocms.evenementen.EventNotifier()).run(); %>
	</mm:compare>
</body>
</html>
</mm:cloud>
