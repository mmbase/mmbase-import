<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   Provider provider = am.getProvider();
   String user=request.getParameter("user");
   provider.deleteUser(user);
%>

<jsp:forward page="providerinfo.jsp" />
