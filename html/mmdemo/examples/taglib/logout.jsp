<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" 
%><html>
<body>
<mm:cloud method="logout" />
<a href='<%=response.encodeUrl("cloud.jsp")%>'>back to cloud.jsp</a>
</body>
</html>