<%@ include file="page_base.jsp"  
%><mm:cloud method="logout" sessionname="${SESSION}" jspvar="cloud">
<mm:write referid="style" />
<title>Logging out</title>
</head>
<body class="basic"><% 
request.getSession().invalidate(); // start all over again %>
<h2>You were logged out. </h2>
<hr />
<a href='<mm:url referids="" page="search_node.jsp" />' >back to editors</a>
<%@ include file="foot.jsp"  %>
</mm:cloud>
