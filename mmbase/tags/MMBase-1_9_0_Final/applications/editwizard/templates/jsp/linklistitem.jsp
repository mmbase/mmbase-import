<%@ page errorPage="exception.jsp"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%>
<mm:cloud  rank="basic user">
  <mm:import externid="relationOriginNode" required="true" />
  <mm:import externid="relationRole"  />
  <mm:import externid="relationCreateDir"  />
  <mm:import externid="selected"  vartype="list" required="true" listdelimiter="\|" />
    <mm:node number="${relationOriginNode}" id="sourcenode" />
    <mm:stringlist referid="selected">
      <mm:isnotempty>
        <mm:node number="${_}" id="relatednode" />
        <mm:log>Relating ${sourcenode} -> ${relatednode}</mm:log>
        <mm:compare referid="relationCreateDir" value="source">
          <mm:createrelation role="${relationRole}" source="relatednode" destination="sourcenode" />
        </mm:compare>
        <mm:compare referid="relationCreateDir" value="source" inverse="true">
          <mm:createrelation role="${relationRole}" source="sourcenode" destination="relatednode" />
        </mm:compare>
      </mm:isnotempty>
    </mm:stringlist>
</mm:cloud>
