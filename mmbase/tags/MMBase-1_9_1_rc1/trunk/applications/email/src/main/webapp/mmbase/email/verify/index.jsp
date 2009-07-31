<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content language="nl" expires="0">
<mm:cloud method="delegate" authenticate="class" jspvar="cloud">
  <mm:import externid="signature" jspvar="signature" vartype="string" required="true" />
  <%=org.mmbase.datatypes.VerifyEmailProcessor.validate(cloud, signature)%>
</mm:cloud>
</mm:content>

