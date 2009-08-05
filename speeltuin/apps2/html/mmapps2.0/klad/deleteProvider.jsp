<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   RemoteProviderManager rpm = am.getRemoteProviderManager();
   String providername=request.getParameter("providername");
   rpm.deleteRemoteProvider(providername);
%>
<%=providername%> is deleted. <A HREF="manageProviders.jsp">Go back</A>
