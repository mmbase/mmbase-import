<div id="navigation">
  <mm:write referid="tab">
    <% if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 100) { %>
    <span <mm:compare value="index">class="actief"</mm:compare>><a href="<mm:url page="index.jsp" />">Content</a></span>
    <% } %>
    <% if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 200) { %>
    | <span <mm:compare value="list">class="actief"</mm:compare>><a href="<mm:url page="list.jsp" />">Lists</a></span>
    <% } %>
    <% if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 500) { %>
    | <span <mm:compare value="structure">class="actief"</mm:compare>><a href="<mm:url page="structure.jsp" />">Structure</a></span>
    <% }
       if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 1000) { %>
    | <span <mm:compare value="meta">class="actief"</mm:compare>><a href="<mm:url page="meta.jsp" />">Meta</a></span>
    <% }
       if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 2000) { %>
    | <span <mm:compare value="security">class="actief"</mm:compare>><a href="<mm:url page="security.jsp" />">Security</a></span>
    <% } %>
    <span>You are: <%= cloud.getUser().getIdentifier() %> (<%= cloud.getUser().getRank() %>)</span>
    <span>
      <a href="<mm:url page="login.jsp?logout=true" />">Log off</a> |
      <a href="<mm:url page="password.jsp" />">Password</a>
      <%-- if (Rank.getRank(cloud.getUser().getRank()).compareTo(Rank.getRank("triad")) >= 0) { --%>
      | <a href="<mm:url><mm:param name="refresh">refresh</mm:param></mm:url>">Reload</a>
      <%-- } --%>
    </span>
  </mm:write>
</div>
