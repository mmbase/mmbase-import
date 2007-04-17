<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="includes/geteducation.jsp" %>
<%@include file="includes/getlesson.jsp" %>
  <mm:node number="<%=currentLesson %>" notfound="skip" id="this_lesson">
    <mm:createrelation role="classrel" source="user" destination="this_lesson" id="this_classrel" />
    <mm:maycreate type="popfeedback">
      <mm:createnode type="popfeedback" id="this_feedback">
        <mm:setfield name="status">0</mm:setfield>
      </mm:createnode>
      <mm:createrelation role="related" source="this_classrel" destination="this_feedback"/>
    </mm:maycreate>
    
    <mm:node number="$user">
      <mm:import id="fullname"><mm:field name="firstname"/> <mm:field name="suffix"><mm:isnotempty><mm:write/></mm:isnotempty></mm:field> <mm:field name="lastname"/></mm:import>
      <mm:import id="from"><mm:field name="email"/></mm:import>
      <mm:import id="subject"><di:translate key="assessment.give_feedback_subj" /> <mm:write referid="fullname" 
          /> / <di:translate key="assessment.period" /> <%= iLesson+1 %></mm:import>
    </mm:node>
    <mm:import id="body">
      <di:translate key="assessment.give_feedback_body" /> 
      <%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() %>/assessment/givefeedback.jsp?feedback_n=<mm:write referid="this_feedback"/>
    </mm:import>
    <mm:node number="$assessment_education" notfound="skip">
      <mm:related path="classrel,people">
        <mm:node element="people">

          <mm:remove referid="to" />
          <mm:import id="to"><mm:field name="email"/></mm:import>

          <mm:related path="related,roles" constraints="roles.name='teacher'">
            <%@ include file="includes/sendmail.jsp" %>
          </mm:related>
        </mm:node>
      </mm:related>
    </mm:node>
  </mm:node>
  <mm:redirect page="/assessment/index.jsp" referids="$referids">
    <mm:param name="step">lessonclosed</mm:param>
  </mm:redirect>

</mm:cloud>
</mm:content>
