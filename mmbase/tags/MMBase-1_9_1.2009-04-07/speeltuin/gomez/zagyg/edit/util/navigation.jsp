<div id="navigation">
  <mm:cloudinfo type="rank">
    <mm:isgreaterthan value="99">
      <span <mm:compare referid="tab" value="index">class="actief"</mm:compare>><a href="<mm:url page="index.jsp" />">Content</a></span>
    </mm:isgreaterthan>
    <mm:isgreaterthan value="199">
    | <span <mm:compare referid="tab" value="list">class="actief"</mm:compare>><a href="<mm:url page="list.jsp" />">Lists</a></span>
    </mm:isgreaterthan>
    <mm:isgreaterthan value="499">
    | <span <mm:compare referid="tab" value="structure">class="actief"</mm:compare>><a href="<mm:url page="structure.jsp" />">Structure</a></span>
    </mm:isgreaterthan>
    <mm:isgreaterthan value="999">
    | <span <mm:compare referid="tab" value="meta">class="actief"</mm:compare>><a href="<mm:url page="meta.jsp" />">Meta</a></span>
    | <span <mm:compare referid="tab" value="import">class="actief"</mm:compare>><a href="<mm:url page="di_overview.jsp" />">Import</a></span>
    </mm:isgreaterthan>
    <mm:isgreaterthan value="1999">
    | <span <mm:compare referid="tab" value="security">class="actief"</mm:compare>><a href="<mm:url page="security.jsp" />">Security</a></span>
    </mm:isgreaterthan>
  </mm:cloudinfo>
  <span>You are: <mm:cloudinfo type="user" /> (<mm:cloudinfo type="rank" />)</span>
  <span>
    <a href="<mm:url page="login.jsp?logout=true" />">Log off</a> |
    <a href="<mm:url page="password.jsp" />">Password</a>
    | <a href="<mm:url><mm:param name="refresh">refresh</mm:param></mm:url>">Reload</a>
  </span>
</div>
