<mm:import externid="nfeedback"/>
<mm:import jspvar="feedback1" externid="feedback1"/>
<mm:import jspvar="feedback2" externid="feedback2"/>
<mm:import jspvar="rating" externid="rating">-1</mm:import>
<% boolean isSuccess = false; %>
<mm:remove referid="thisfeedback"/>
<mm:node number="$nfeedback" id="thisfeedback">
  <mm:field name="status">
    <mm:compare value="0">
      <% isSuccess = true; %><html  >
      <mm:setfield>-1</mm:setfield>
      <mm:setfield name="rank"><mm:write referid="feedback1"/></mm:setfield>
      <mm:setfield name="text"><mm:write referid="feedback2"/></mm:setfield>
      <mm:related path="people">
        <mm:import id="inviteefname"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></mm:import>
        <mm:import id="from"><mm:field name="people.email"/></mm:import>
      </mm:related>
      <% boolean isNoRating = true; %>
      <mm:related path="related,ratings">
        <mm:node element="related">
          <mm:maydelete>
            <mm:deletenode deleterelations="true"/>
          </mm:maydelete>
        </mm:node>
      </mm:related>
      <mm:compare referid="rating" value="-1" inverse="true">
        <mm:createrelation role="related" source="thisfeedback" destination="rating" />
      </mm:compare>
      <mm:related path="pop,people">
        <mm:import id="userfname"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></mm:import>
        <mm:import id="to"><mm:field name="people.email"/></mm:import>
      </mm:related>
      <mm:related path="competencies">
        <mm:import id="compname"><mm:field name="competencies.name"/></mm:import>
      </mm:related>
    </mm:compare>
  </mm:field>
</mm:node>

<% if (isSuccess) { %>

<%-- some sending email code--%>
<mm:import id="ratingmsg"></mm:import>
<mm:node number="rating" notfound="skip">
  <mm:import id="ratingmsg" reset="true"><fmt:message key="SendFeedbackPart6"/> <mm:field name="name"/><br/></mm:import>
</mm:node>

<mm:import id="subject"><fmt:message key="SendFeedbackSubject"/></mm:import>
<mm:import id="body"><HTML>
<fmt:message key="SendFeedbackPart1"/> <b><mm:write referid="userfname"/>,<br/>
<br/>
<fmt:message key="SendFeedbackPart2"/> <b><mm:write referid="compname"/></b> <fmt:message key="SendFeedbackPart3"/> <b><mm:write referid="inviteefname"/></b> 
<fmt:message key="SendFeedbackPart4"/>
<br/>
<fmt:message key="SendFeedbackPart5"/> "<%= feedback1.replaceAll("\\n", "<br/>") %>"<br/>
<%= feedback2.replaceAll("\\n", "<br/>") %><br/>
<br/>
<mm:write referid="ratingmsg"/>
</HTML></mm:import>
<%@include file="sendmail.jsp" %>

<p><fmt:message key="MsgSendFeedbackDonePart1"/> <b><mm:write referid="compname"/></b> <fmt:message key="MsgSendFeedbackDonePart2"/> <b><mm:write referid="userfname"/></b><fmt:message key="MsgSendFeedbackDonePart3"/></p>

<% } %>