<mm:remove referid="newfeedback"/>
<mm:import externid="invitee"/>
<mm:import jspvar="querytext" externid="query"/>
<% isEmpty = true; %>
<mm:listcontainer path="pop,popfeedback,people">
  <mm:constraint field="people.number" value="$invitee" operator="EQUAL" />
  <mm:constraint field="popfeedback.status" value="0" />
  <mm:list nodes="$currentpop">
    <mm:field name="popfeedback.number" jspvar="thisFeedback" vartype="String">
      <mm:list nodes="$currentcomp" path="competencies,popfeedback"
          constraints="<%= "popfeedback.number='" + thisFeedback + "'" %>">
        <% isEmpty = false; %>
        <mm:node element="popfeedback" id="newfeedback">
          <mm:setfield name="text"><mm:write referid="query"/></mm:setfield>
        </mm:node>
      </mm:list>
    </mm:field>
  </mm:list>
</mm:listcontainer>
<% if (isEmpty) { %>
  <mm:maycreate type="popfeedback">
    <mm:createnode type="popfeedback" id="newfeedback">
      <mm:setfield name="rank"></mm:setfield>
      <mm:setfield name="text"><mm:write referid="query"/></mm:setfield>
      <mm:setfield name="status">0</mm:setfield>
    </mm:createnode>
  </mm:maycreate>
  <mm:createrelation role="related" source="currentpop" destination="newfeedback" />
  <mm:createrelation role="related" source="currentcomp" destination="newfeedback" />
  <mm:createrelation role="related" source="newfeedback" destination="invitee" />
<% } %>

<mm:node number="$invitee">
  <mm:import id="inviteefname"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:import>
  <mm:import id="to"><mm:field name="email"/></mm:import>
</mm:node>
<mm:node number="$student">
  <mm:import id="userfname"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:import>
  <mm:import id="from"><mm:field name="email"/></mm:import>
</mm:node>

<mm:import id="link" jspvar="linktofeedback"><%= request.getRequestURL() 
    %>?provider=<mm:write referid="provider"/>&command=getinvite&nfeedback=<mm:write referid="newfeedback"/></mm:import>

<mm:node number="$currentcomp">
  <mm:import id="compname"><mm:field name="name"/></mm:import>
</mm:node>

<%-- some sending email code--%>
<mm:import id="subject"><fmt:message key="SendInviteSubject"/></mm:import>
<mm:import id="body"><HTML>
<fmt:message key="SendInvitePart1"/> <mm:write referid="inviteefname"/><br/>
<br/>
<b><mm:write referid="userfname"/></b> <fmt:message key="SendInvitePart2"/> <b><mm:write referid="compname"/></b> <fmt:message key="SendInvitePart3"/><br/>
<br/>
<%= querytext.replaceAll("\\n", "<br/>") %><br/>
<br/>
<fmt:message key="SendInvitePart4"/> <a href="<%= linktofeedback %>"><fmt:message key="SendInvitePart5"/></a><fmt:message key="SendInvitePart6"/><br/>
<br/>
<fmt:message key="SendInvitePart7"/></HTML></mm:import>
<%@include file="sendmail.jsp" %>