<div id="header">
  <a href="index.jsp"><img src="img/mmbase-edit-42.png" alt="" width="42" height="42" /></a>
  <form action="edit_object.jsp" method="post">
    <fieldset>
      <label for="nr">edit node #</label>
      <input type="text" name="nr" value="<mm:present referid="nr"><mm:write referid="nr" /></mm:present>" size="8" maxlength="255" id="nr" tabindex="1" accesskey="E" />
    </fieldset>
  </form>
  <div>
    <h1>my_editors</h1>
    <ul>
      <li><a class="top-links" href="index.jsp">home</a> -</li>
      <li><a class="top-links" href="tools.jsp">tools</a> -</li>
      <li><a class="top-links" href="config.jsp">configure &amp; help</a> -</li>
      <li>logged on as:  <%= cloud.getUser().getIdentifier() %> (rank: <%= cloud.getUser().getRank() %>) -</li>
      <li><a href="logout.jsp">log out</a></li>
    </ul>
  </div>
</div>
<div id="crumbpath">
  <a href="index.jsp">my_editors</a>
  <mm:present referid="ntype">&gt; <a href="<mm:url page="index.jsp" referids="ntype" />">overview <mm:write referid="ntype" /></a></mm:present>
  <mm:present referid="pagetitle">&gt; <mm:write referid="pagetitle" escape="lowercase" /></mm:present>
</div>
