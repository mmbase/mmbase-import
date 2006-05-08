<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<% (new nl.mmatch.CSVReader()).run(); %>
</mm:cloud>
