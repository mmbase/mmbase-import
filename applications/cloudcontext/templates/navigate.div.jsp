<mm:context>
 <mm:import externid="current" from="parent">none</mm:import>
 <div id="navigate">
  <mm:write referid="current">
   <p <mm:compare value="users">class="current"</mm:compare>>
      <a href="<mm:url page="index.jsp" />">Users</a></p>
   <p <mm:compare value="groups">class="current"</mm:compare>>
     <a href="<mm:url page="index_groups.jsp" />">Groups</a></p>
   <p <mm:compare value="contexts">class="current"</mm:compare>>
     <a href="<mm:url page="index_contexts.jsp" />">Contexts</a></p>
   <p <mm:compare value="config">class="current"</mm:compare>>
     <a href="<mm:url page="config.jsp" />">Config</a></p>
   <p <mm:compare value="help">class="current"</mm:compare>>
     <a href="<mm:url page="help.jsp" />">Help</a></p>
  </mm:write>
 </div>
</mm:context>
