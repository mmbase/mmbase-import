<body>
<mm:import externid="logout" />
<mm:present referid="logout">
  <mm:cloud method="logout" />
</mm:present>

<div id="content">
  <h2>Login</h2>
  <mm:import externid="reason">please</mm:import>
  <mm:import externid="exactreason">unknown</mm:import>
  <mm:compare referid="reason" value="failed">
    <p class="failed">
      Login failed. (<mm:write referid="exactreason" />). <br />
      Please try again.
    </p>
  </mm:compare>
  <mm:compare referid="reason" value="rank">
    <p class="failed">
      You do not have suffciient rights. Log in as another user.
    </p>
  </mm:compare>
  <table>
    <form method="post" action="<mm:url page="dologin.jsp" />" >
    <tr><td>Account:</td><td><input type="text" name="username" /></td></tr>
    <tr><td>Password:</td><td><input type="password" name="password" /> (change now: <input type="checkbox" name="change" />)</td></tr>
    <input type="hidden" name="authenticate" value="name/password" />
    <input type="hidden" name="finduser"     value="" />
    <input type="hidden" name="dir"     value="<mm:write referid="dir" />" />
    <tr><td /><td><input type="submit" name="command" value="login" /></td></tr>
 </td></tr>
  </form>
</table>
</div>

<div id="contentkop">
  <p>Login </p><p class="right"><!-- help IE -->&nbsp;</p>
</div>
