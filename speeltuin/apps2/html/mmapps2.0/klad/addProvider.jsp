<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   RemoteProviderManager rpm = am.getRemoteProviderManager();
   String location=request.getParameter("location");
   String userid=request.getParameter("userid");
   String password=request.getParameter("password");
   rpm.addRemoteProvider(location, userid, password);
%>

<%=location%> is added. <A HREF="manageProviders.jsp">Go back</A>
