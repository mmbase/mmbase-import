<mm:import externid="ca" from="cookie" />
<mm:import externid="cw" from="cookie" />

<%-- should become
<mm:present referid="ca">
  <mm:present referid="cw">
    <mm:cloud method="sessionlogon" username="$ca" password="$cw">
      .. determin 'user' object.
    </mm:cloud>
  </mm:present>
</mm:present>
--%>

<%-- is --%>
<mm:present referid="ca">
  <mm:present referid="cw">
    <mm:listnodes type="users" constraints="account='$ca' and password='$cw'" max="1">
      <mm:field id="user" name="number" write="false" />
    </mm:listnodes>
  </mm:present>
</mm:present>
<mm:present referid="user">
  <mm:list path="users,groups" nodes="$user" constraints="groups.name='BugTrackerCommitors'" max="1">
    <mm:field id="commitor" write="false" name="users.number" />
  </mm:list>
</mm:present>