<mm:import externid="ca" from="cookie,session"/>
<mm:import externid="cw" from="cookie,session" />

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
    <mm:listnodescontainer type="users">
     <mm:constraint field="account" referid="ca"/>
     <mm:constraint field="password" referid="cw"/>
     <mm:maxnumber value="1"/>
     <mm:listnodes >
      <mm:field id="user" name="number" write="false" />
      <mm:write request="user"  />
    </mm:listnodes>
    </mm:listnodescontainer>
  </mm:present>
</mm:present>
<mm:present referid="user">
  <mm:list path="users,groups" nodes="$user" constraints="groups.name='BugTrackerCommitors'" max="1">
    <mm:field id="commitor" write="false" name="users.number"  />
    <mm:write request="commitor" />
  </mm:list>
</mm:present>
