<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="util/nocache.jsp"
%><mm:import externid="username" required="true" />
<mm:import externid="authenticate" required="true" />
<mm:import externid="language">nl</mm:import
><mm:cloud loginpage="login.jsp" username="$username">
  <mm:listnodes type="mmbaseusers" constraints="[username] = '$username'">
    <mm:field name="number"  id="user" write="false" />
  </mm:listnodes>
  <mm:present referid="user">
    <mm:node number="$user">
      <mm:field name="status">
        <mm:compare value="0">
          New account, please change your password.
          <mm:redirect page="password.jsp" />
        </mm:compare>
      </mm:field>
    </mm:node>
    <mm:import externid="change" />
    <mm:notpresent referid="change">
      <mm:redirect page="index.jsp" />
    </mm:notpresent>
    <mm:present referid="change">
      <mm:redirect page="password.jsp" />
    </mm:present>
  </mm:present>
  <mm:present referid="user" inverse="true">
    <mm:content language="$language" postprocessor="reducespace" expires="0">
    <html>
      <head>
        <title>Logon</title>
        <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
        <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
        <link rel="stylesheet" href="css/edit.css" />
      </head>
      <body>
        <div id="content">
          Error: Cannot find user with name <mm:write referid="username" />.
        </div>
      </body>
    </html>
    </mm:content>
  </mm:present>
</mm:cloud>
