<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0" type="text/html" encoding="UTF-8" escaper="entities">
<mm:cloud jspvar="cloud" name="mmbase">

<mm:import externid="action" />
<mm:compare referid="action" value="import">
  <%
  String [] args = { "C:/data/didactor/webapps/appro/WEB-INF/classes/noise" };
  (new nl.didactor.utils.importer.noise.Importer()).main(args); 
  %>
  accounts have been imported.
<br/><br/>
</mm:compare>
<a href="?action=import">import accounts from /WEB-INF/classes/noise</a>
</mm:cloud>
</mm:content>
