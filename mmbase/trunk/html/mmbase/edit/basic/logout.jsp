<%@ include file="page_base.jsp" 
%><mm:content type="text/html" language="$config.lang" expires="0">
<mm:cloud jspvar="cloud" sessionname="$config.session" method="asis">
  <mm:import id="username"><%= "" + cloud.getUser().getIdentifier() %></mm:import>
</mm:cloud>
<mm:notpresent referid="username">
  <mm:import id="username">--</mm:import>
</mm:notpresent>
<mm:compare referid="username" value="anonymous">
  <mm:remove referid="username" />
  <mm:import id="username" />
</mm:compare>

<mm:cloud method="logout" sessionname="$config.session" jspvar="cloud" />
<mm:write referid="style" />
<title>Logging out</title>
</head>
<mm:log />
<body class="basic"><% 
request.getSession().invalidate(); // start all over again %>
<h2>You were logged out. </h2>
<hr />
<form action="<mm:url page="search_node.jsp" />">
  <input type="submit" name="back" value="back to editors" />
  As: <input type="text" name="username" value="<mm:write referid="username" />" />
</form>
<%@ include file="footfoot.jsp"  %>
</mm:content>