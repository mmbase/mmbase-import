<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
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
<mm:import externid="coachmode">false</mm:import>

<mm:compare referid="step" value="cancel">
  <mm:redirect page="/assessment/index.jsp" referids="coachmode?,$referids"/>
</mm:compare>

<mm:compare referid="step" value="save">
  <mm:node number="$feedback_n">
    <mm:setfield name="status">-1</mm:setfield>
    <mm:setfield name="text"><mm:write referid="feedbacktext"/></mm:setfield>
    <mm:createrelation role="related" source="feedback_n" destination="user"/>

    <mm:node number="$user">
      <mm:import id="from"><mm:field name="email"/></mm:import>
    </mm:node>
    <mm:relatednodes type="classrel">
      <mm:field name="number" jspvar="this_classrel" vartype="String" write="false">
        <mm:list path="people,classrel,learnblocks" constraints="<%= "classrel.number=" + this_classrel %>">
          <mm:import id="to"><mm:field name="people.email"/></mm:import>
        </mm:list>
      </mm:field>
    </mm:relatednodes>
    <mm:import id="subject"><di:translate key="assessment.given_feedback_subj" /></mm:import>
    <mm:import id="body"><di:translate key="assessment.given_feedback_body" /> <mm:write referid="feedbacktext"/></mm:import>
    <%@ include file="includes/sendmail.jsp" %>
  </mm:node>

  <mm:redirect page="/assessment/index.jsp" referids="coachmode?,$referids"/>
</mm:compare>

</mm:cloud>
</mm:content>
