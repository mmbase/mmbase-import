<%@ page errorPage="exception.jsp"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%>
<mm:cloud  rank="basic user">
  <mm:import externid="newfromlist" vartype="list" required="true" />
  <mm:import externid="selected"  vartype="list" required="true" listdelimiter="\|" />
    <mm:node number="${newfromlist[0]}" id="sourcenode" />
    <mm:stringlist referid="selected">
      <mm:isnotempty>
        <mm:node number="${_}" id="relatednode" />
        <mm:log>Relating ${sourcenode} -> ${relatednode}</mm:log>
        <mm:createrelation role="${newfromlist[1]}" source="sourcenode" destination="relatednode" />
      </mm:isnotempty>
    </mm:stringlist>
</mm:cloud>
