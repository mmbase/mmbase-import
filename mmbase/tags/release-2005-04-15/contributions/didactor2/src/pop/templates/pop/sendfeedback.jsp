<mm:import externid="nfeedback"/>
<mm:import externid="feedback1"/>
<mm:import jspvar="feedback2" externid="feedback2"/>
<% boolean isSuccess = false; %>
<mm:node number="$nfeedback">
  <mm:field name="status">
    <mm:compare value="0">
      <% isSuccess = true; %>
      <mm:setfield>-1</mm:setfield>
      <mm:setfield name="rank"><mm:write referid="feedback1"/></mm:setfield>
      <mm:setfield name="text"><mm:write referid="feedback2"/></mm:setfield>
      <mm:related path="people">
        <mm:import id="inviteefname"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></mm:import>
        <mm:import id="from"><mm:field name="people.email"/></mm:import>
      </mm:related>
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

<mm:import id="link" jspvar="linktofeedback">http://<mm:write referid="servername"/><mm:treefile page="/pop/index.jsp" 
             objectlist="$includePath" referids="$referids">
         </mm:treefile>&command=getinvite&nfeedback=<mm:write referid="nfeedback"/></mm:import>


<%-- some sending email code--%>
<mm:remove referid="mail1"/>
<mm:createnode type="emails" id="mail1">
  <mm:setfield name="from"><mm:write referid="from"/></mm:setfield>
  <mm:setfield name="to"><mm:write referid="to"/></mm:setfield>
  <mm:setfield name="subject">TODO NB</mm:setfield>
  <mm:setfield name="body"><HTML>
Beste <b><mm:write referid="userfname"/>,<br/>
<br/>
Je hebt de volgende beoordeling over <b><mm:write referid="compname"/></b> van <b><mm:write referid="inviteefname"/></b> ontvangen:
<br/>
<%= feedback2.replaceAll("\\n", "<br/>") %><br/>
</HTML></mm:setfield>
  <mm:setfield name="type">0</mm:setfield>
  <mm:setfield name="date"><%=System.currentTimeMillis()/1000%></mm:setfield> 
</mm:createnode>
<mm:node referid="mail1">
  <mm:setfield name="type">1</mm:setfield>
</mm:node> 

<p>Je beoordeling over <b><mm:write referid="compname"/></b> is verstuurd <b><mm:write referid="userfname"/></b>.</p>

<% } %>