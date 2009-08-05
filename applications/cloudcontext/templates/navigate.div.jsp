<mm:context>
 <mm:import externid="current" from="parent">none</mm:import>
 <div id="navigate">
  <mm:write referid="current">
   <p <mm:compare value="users">class="current"</mm:compare>>
      <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_users.jsp</mm:param></mm:url>"><%=getPrompt(m,"accounts")%></a></p>
   <p <mm:compare value="groups">class="current"</mm:compare>>
      <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_groups.jsp</mm:param></mm:url>"><%=getPrompt(m,"groups")%></a></p>
   <p <mm:compare value="contexts">class="current"</mm:compare>>
     <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_contexts.jsp</mm:param></mm:url>"><%=getPrompt(m,"contexts")%></a></p>
    <mm:write referid="language" vartype="string" jspvar="language">
     <p <mm:compare referid="current" value="help">class="current"</mm:compare>>
       <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">help_<mm:write referid="language" />.jsp</mm:param></mm:url>"><%=getPrompt(m,"help")%></a></p>
    </mm:write>
  </mm:write>
 </div>
</mm:context>
