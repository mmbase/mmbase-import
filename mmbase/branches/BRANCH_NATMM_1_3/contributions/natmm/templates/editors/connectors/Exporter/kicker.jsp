<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
<mm:import externid="a">none</mm:import>

<html>
<head>
<title>Exporter kicker</title>
<link rel="stylesheet" type="text/css" href="../../css/editorstyle.css">
</head>
<body style="overflow:auto;padding:5px;">
	<mm:compare referid="a" value="articles">
		<% (new nl.leocms.connectors.Exporter.output.Articles.Exporter()).run(); %>
    Articles are exported.
	</mm:compare>
	<mm:compare referid="a" value="attachments">
		<% (new nl.leocms.connectors.Exporter.output.Attachments.Exporter()).run(); %>
    Atttachments are exported.
	</mm:compare>
	<mm:compare referid="a" value="medewerkers">
		<% (new nl.leocms.connectors.Exporter.output.People.Exporter()).run(); %>
    Employees are exported.
  </mm:compare>
  <br/><br/><br/><br/>
  Please select the process you want to run:<br/>
  <a href="kicker.jsp?a=articles">Export articles</a><br/>
  <a href="kicker.jsp?a=attachments">Export attachments</a><br/>
  <a href="kicker.jsp?a=medewerkers">Export employees</a><br/>
</body>
</html>
</mm:cloud>
