<mm:import externid="username" required="true" />
<mm:import externid="password" required="true" />
<mm:import externid="referrer">index.jsp</mm:import>
<mm:import externid="authenticate" required="true" />

<mm:cloud loginpage="login.jsp?referrer=$referrer" username="$username">
  <mm:listnodes type="mmbaseusers" constraints="username = '$username'">
    <mm:field name="number"  id="user" write="false" />
  </mm:listnodes>


  <mm:present referid="user">
    <mm:node number="$user">
      <mm:field name="status">
        <mm:compare value="0">
          New account, please change your password.
          <mm:write referid="user" jspvar="user">
            <% response.sendRedirect(response.encodeRedirectURL("password.jsp")); %>
          </mm:write>
        </mm:compare>
      </mm:field>
    </mm:node>
    <mm:import externid="change" />

    <mm:notpresent referid="change">
      <mm:redirect page="${referrer}" referids="username,password,authenticate,user">
        <mm:param name="command">login</mm:param>
      </mm:redirect>
    </mm:notpresent>
    <mm:present referid="change">
      <mm:redirect page="${dir}edit/password.jsp" referids="username,password,authenticate,user">
        <mm:param name="command">login</mm:param>
      </mm:redirect>
    </mm:present>
  </mm:present>
  <mm:present referid="user" inverse="true">
    <body>
      <div id="content">
        Error: Cannot find user with name <mm:write referid="username" />.
      </div>
    </body>
  </mm:present>
</mm:cloud>