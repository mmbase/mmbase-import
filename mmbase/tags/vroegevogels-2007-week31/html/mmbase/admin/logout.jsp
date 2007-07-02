<mm:cloud method="logout" />
<% 
session.invalidate();
response.sendRedirect("login.jsp"); 
%>
