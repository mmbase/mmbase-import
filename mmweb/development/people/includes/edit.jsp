<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="objectnumber" /> 
<mm:import externid="page" /> 
<mm:import externid="portal" /> 
<mm:present referid="objectnumber">
<mm:cloud authenticate="class" method="delegate">
<mm:import id="referrer"
	><mm:url page="/editors/referrer.jsp" referids="portal,page"
		><mm:param name="person"><mm:write referid="objectnumber" /></mm:param
	></mm:url
></mm:import>
<a href="<mm:url page="/mmbase/editwizard/jsp/wizard.jsp?wizard=wizards/persons/persons" referids="objectnumber,referrer" />">click here to edit your profile</a>
</mm:cloud>
</mm:present>
