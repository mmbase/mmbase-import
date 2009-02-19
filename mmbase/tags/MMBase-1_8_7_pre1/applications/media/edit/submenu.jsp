<mm:context>
  <mm:import externid="current">search</mm:import>
  <mm:import externid="dir"></mm:import>
  
<mm:write referid="current">
<p class="menu">
  <a target="content" href="<mm:url page="${dir}help_${config.lang}.jsp" />"><%=m.getString("help")%></a>
  |      
  <a <mm:compare value="search">class="current"</mm:compare> href="<mm:url page="${dir}search.jsp" />"><%=m.getString("results")%></a>
  <mm:hasrank minvalue="basic user">
    |
    <a <mm:compare value="edit">class="current"</mm:compare> href="<mm:url page="${dir}poolselector.jsp" />"><%=m.getString("edit")%></a>
    <mm:hasrank minvalue="project manager">
      |
      <a <mm:compare value="security">class="current"</mm:compare> href="<mm:url page="${dir}security.jsp" />"><%=m.getString("security")%></a>
    </mm:hasrank>
    |
    <a href="<mm:url page="${dir}search.jsp?logout=true" />">Logout</a>
  </mm:hasrank>
  <mm:hasrank maxvalue="anonymous">
    |
    <a <mm:compare value="login">class="current"</mm:compare> href="<mm:url page="${dir}login.jsp" />">Login</a>
  </mm:hasrank>
  |
  <a <mm:compare value="config">class="current"</mm:compare> href="<mm:url page="${dir}config/" />"><%=m.getString("config")%></a>
</p>
</mm:write>
</mm:context>