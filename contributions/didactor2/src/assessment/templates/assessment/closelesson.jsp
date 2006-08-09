<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="includes/getlesson.jsp" %>
  <mm:node number="<%=currentLesson %>" notfound="skip" id="this_lesson">
    <mm:createrelation role="classrel" source="user" destination="this_lesson" id="this_classrel" />
    <mm:maycreate type="popfeedback">
      <mm:createnode type="popfeedback" id="this_feedback">
        <mm:setfield name="status">0</mm:setfield>
      </mm:createnode>
      <mm:createrelation role="related" source="this_classrel" destination="this_feedback"/>
    </mm:maycreate>

  </mm:node>
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>

</mm:cloud>
</mm:content>
