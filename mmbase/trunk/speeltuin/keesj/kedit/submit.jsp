<html>
<head>

<title>submit</title>
<link href="css/style.css" rel="stylesheet" type="text/css">
<script language="javascript" src="js/i.js">
</script>
</head>
<body>
<pre>
<% String q = request.getParameter("q");
%>
<jsp:include page="<%= "functions/" +  q +".jsp" %>"/>
</pre>
</body>
</html>
