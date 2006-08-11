<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="includes/variables.jsp" %>
<%@include file="includes/functions.jsp" %>

<mm:import externid="step">-1</mm:import>
<mm:import externid="feedback_n">-1</mm:import>
<mm:import externid="feedbacktext"/>

<mm:compare referid="step" value="cancel">
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

<mm:compare referid="step" value="save">
  <mm:node number="$feedback_n">
    <mm:setfield name="status">-1</mm:setfield>
    <mm:setfield name="text"><mm:write referid="feedbacktext"/></mm:setfield>
    <mm:createrelation role="related" source="feedback_n" destination="user"/>
    <%// mail to student%>
  </mm:node>

  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

</mm:cloud>
</mm:content>
