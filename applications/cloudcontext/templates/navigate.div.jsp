<mm:context>
 <mm:import externid="current" from="parent">none</mm:import>
 <div id="navigate">
  <mm:write referid="current">
   <p <mm:compare value="users">class="current"</mm:compare>>
      <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_users.jsp</mm:param></mm:url>">Users</a></p>
   <p <mm:compare value="groups">class="current"</mm:compare>>
      <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_groups.jsp</mm:param></mm:url>">Groups</a></p>
   <p <mm:compare value="contexts">class="current"</mm:compare>>
     <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_contexts.jsp</mm:param></mm:url>">Contexts</a></p>
<!--
   <p <mm:compare value="config">class="current"</mm:compare>>
     <a href="<mm:url page="config.jsp" />">Config</a></p> -->
   <p <mm:compare value="help">class="current"</mm:compare>>
     <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">help.jsp</mm:param></mm:url>">Help</a></p>
  </mm:write>
 </div>
</mm:context>
