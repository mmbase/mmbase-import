<%@ include file="page_base.jsp"  
%><mm:cloud jspvar="cloud" sessionname="${SESSION}" method="http">
  <mm:import id="username"><%= cloud.getUser().getIdentifier() %></mm:import>
</mm:cloud>
<mm:cloud method="logout" sessionname="${SESSION}" jspvar="cloud">
<mm:write referid="style" />
<title>Logging out</title>
</head>
<body class="basic"><% 
request.getSession().invalidate(); // start all over again %>
<h2>You were logged out. </h2>
<hr />
<form action="<mm:url page="search_node.jsp" />">
  <input type="submit" name="back" value="back to editors" />
  As: <input type="text" name="username" value="<mm:write referid="username" />" />
</form>
<%@ include file="foot.jsp"  %>
</mm:cloud>
