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
    %>?provider=<mm:write referid="provider"/>&popcmd=getinvite&nfeedback=<mm:write referid="newfeedback"/></mm:import>

<mm:node number="$currentcomp">
  <mm:import id="compname"><mm:field name="name"/></mm:import>
</mm:node>

<%-- some sending email code--%>
<mm:import id="subject"><di:translate key="pop.sendinvitesubject" /></mm:import>
<mm:import id="htmlbody"><HTML>
<di:translate key="pop.sendinvitepart1" /> <mm:write referid="inviteefname"/><br/>
<br/>
<b><mm:write referid="userfname"/></b> <di:translate key="pop.sendinvitepart2" /> <b><mm:write referid="compname"/></b> <di:translate key="pop.sendinvitepart3" /><br/>
<br/>
<%= querytext.replaceAll("\\n", "<br/>\n") %><br/>
<br/>
<di:translate key="pop.sendinvitepart4" /> <a href="<%= linktofeedback %>"><di:translate key="pop.sendinvitepart5" /></a><di:translate key="pop.sendinvitepart6" /><br/>
<br/>
<di:translate key="pop.sendinvitepart7" /></HTML>
</mm:import>
<mm:import id="body">
<di:translate key="pop.sendinvitepart1" /> <mm:write referid="inviteefname"/>

<mm:write referid="userfname"/> <di:translate key="pop.sendinvitepart2" /> <mm:write referid="compname"/> <di:translate key="pop.sendinvitepart3" />
<%= querytext %>

<di:translate key="pop.sendinvitepart4" /> <%= linktofeedback %> <di:translate key="pop.sendinvitepart5" /><di:translate key="pop.sendinvitepart6" />

<di:translate key="pop.sendinvitepart7" />
</mm:import>
<%@include file="sendmail.jsp" %>
