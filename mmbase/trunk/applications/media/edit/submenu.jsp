<mm:context>
  <mm:import externid="current">search</mm:import>
  <mm:import externid="dir"></mm:import>
  
<mm:write referid="current">
<p class="menu">
  <a target="content" href="<mm:url page="${dir}help_${config.lang}.jsp" />"><%=m.getString("help")%></a>
  |      
  <a <mm:compare value="search">class="current"</mm:compare> href="<mm:url page="${dir}search.jsp" />"><%=m.getString("results")%></a>
<% if (org.mmbase.security.Rank.getRank(cloud.getUser().getRank()).compareTo(org.mmbase.security.Rank.getRank("basic user")) >= 0) { %>
  |
  <a <mm:compare value="edit">class="current"</mm:compare> href="<mm:url page="${dir}poolselector.jsp" />"><%=m.getString("edit")%></a>
<% if (org.mmbase.security.Rank.getRank(cloud.getUser().getRank()).compareTo(org.mmbase.security.Rank.getRank("editor")) >= 0) { %>
  |
  <a <mm:compare value="security">class="current"</mm:compare> href="<mm:url page="${dir}security.jsp" />"><%=m.getString("security")%></a>
<% 
  }
} %>
  |
  <a <mm:compare value="config">class="current"</mm:compare> href="<mm:url page="${dir}config/" />"><%=m.getString("config")%></a>
</mm:write>
</p>
</mm:context>