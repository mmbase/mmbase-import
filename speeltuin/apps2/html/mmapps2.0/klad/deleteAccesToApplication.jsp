<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   Provider provider = am.getProvider();
   String user=request.getParameter("user");
   String application=request.getParameter("application");
   provider.deleteAccessToApplication(application,user);
%>
<!-- ### waarom wertkt hier de forward niet ? 
<jsp:forward page="providerinfo.jsp" />
-->
