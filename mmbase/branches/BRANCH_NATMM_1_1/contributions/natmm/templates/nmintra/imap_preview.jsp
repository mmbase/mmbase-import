<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<head>
   <title></title>
   <meta http-equiv="imagetoolbar" content="no">
</head>
<body style="margin-top: 0px; margin-left: 0px;">
   <% String readmoreUrl = "ipoverview.jsp?p=" + paginaID + "&article="; %>
   <%@include file="includes/imap/relatedimap.jsp" %>
</body>
</html>		       
</mm:cloud>