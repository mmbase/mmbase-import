<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" jspvar="cloud" rank="administrator">
<%
   nl.mmatch.CSVReader test = new nl.mmatch.CSVReader();
   test.run();
%>
</mm:cloud> 
