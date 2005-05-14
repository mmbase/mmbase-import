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
<mm:node number="$user">
  <mm:import id="userfname"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:import>
  <mm:import id="from"><mm:field name="email"/></mm:import>
</mm:node>

<mm:import id="link" jspvar="linktofeedback"><%= request.getRequestURL() 
    %>?provider=<mm:write referid="provider"/>&command=getinvite&nfeedback=<mm:write referid="newfeedback"/></mm:import>

<mm:node number="$currentcomp">
  <mm:import id="compname"><mm:field name="name"/></mm:import>
</mm:node>

<%-- some sending email code--%>
<mm:import id="subject">Uitnodiging om feedback te geven.</mm:import>
<mm:import id="body"><HTML>
Beste <mm:write referid="inviteefname"/><br/>
<br/>
<b><mm:write referid="userfname"/></b> heeft je uitgenodigd om zijn competentie <b><mm:write referid="compname"/></b> te beoordelen.<br/>
<br/>
<%= querytext.replaceAll("\\n", "<br/>") %><br/>
<br/>
Je kunt je beoordeling geven op <a href="<%= linktofeedback %>">link</a>.<br/>
<br/>
Bij voorbaat dank.</HTML></mm:import>
<%@include file="sendmail.jsp" %>