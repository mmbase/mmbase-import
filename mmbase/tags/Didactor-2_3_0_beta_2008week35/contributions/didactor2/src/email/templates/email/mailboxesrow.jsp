<mm:remove referid="mailboxnumber"/>
<mm:field id="mailboxnumber" name="number" write="false" />

<mm:remove referid="activemailbox"/>
<mm:remove referid="mboxdisplayname" />

<mm:import id="mboxdisplayname"><jsp:include page="mailboxname.jspx" /></mm:import>

<mm:compare referid="mailbox" referid2="mailboxnumber">
  <mm:import id="activemailbox"><b><mm:write referid="mboxdisplayname" /></b></mm:import>
</mm:compare>
<mm:compare referid="mailbox" referid2="mailboxnumber" inverse="true">
  <mm:import id="activemailbox"><mm:write referid="mboxdisplayname" /></mm:import>
</mm:compare>

<mm:import id="newmails" reset="true">0</mm:import>
<mm:relatednodescontainer type="emails">
    <mm:constraint field="type" value="2" operator="=" /> <%-- find new mails --%>
    <mm:import id="newmails" reset="true"><mm:size /></mm:import>
</mm:relatednodescontainer>

<mm:import id="mails" reset="true">0</mm:import>
<mm:relatednodescontainer type="emails">
  <mm:import id="mails" reset="true"><mm:size /></mm:import>
</mm:relatednodescontainer>

<a href="<mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids,mboxdisplayname?@mailboxname">
  <mm:param name="mailbox"><mm:field name="number" /></mm:param>
  <mm:param name="so">down</mm:param>
</mm:treefile>"><mm:write referid="activemailbox" escape="none" /> (<mm:write referid="newmails"/>/<mm:write referid="mails"/>)</a> 
<br />
