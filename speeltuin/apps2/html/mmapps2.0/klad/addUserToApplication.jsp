<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   Provider provider = am.getProvider();
   String application=request.getParameter("application");
   String user=request.getParameter("user");
   provider.addUserToApplication(application,user);
%>
<!-- waarom werkt hier de forward niet 
<jsp:forward page="providerinfo.jsp" />
-->
